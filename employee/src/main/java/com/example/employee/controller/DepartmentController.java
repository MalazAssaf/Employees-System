package com.example.employee.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dtos.request.AssignManagerRequest;
import com.example.employee.dtos.request.DepartmentCreateRequest;
import com.example.employee.dtos.response.DepartmentResponse;
import com.example.employee.dtos.response.DepartmentWithEmployeesResponse;
import com.example.employee.service.DepartmentService;
import com.example.employee.shared.GlobalResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {
  private final DepartmentService service;

  @GetMapping("/{id}")
  public GlobalResponse<DepartmentResponse> getById(@PathVariable UUID id) {
    return service.getById(id);
  }

  @GetMapping()
  public GlobalResponse<List<DepartmentResponse>> getAll() {
    return service.getAll();
  }

  @GetMapping("/{id}/employees")
  public GlobalResponse<DepartmentWithEmployeesResponse> getEmployeeByDepartment(@PathVariable UUID id) {
    return service.getDepartmentWithEmployees(id);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping
  public GlobalResponse<DepartmentResponse> create(@Valid @RequestBody DepartmentCreateRequest req) {
    return service.create(req);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable UUID id) {
    service.delete(id);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/{id}")
  public GlobalResponse<DepartmentResponse> update(@PathVariable UUID id,
      @Valid @RequestBody DepartmentCreateRequest request) {
    return service.update(id, request);
  }

  @PatchMapping("/{id}/manager")
  public GlobalResponse<String> assignManager(
      @PathVariable UUID id,
      @Valid @RequestBody AssignManagerRequest request) {
    return service.assignManagerToDepartment(id, request);
  }

  @DeleteMapping("/{id}/manager")
  public GlobalResponse<String> removeManager(@PathVariable UUID id) {
    return service.removeManagerFromDepartment(id);
  }

}
