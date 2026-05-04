package com.example.employee.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dtos.request.AssignManagerRequest;
import com.example.employee.dtos.request.DepartmentCreateRequest;
import com.example.employee.dtos.response.DepartmentResponse;
import com.example.employee.dtos.response.DepartmentWithEmployeesResponse;
import com.example.employee.dtos.response.PaginatedResponse;
import com.example.employee.service.DepartmentService;
import com.example.employee.shared.GlobalResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

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
  public GlobalResponse<PaginatedResponse<DepartmentResponse>> getAll(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "3") int size, HttpServletRequest request) {
    String baseUrl = request.getRequestURI();

    PaginatedResponse<DepartmentResponse> paginatedResponse = service.getAll(page, size, baseUrl);

    return new GlobalResponse<>(paginatedResponse);
  }

  @GetMapping("/{id}/employees")
  public GlobalResponse<DepartmentWithEmployeesResponse> getEmployeeByDepartment(@PathVariable UUID id,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "3") int size, HttpServletRequest request) {
    String baseUrl = request.getRequestURI();

    DepartmentWithEmployeesResponse paginatedResponse = service.getDepartmentWithEmployees(id, page,
        size, baseUrl);
    return new GlobalResponse<>(paginatedResponse);
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
