package com.example.employee.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.employee.dtos.request.LoginRequest;
import com.example.employee.dtos.request.RegisterRequest;
import com.example.employee.dtos.response.RegisterResponse;
import com.example.employee.entity.Employee;
import com.example.employee.entity.User;
import com.example.employee.entity.UserRole;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.UserRepo;
import com.example.employee.shared.CustomResponseException;
import com.example.employee.shared.GlobalResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepo userRepo;
  private final EmployeeRepo employeeRepo;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  private RegisterResponse toDto(User user) {
    return new RegisterResponse(
        user.getId(),
        user.getUsername(),
        user.getRole(),
        user.getEmployee().getId());
  }

  public GlobalResponse<RegisterResponse> register(RegisterRequest req) {
    User user = new User();

    Employee employee = employeeRepo.findById(req.employeeId())
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Employee with " + req.employeeId() + " Not found!"));
    user.setUsername(req.username().toLowerCase());

    user.setPassword(passwordEncoder.encode(req.password()));
    user.setRole(UserRole.valueOf(req.role().toUpperCase()));
    user.setEmployee(employee);

    return new GlobalResponse<>(toDto(userRepo.save(user)));
  }

  public GlobalResponse<LoginResponse> login(LoginRequest request) {
    // Will throw AuthenticationException if it is not valid
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username().toLowerCase(),
            request.password()));

    String jwtToken = jwtService.generateToken(request.username());

    return new GlobalResponse<>(new LoginResponse(jwtToken));
  }

}