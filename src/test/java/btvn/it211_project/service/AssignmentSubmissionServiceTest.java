package btvn.it211_project.service;

import btvn.it211_project.domain.Assignment;
import btvn.it211_project.domain.AssignmentSubmission;
import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.AssignmentSubmissionRequest;
import btvn.it211_project.exception.BusinessRuleException;
import btvn.it211_project.exception.ResourceNotFoundException;
import btvn.it211_project.repository.AssignmentRepository;
import btvn.it211_project.repository.AssignmentSubmissionRepository;
import btvn.it211_project.repository.UserAccountRepository;
import btvn.it211_project.service.impl.AssignmentSubmissionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignmentSubmissionServiceTest {

    @Mock
    private AssignmentSubmissionRepository submissionRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private AssignmentSubmissionServiceImpl submissionService;

    @Test
    public void submitAssignment_StudentNotFound_ThrowsResourceNotFoundException() {
        Long studentId = 999L;
        AssignmentSubmissionRequest request = new AssignmentSubmissionRequest(1L, "https://github.com/repo");

        when(userAccountRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> submissionService.submitAssignment(studentId, request));
        verify(submissionRepository, never()).save(any(AssignmentSubmission.class));
    }

    @Test
    public void submitAssignment_AlreadySubmitted_ThrowsBusinessRuleException() {
        Long studentId = 3L;
        AssignmentSubmissionRequest request = new AssignmentSubmissionRequest(1L, "https://github.com/repo");

        UserAccount student = new UserAccount("Student", "s@it211.local", "pass", "123", Role.STUDENT);
        Assignment assignment = new Assignment();
        assignment.setId(1L);

        when(userAccountRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(submissionRepository.findByAssignmentIdAndStudentId(1L, studentId))
                .thenReturn(Optional.of(new AssignmentSubmission()));

        assertThrows(BusinessRuleException.class, () -> submissionService.submitAssignment(studentId, request));
        verify(submissionRepository, never()).save(any(AssignmentSubmission.class));
    }
}
