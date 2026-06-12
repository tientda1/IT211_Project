package btvn.it211_project.repository;

import btvn.it211_project.domain.Course;
import btvn.it211_project.domain.Enrollment;
import btvn.it211_project.domain.UserAccount;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentAndCourse(UserAccount student, Course course);

    Optional<Enrollment> findByStudentAndCourse(UserAccount student, Course course);

    List<Enrollment> findByStudentId(Long studentId);

    long countByCourseId(Long courseId);
}