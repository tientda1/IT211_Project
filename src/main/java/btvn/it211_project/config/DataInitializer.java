package btvn.it211_project.config;

import btvn.it211_project.domain.Course;
import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import btvn.it211_project.repository.CourseRepository;
import btvn.it211_project.repository.UserAccountRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedDemoData(UserAccountRepository userAccountRepository,
                                          CourseRepository courseRepository) {
        return args -> {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

            if (userAccountRepository.count() == 0) {
                List<UserAccount> users = List.of(
                        new UserAccount("System Admin", "admin@it211.local", passwordEncoder.encode("Admin@123"), "0900000001", Role.ADMIN),
                        new UserAccount("Demo Lecturer", "lecturer@it211.local", passwordEncoder.encode("Lecturer@123"), "0900000002", Role.LECTURER),
                        new UserAccount("Demo Student", "student@it211.local", passwordEncoder.encode("Student@123"), "0900000003", Role.STUDENT)
                );
                userAccountRepository.saveAll(users);
            }

            if (courseRepository.count() == 0) {
                courseRepository.saveAll(List.of(
                        new Course("IT101", "Java Web Fundamentals", "Introduction to Spring Boot and REST APIs", 30),
                        new Course("IT102", "Database Design", "Relational modeling and JPA basics", 25)
                ));
            }
        };
    }
}