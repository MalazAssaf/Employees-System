package com.example.employee.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AssignManagerRequest(@NotNull(message = "Manager ID is required") UUID managerId) {
}
