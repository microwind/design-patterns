package com.microwind.knife.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    private int code;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(HttpStatus.OK.value(), data, message);
    }

    public static <T> ApiResponse<T> failure(int code, T data, String message) {
        return new ApiResponse<>(code, data, message);
    }

    public static <T> ApiResponse<T> failure(int code, String message) {
        return new ApiResponse<>(code, null, message);
    }
}