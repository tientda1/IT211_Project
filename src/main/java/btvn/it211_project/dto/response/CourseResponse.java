package btvn.it211_project.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CourseResponse {
    Long id;
    String code;
    String name;
    String description;
    Integer maxStudents;
    boolean active;
    long enrolledCount;
    LocalDateTime createdAt;
}