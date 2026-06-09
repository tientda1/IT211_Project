package btvn.it211_project.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiResponse<T> {
    String message;
    T data;
}