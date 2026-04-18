package com.example.employee.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dtos.request.RegisterRequest;
import com.example.employee.dtos.response.RegisterResponse;
import com.example.employee.service.UserService;
import com.example.employee.shared.GlobalResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth/signup")
@RequiredArgsConstructor
public class RegisterController {
  private final UserService userService;

  @PostMapping
  public GlobalResponse<RegisterResponse> create(@Valid @RequestBody RegisterRequest req) {
    return userService.create(req);
  }

}
