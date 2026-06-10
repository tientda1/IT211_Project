package btvn.it211_project.repository;

import btvn.it211_project.domain.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByCourseIdAndActiveTrue(Long courseId);

    Page<Assignment> findByCourseId(Long courseId, Pageable pageable);

    Optional<Assignment> findByIdAndActiveTrue(Long id);
}
