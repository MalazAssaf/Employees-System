package com.example.employee.service;

import com.example.employee.entity.UserRole;

public record LoginResponse(String username, UserRole role) {

}
