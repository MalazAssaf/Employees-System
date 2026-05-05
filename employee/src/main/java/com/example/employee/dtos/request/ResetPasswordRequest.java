package com.example.employee.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Add the token") String token,

        @NotBlank(message = "Please add the new password") String newPassword) {
}
