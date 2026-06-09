package btvn.it211_project.service;

import btvn.it211_project.dto.request.CourseUpsertRequest;
import btvn.it211_project.dto.response.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    CourseResponse createCourse(CourseUpsertRequest request);

    CourseResponse updateCourse(Long id, CourseUpsertRequest request);

    void deactivateCourse(Long id);

    CourseResponse getCourseById(Long id);

    Page<CourseResponse> searchCourses(String keyword, Pageable pageable);
}