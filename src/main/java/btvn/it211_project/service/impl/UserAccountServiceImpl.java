package btvn.it211_project.service.impl;

import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.StudentRegistrationRequest;
import btvn.it211_project.dto.request.UserUpsertRequest;
import btvn.it211_project.dto.response.UserResponse;
import btvn.it211_project.exception.DuplicateResourceException;
import btvn.it211_project.exception.ResourceNotFoundException;
import btvn.it211_project.repository.UserAccountRepository;
import btvn.it211_project.service.UserAccountService;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserResponse registerStudent(StudentRegistrationRequest request) {
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        UserAccount user = new UserAccount(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                Role.STUDENT);

        return toResponse(userAccountRepository.save(user));
    }

    @Override
    public UserResponse createUser(UserUpsertRequest request) {
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        UserAccount user = new UserAccount(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                request.getRole());
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        return toResponse(userAccountRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UserUpsertRequest request) {
        UserAccount user = getRequiredUser(id);
        if (!Objects.equals(user.getEmail(), request.getEmail()) && userAccountRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        return toResponse(userAccountRepository.save(user));
    }

    @Override
    public void deactivateUser(Long id) {
        UserAccount user = getRequiredUser(id);
        user.setActive(false);
        userAccountRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toResponse(getRequiredUser(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String keyword, Role role, Boolean active, Pageable pageable) {
        Page<UserAccount> pageResult;

        if (keyword != null && !keyword.isBlank()) {
            pageResult = userAccountRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
        } else if (role != null && active != null) {
            pageResult = userAccountRepository.findByRoleAndActive(role, active, pageable);
        } else if (role != null) {
            pageResult = userAccountRepository.findByRole(role, pageable);
        } else if (active != null) {
            pageResult = userAccountRepository.findByActive(active, pageable);
        } else {
            pageResult = userAccountRepository.findAll(pageable);
        }

        return pageResult.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccount getRequiredUser(Long id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private UserResponse toResponse(UserAccount user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}