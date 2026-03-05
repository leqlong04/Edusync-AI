package com.edusync.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
}
