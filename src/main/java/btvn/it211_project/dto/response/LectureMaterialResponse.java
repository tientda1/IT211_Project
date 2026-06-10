package btvn.it211_project.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureMaterialResponse {

    private Long id;

    private String title;

    private String description;

    private Long courseId;

    private Long uploadedById;

    private String uploadedByName;

    private String fileUrl;

    private String fileName;

    private long fileSize;

    private String fileType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private boolean active;
}
