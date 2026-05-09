package com.example.employee.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.employee.dtos.request.AssignManagerRequest;
import com.example.employee.dtos.request.DepartmentCreateRequest;
import com.example.employee.dtos.response.DepartmentResponse;
import com.example.employee.dtos.response.DepartmentWithEmployeesResponse;
import com.example.employee.dtos.response.EmployeeSummaryResponse;
import com.example.employee.dtos.response.PaginatedResponse;
import com.example.employee.service.DepartmentService;
import com.example.employee.shared.GlobalResponse;
import com.example.employee.utils.PaginationUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
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

  // TO DO
  @PreAuthorize("hasAuthority('ADMIN') or @securityUtils.isEmployeeOfDepartment(#id, authentication.principal.employee?.id)")
  @GetMapping("/{id}")
  public GlobalResponse<DepartmentResponse> getById(@PathVariable UUID id) {
    return new GlobalResponse<>(service.getById(id));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping()
  public GlobalResponse<PaginatedResponse<DepartmentResponse>> getAll(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "3") int size, HttpServletRequest request) {

    String baseUrl = ServletUriComponentsBuilder
        .fromRequestUri(request)
        .replaceQuery(null)
        .build()
        .toUriString();

    Page<DepartmentResponse> departments = service.getAll(page, size);

    PaginatedResponse<DepartmentResponse> paginatedResponse = PaginationUtil.buildResponse(departments, page, size,
        baseUrl);

    return new GlobalResponse<>(paginatedResponse);
  }

  @PreAuthorize("hasAuthority('ADMIN') or @securityUtils.isManagerOfDepartment(#id, authentication.principal.employee?.id)")
  @GetMapping("/{id}/employees")
  public GlobalResponse<DepartmentWithEmployeesResponse> getEmployeesByDepartment(@PathVariable UUID id,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "3") int size, HttpServletRequest request) {

    String baseUrl = ServletUriComponentsBuilder
        .fromRequestUri(request)
        .replaceQuery(null)
        .build()
        .toUriString();

    DepartmentResponse department = service.getById(id);

    Page<EmployeeSummaryResponse> employeesPage = service.getDepartmentWithEmployees(id, page, size);

    PaginatedResponse<EmployeeSummaryResponse> paginatedTeam = PaginationUtil.buildResponse(employeesPage,
        page, size,
        baseUrl);

    DepartmentWithEmployeesResponse response = new DepartmentWithEmployeesResponse(
        department,
        paginatedTeam);

    return new GlobalResponse<>(response);

  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping
  public GlobalResponse<DepartmentResponse> create(@Valid @RequestBody DepartmentCreateRequest req) {
    return new GlobalResponse<>(service.create(req));
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
    return new GlobalResponse<>(service.update(id, request));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PatchMapping("/{id}/manager")
  public GlobalResponse<String> assignManager(
      @PathVariable UUID id,
      @Valid @RequestBody AssignManagerRequest request) {
    return new GlobalResponse<String>(service.assignManagerToDepartment(id, request));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("/{id}/manager")
  public GlobalResponse<String> removeManager(@PathVariable UUID id) {
    return new GlobalResponse<>(service.removeManagerFromDepartment(id));
  }

}
