package btvn.it211_project.service.impl;

import btvn.it211_project.domain.Assignment;
import btvn.it211_project.domain.AssignmentSubmission;
import btvn.it211_project.domain.SubmissionStatus;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.AssignmentSubmissionRequest;
import btvn.it211_project.dto.request.GradingRequest;
import btvn.it211_project.dto.response.AssignmentSubmissionResponse;
import btvn.it211_project.exception.BusinessRuleException;
import btvn.it211_project.exception.ResourceNotFoundException;
import btvn.it211_project.repository.AssignmentRepository;
import btvn.it211_project.repository.AssignmentSubmissionRepository;
import btvn.it211_project.repository.UserAccountRepository;
import btvn.it211_project.service.AssignmentSubmissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {

    private final AssignmentSubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserAccountRepository userAccountRepository;

    public AssignmentSubmissionServiceImpl(
            AssignmentSubmissionRepository submissionRepository,
            AssignmentRepository assignmentRepository,
            UserAccountRepository userAccountRepository) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public AssignmentSubmissionResponse submitAssignment(Long studentId, AssignmentSubmissionRequest request) {
        UserAccount student = userAccountRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        // Check if already submitted
        if (submissionRepository.findByAssignmentIdAndStudentId(assignment.getId(), studentId).isPresent()) {
            throw new BusinessRuleException("Already submitted this assignment");
        }

        boolean isLate = LocalDateTime.now().isAfter(assignment.getDueDate());

        AssignmentSubmission submission = AssignmentSubmission.builder()
                .assignment(assignment)
                .student(student)
                .submissionContent(request.getSubmissionContent())
                .isLate(isLate)
                .status(SubmissionStatus.SUBMITTED)
                .submissionDate(LocalDateTime.now())
                .build();

        return toResponse(submissionRepository.save(submission));
    }

    @Override
    public AssignmentSubmissionResponse gradeSubmission(Long submissionId, Long lecturerId, GradingRequest request) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        UserAccount lecturer = userAccountRepository.findById(lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found"));

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setLecturerGraded(lecturer);
        submission.setGradedDate(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setUpdatedAt(LocalDateTime.now());

        return toResponse(submissionRepository.save(submission));
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentSubmissionResponse getSubmissionById(Long id) {
        AssignmentSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        return toResponse(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentSubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentSubmissionResponse> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssignmentSubmissionResponse> getSubmissionsByAssignmentPaged(Long assignmentId, Pageable pageable) {
        return submissionRepository.findByAssignmentId(assignmentId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssignmentSubmissionResponse> getSubmissionsByStudentPaged(Long studentId, Pageable pageable) {
        return submissionRepository.findByStudentId(studentId, pageable)
                .map(this::toResponse);
    }

    @Override
    public void deleteSubmission(Long id) {
        AssignmentSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        submissionRepository.delete(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentSubmissionResponse> getPendingSubmissions(Long assignmentId) {
        return submissionRepository.findByAssignmentIdAndStatusIn(assignmentId, List.of(SubmissionStatus.SUBMITTED.name()))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AssignmentSubmissionResponse toResponse(AssignmentSubmission submission) {
        return AssignmentSubmissionResponse.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignment().getId())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getFullName())
                .submissionContent(submission.getSubmissionContent())
                .submissionDate(submission.getSubmissionDate())
                .isLate(submission.isLate())
                .status(submission.getStatus().name())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .lecturerGradedId(submission.getLecturerGraded() != null ? submission.getLecturerGraded().getId() : null)
                .lecturerGradedName(submission.getLecturerGraded() != null ? submission.getLecturerGraded().getFullName() : null)
                .gradedDate(submission.getGradedDate())
                .createdAt(submission.getCreatedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }
}
