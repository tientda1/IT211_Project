package btvn.it211_project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmissionRequest {

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotBlank(message = "Submission content (GitHub link or file path) is required")
    private String submissionContent;
}
