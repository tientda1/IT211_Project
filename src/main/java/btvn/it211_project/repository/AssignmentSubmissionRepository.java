package btvn.it211_project.repository;

import btvn.it211_project.domain.AssignmentSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);

    List<AssignmentSubmission> findByStudentId(Long studentId);

    Page<AssignmentSubmission> findByAssignmentId(Long assignmentId, Pageable pageable);

    Page<AssignmentSubmission> findByStudentId(Long studentId, Pageable pageable);

    List<AssignmentSubmission> findByAssignmentIdAndStatusIn(Long assignmentId, List<String> statuses);
}
