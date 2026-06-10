package btvn.it211_project.service;

import btvn.it211_project.dto.request.LectureMaterialUploadRequest;
import btvn.it211_project.dto.response.LectureMaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LectureMaterialService {

    LectureMaterialResponse uploadMaterial(Long lecturerId, LectureMaterialUploadRequest request);

    LectureMaterialResponse getMaterialById(Long id);

    List<LectureMaterialResponse> getMaterialsByCourse(Long courseId);

    Page<LectureMaterialResponse> getMaterialsByCoursePaged(Long courseId, Pageable pageable);

    LectureMaterialResponse updateMaterial(Long id, LectureMaterialUploadRequest request);

    void deleteMaterial(Long id);

    void deactivateMaterial(Long id);

    List<LectureMaterialResponse> getMaterialsByLecturer(Long lecturerId);
}
