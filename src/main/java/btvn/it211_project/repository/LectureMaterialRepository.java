package btvn.it211_project.repository;

import btvn.it211_project.domain.LectureMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureMaterialRepository extends JpaRepository<LectureMaterial, Long> {

    List<LectureMaterial> findByCourseIdAndActiveTrue(Long courseId);

    Page<LectureMaterial> findByCourseId(Long courseId, Pageable pageable);

    Optional<LectureMaterial> findByIdAndActiveTrue(Long id);

    List<LectureMaterial> findByUploadedByIdAndActiveTrue(Long uploadedById);
}
