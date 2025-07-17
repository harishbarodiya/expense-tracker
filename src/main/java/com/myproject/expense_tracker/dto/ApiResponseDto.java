package com.myproject.expense_tracker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myproject.expense_tracker.enums.ApiStatus;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

//@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {
    private ApiStatus status;
    private int code;
    private String message;
    private LocalDateTime timestamp;
    private T data;

    public ApiResponseDto(ApiStatus status, int code, String message, LocalDateTime timestamp, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.data = data;
    }

    public ApiStatus getStatus() {
        return status;
    }

    public void setStatus(ApiStatus status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
