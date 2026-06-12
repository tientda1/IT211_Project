package btvn.it211_project.service;

import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.StudentRegistrationRequest;
import btvn.it211_project.dto.response.UserResponse;
import btvn.it211_project.exception.DuplicateResourceException;
import btvn.it211_project.repository.UserAccountRepository;
import btvn.it211_project.service.impl.UserAccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @Test
    public void registerStudent_EmailExists_ThrowsDuplicateResourceException() {
        StudentRegistrationRequest request = new StudentRegistrationRequest();
        request.setEmail("test@gmail.com");
        request.setFullName("Test Student");
        request.setPassword("password123");

        when(userAccountRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userAccountService.registerStudent(request));
        verify(userAccountRepository, never()).save(any(UserAccount.class));
    }

    @Test
    public void registerStudent_ValidRequest_Success() {
        StudentRegistrationRequest request = new StudentRegistrationRequest();
        request.setEmail("student@gmail.com");
        request.setFullName("John Doe");
        request.setPassword("password123");
        request.setPhone("0912345678");

        UserAccount savedUser = new UserAccount("John Doe", "student@gmail.com", "encodedPassword", "0912345678", Role.STUDENT);

        when(userAccountRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(savedUser);

        UserResponse response = userAccountService.registerStudent(request);

        assertNotNull(response);
        assertEquals("student@gmail.com", response.getEmail());
        assertEquals("John Doe", response.getFullName());
        assertEquals(Role.STUDENT, response.getRole());
        verify(userAccountRepository, times(1)).save(any(UserAccount.class));
    }
}
