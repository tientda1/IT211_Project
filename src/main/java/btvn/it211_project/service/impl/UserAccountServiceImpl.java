package btvn.it211_project.service.impl;

import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.StudentRegistrationRequest;
import btvn.it211_project.dto.request.UserUpsertRequest;
import btvn.it211_project.dto.response.UserResponse;
import btvn.it211_project.exception.BusinessRuleException;
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
    private final PasswordEncoder passwordEncoder;

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    @Transactional(readOnly = true)
    public UserAccount getUserByEmail(String email) {
        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        UserAccount user = getRequiredUser(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessRuleException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(user);
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        UserAccount user = getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(user);
    }

    @Override
    public void updateRefreshToken(Long userId, String refreshToken) {
        UserAccount user = getRequiredUser(userId);
        user.setRefreshToken(refreshToken);
        userAccountRepository.save(user);
    }

    @Override
    public void revokeToken(Long userId) {
        UserAccount user = getRequiredUser(userId);
        user.setRefreshToken(null);
        userAccountRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccount findByEmailAndPassword(String email, String password) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        return user;
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