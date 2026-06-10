package btvn.it211_project.service;

import btvn.it211_project.dto.request.AssignmentSubmissionRequest;
import btvn.it211_project.dto.request.GradingRequest;
import btvn.it211_project.dto.response.AssignmentSubmissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssignmentSubmissionService {

    AssignmentSubmissionResponse submitAssignment(Long studentId, AssignmentSubmissionRequest request);

    AssignmentSubmissionResponse gradeSubmission(Long submissionId, Long lecturerId, GradingRequest request);

    AssignmentSubmissionResponse getSubmissionById(Long id);

    List<AssignmentSubmissionResponse> getSubmissionsByAssignment(Long assignmentId);

    List<AssignmentSubmissionResponse> getSubmissionsByStudent(Long studentId);

    Page<AssignmentSubmissionResponse> getSubmissionsByAssignmentPaged(Long assignmentId, Pageable pageable);

    Page<AssignmentSubmissionResponse> getSubmissionsByStudentPaged(Long studentId, Pageable pageable);

    void deleteSubmission(Long id);

    List<AssignmentSubmissionResponse> getPendingSubmissions(Long assignmentId);
}
