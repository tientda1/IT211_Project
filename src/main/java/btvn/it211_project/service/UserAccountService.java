package btvn.it211_project.service;

import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.StudentRegistrationRequest;
import btvn.it211_project.dto.request.UserUpsertRequest;
import btvn.it211_project.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserAccountService {

    UserResponse registerStudent(StudentRegistrationRequest request);

    UserResponse createUser(UserUpsertRequest request);

    UserResponse updateUser(Long id, UserUpsertRequest request);

    void deactivateUser(Long id);

    UserResponse getUserById(Long id);

    Page<UserResponse> searchUsers(String keyword, Role role, Boolean active, Pageable pageable);

    UserAccount getRequiredUser(Long id);
}