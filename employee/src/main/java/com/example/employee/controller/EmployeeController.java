package com.example.employee.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dtos.request.EmployeeRequest;
import com.example.employee.dtos.response.EmployeeResponse;
import com.example.employee.service.EmployeeService;
import com.example.employee.shared.GlobalResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {
  private final EmployeeService service;

  @GetMapping("/{id}")
  public GlobalResponse<EmployeeResponse> getById(@PathVariable UUID id) {
    return service.getById(id);
  }

  @GetMapping()
  public GlobalResponse<List<EmployeeResponse>> getAll() {
    return service.getAll();
  }

  @PostMapping
  public GlobalResponse<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest req) {

    return service.create(req);
  }

  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable UUID id) {
    service.delete(id);
  }

  @PutMapping("/{id}")
  public GlobalResponse<EmployeeResponse> update(@PathVariable UUID id, @Valid @RequestBody EmployeeRequest request) {
    return service.update(id, request);
  }

}
