package btvn.it211_project.dto.response;

import btvn.it211_project.domain.EnrollmentStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EnrollmentResponse {
    Long id;
    Long studentId;
    String studentName;
    Long courseId;
    String courseCode;
    String courseName;
    EnrollmentStatus status;
    LocalDateTime enrolledAt;
}