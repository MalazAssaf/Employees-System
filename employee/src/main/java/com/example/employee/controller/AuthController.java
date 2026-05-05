package com.example.employee.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dtos.request.LoginRequest;
import com.example.employee.dtos.request.ResetPasswordRequest;
import com.example.employee.dtos.request.SignUpRequest;
import com.example.employee.dtos.response.SignUpResponse;
import com.example.employee.service.LoginResponse;
import com.example.employee.service.AuthService;
import com.example.employee.shared.GlobalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/signup")
  public GlobalResponse<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest req, @RequestParam String token) {
    return new GlobalResponse<>(authService.signUp(req, token));
  }

  @PostMapping("/login")
  public GlobalResponse<LoginResponse> login(@RequestBody LoginRequest req) {
    return new GlobalResponse<>(authService.login(req));
  }

  @PostMapping("/forgot-password/{username}")
  public GlobalResponse<String> forgotPassword(@PathVariable String username) {
    authService.initiatePasswordReset(username);
    return new GlobalResponse<String>("Password reset email sent!");
  }

  @PostMapping("/reset-password")
  public GlobalResponse<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
    authService.resetPassword(resetPasswordRequest);
    return new GlobalResponse<String>("Password has been updated successfully!");
  }
}
