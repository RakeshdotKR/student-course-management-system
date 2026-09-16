package com.scms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO for incoming registration requests (POST /api/auth/register)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    // Optional - "ROLE_USER" or "ROLE_ADMIN". Defaults to ROLE_USER if not provided.
    private String role;
}