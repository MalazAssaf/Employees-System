package com.example.employee.service;

import org.springframework.stereotype.Service;

import com.example.employee.dtos.request.RegisterRequest;
import com.example.employee.dtos.response.RegisterResponse;
import com.example.employee.entity.Employee;
import com.example.employee.entity.User;
import com.example.employee.entity.UserRole;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.UserRepo;
import com.example.employee.shared.GlobalResponse;
import com.example.employee.shared.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UserService {
  private final UserRepo userRepo;
  private final EmployeeRepo employeeRepo;

  private RegisterResponse toDto(User user) {
    return new RegisterResponse(
        user.getId(),
        user.getUsername(),
        user.getRole(),
        user.getEmployee().getId());
  }

  public GlobalResponse<RegisterResponse> create(RegisterRequest req) {
    User user = new User();

    Employee employee = employeeRepo.findById(req.getEmployeeId())
        .orElseThrow(() -> new ResourceNotFoundException("Employee with " + req.getEmployeeId() + " Not found!"));
    user.setUsername(req.getUsername());
    user.setPassword(req.getPassword());
    user.setRole(UserRole.valueOf(req.getRole().toUpperCase()));
    user.setEmployee(employee);

    return new GlobalResponse<>(toDto(userRepo.save(user)));
  }
}
