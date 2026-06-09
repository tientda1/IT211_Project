package btvn.it211_project.service;

import btvn.it211_project.dto.request.EnrollmentRequest;
import btvn.it211_project.dto.response.EnrollmentResponse;
import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse enroll(Long courseId, EnrollmentRequest request);

    List<EnrollmentResponse> getEnrollmentsForStudent(Long studentId);
}