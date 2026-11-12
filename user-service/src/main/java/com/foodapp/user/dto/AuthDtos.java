package com.foodapp.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDtos {

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String fullName;

        @Email
        @NotBlank
        private String email;

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank
        private String phone;
    }

    @Data
    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String fullName;
        private String email;
        private String role;

        public AuthResponse(String token, String fullName, String email, String role) {
            this.token = token;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
        }
    }
}
