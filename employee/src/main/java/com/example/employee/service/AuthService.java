package com.example.employee.service;

import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.employee.dtos.request.LoginRequest;
import com.example.employee.dtos.request.SignUpRequest;
import com.example.employee.dtos.response.SignUpResponse;
import com.example.employee.entity.ActivationToken;
import com.example.employee.entity.Employee;
import com.example.employee.entity.User;
import com.example.employee.entity.UserRole;
import com.example.employee.repo.ActivationTokenRepo;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.UserRepo;
import com.example.employee.shared.CustomResponseException;
import com.example.employee.shared.GlobalResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepo userRepo;
  private final EmployeeRepo employeeRepo;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final ActivationTokenRepo activationTokenRepo;

  private SignUpResponse toDto(User user) {
    return new SignUpResponse(
        user.getId(),
        user.getUsername(),
        user.getRole(),
        user.getEmployee().getId());
  }

  @Transactional
  public GlobalResponse<SignUpResponse> signUp(SignUpRequest req, String token) {

    ActivationToken activationToken = activationTokenRepo.findByToken(token)
        .orElseThrow(() -> CustomResponseException.badRequestException("Invalid or missing activation token!"));

    if (activationToken.getExpiryDate().before(new Date())) {
      activationTokenRepo.delete(activationToken);
      throw CustomResponseException.badRequestException("This activation link has expired. Please request a new one.");
    }

    Employee employee = activationToken.getEmployee();

    if (userRepo.existsByUsername(req.username().toLowerCase())) {
      throw CustomResponseException.badRequestException("Username is already taken!");
    }

    User user = new User();
    user.setUsername(req.username().toLowerCase());
    user.setPassword(passwordEncoder.encode(req.password()));

    user.setRole(UserRole.valueOf(req.role().toUpperCase()));

    user.setEmployee(employee);

    employee.setIsActivated(true);
    employeeRepo.save(employee);

    activationTokenRepo.delete(activationToken);

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