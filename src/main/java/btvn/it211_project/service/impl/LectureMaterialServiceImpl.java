package btvn.it211_project.service.impl;

import btvn.it211_project.domain.Course;
import btvn.it211_project.domain.LectureMaterial;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.dto.request.LectureMaterialUploadRequest;
import btvn.it211_project.dto.response.LectureMaterialResponse;
import btvn.it211_project.exception.ResourceNotFoundException;
import btvn.it211_project.repository.CourseRepository;
import btvn.it211_project.repository.LectureMaterialRepository;
import btvn.it211_project.repository.UserAccountRepository;
import btvn.it211_project.service.LectureMaterialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LectureMaterialServiceImpl implements LectureMaterialService {

    private final LectureMaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final UserAccountRepository userAccountRepository;

    public LectureMaterialServiceImpl(
            LectureMaterialRepository materialRepository,
            CourseRepository courseRepository,
            UserAccountRepository userAccountRepository) {
        this.materialRepository = materialRepository;
        this.courseRepository = courseRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public LectureMaterialResponse uploadMaterial(Long lecturerId, LectureMaterialUploadRequest request) {
        UserAccount lecturer = userAccountRepository.findById(lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        LectureMaterial material = LectureMaterial.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .course(course)
                .uploadedBy(lecturer)
                .fileUrl(request.getFileUrl())
                .fileName(request.getFileName())
                .fileSize(request.getFileSize())
                .fileType(request.getFileType())
                .build();

        return toResponse(materialRepository.save(material));
    }

    @Override
    @Transactional(readOnly = true)
    public LectureMaterialResponse getMaterialById(Long id) {
        LectureMaterial material = materialRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));
        return toResponse(material);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LectureMaterialResponse> getMaterialsByCourse(Long courseId) {
        return materialRepository.findByCourseIdAndActiveTrue(courseId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LectureMaterialResponse> getMaterialsByCoursePaged(Long courseId, Pageable pageable) {
        return materialRepository.findByCourseId(courseId, pageable)
                .map(this::toResponse);
    }

    @Override
    public LectureMaterialResponse updateMaterial(Long id, LectureMaterialUploadRequest request) {
        LectureMaterial material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));

        material.setTitle(request.getTitle());
        material.setDescription(request.getDescription());
        material.setFileUrl(request.getFileUrl());
        material.setFileName(request.getFileName());
        material.setFileSize(request.getFileSize());
        material.setFileType(request.getFileType());
        material.setUpdatedAt(LocalDateTime.now());

        return toResponse(materialRepository.save(material));
    }

    @Override
    public void deleteMaterial(Long id) {
        LectureMaterial material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));
        materialRepository.delete(material);
    }

    @Override
    public void deactivateMaterial(Long id) {
        LectureMaterial material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));
        material.setActive(false);
        material.setUpdatedAt(LocalDateTime.now());
        materialRepository.save(material);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LectureMaterialResponse> getMaterialsByLecturer(Long lecturerId) {
        return materialRepository.findByUploadedByIdAndActiveTrue(lecturerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LectureMaterialResponse toResponse(LectureMaterial material) {
        return LectureMaterialResponse.builder()
                .id(material.getId())
                .title(material.getTitle())
                .description(material.getDescription())
                .courseId(material.getCourse().getId())
                .uploadedById(material.getUploadedBy().getId())
                .uploadedByName(material.getUploadedBy().getFullName())
                .fileUrl(material.getFileUrl())
                .fileName(material.getFileName())
                .fileSize(material.getFileSize())
                .fileType(material.getFileType())
                .uploadedAt(material.getUploadedAt())
                .updatedAt(material.getUpdatedAt())
                .active(material.isActive())
                .build();
    }
}
