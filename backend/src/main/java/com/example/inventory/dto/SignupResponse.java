package com.example.inventory.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
public class SignupResponse {
    private boolean success;
    private String message;
    private String username;

    public SignupResponse(boolean success, String message, String username) {
        this.success = success;
        this.message = message;
        this.username = username;
    }

    public SignupResponse() {
    }

    public boolean isSuccess() {
        return success;
    }
} 