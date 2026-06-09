package btvn.it211_project.dto.response;

import btvn.it211_project.domain.Role;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponse {
    Long id;
    String fullName;
    String email;
    String phone;
    Role role;
    boolean active;
    LocalDateTime createdAt;
}