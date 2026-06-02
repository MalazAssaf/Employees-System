package com.example.employee.event;

public record EmployeeCreatedEvent(
    String email, String token) {
}