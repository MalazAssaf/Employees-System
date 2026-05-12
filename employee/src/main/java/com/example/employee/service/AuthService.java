package com.example.employee.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.employee.dtos.request.LoginRequest;
import com.example.employee.dtos.request.ResetPasswordRequest;
import com.example.employee.dtos.request.SignUpRequest;
import com.example.employee.dtos.response.SignUpResponse;
import com.example.employee.entity.ActivationToken;
import com.example.employee.entity.Employee;
import com.example.employee.entity.PasswordResetToken;
import com.example.employee.entity.User;
import com.example.employee.repo.ActivationTokenRepo;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.PasswordResetTokenRepo;
import com.example.employee.repo.UserRepo;
import com.example.employee.shared.CustomResponseException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
  private final PasswordResetTokenRepo passwordResetTokenRepo;
  private final EmailService emailService;

  private SignUpResponse toDto(User user) {
    return new SignUpResponse(
        user.getId(),
        user.getUsername(),
        user.getEmployee().getRole(),
        user.getEmployee().getId());
  }

  @Transactional
  public SignUpResponse signUp(SignUpRequest req, String token) {

    ActivationToken activationToken = activationTokenRepo.findByToken(token)
        .orElseThrow(() -> CustomResponseException.badRequestException("Invalid or missing activation token!"));

    boolean isTokenExpired = activationToken.getExpiryDate().before(new Date());

    if (isTokenExpired) {
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

    user.setEmployee(employee);

    employee.setIsActivated(true);
    employeeRepo.save(employee);

    activationTokenRepo.delete(activationToken);

    User savedUser = userRepo.save(user);

    return toDto(savedUser);
  }

  public LoginResponse login(LoginRequest request, HttpServletResponse response) {
    Authentication authenitcation = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username().toLowerCase(),
            request.password()));

    User user = (User) authenitcation.getPrincipal();

    String jwtToken = jwtService.generateToken(user);

    // Set httpOnly cookie
    Cookie cookie = new Cookie("token", jwtToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/");
    cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
    response.addCookie(cookie);

    return new LoginResponse(
        user.getUsername(),
        user.getEmployee().getRole());
  }

  @Transactional
  public void initiatePasswordReset(String username) {

    User user = userRepo.findByUsername(username)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException("Account not found!"));

    String token = UUID.randomUUID().toString();
    LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

    PasswordResetToken resetToken = new PasswordResetToken();
    resetToken.setToken(token);
    resetToken.setUser(user);
    resetToken.setExpiryDate(expiry);

    passwordResetTokenRepo.save(resetToken);

    emailService.sendPasswordResetEmail(user.getEmployee().getEmail(), token);
  }

  @Transactional
  public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
    PasswordResetToken resetToken = passwordResetTokenRepo.findOneByToken(resetPasswordRequest.token())
        .orElseThrow(() -> CustomResponseException.badRequestException("Invalid token"));

    boolean isTokenExpired = resetToken.getExpiryDate().isBefore(LocalDateTime.now());

    if (isTokenExpired) {
      passwordResetTokenRepo.delete(resetToken);
      throw CustomResponseException.badRequestException("Token has expired, request a new one");
    }

    User user = resetToken.getUser();
    user.setPassword(passwordEncoder.encode(resetPasswordRequest.newPassword()));
    userRepo.save(user);

    passwordResetTokenRepo.delete(resetToken);
  }

}