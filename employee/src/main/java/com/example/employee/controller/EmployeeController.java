package com.example.employee.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.employee.dtos.request.EmployeeRequest;
import com.example.employee.dtos.response.EmployeeResponse;
import com.example.employee.dtos.response.EmployeeSummaryResponse;
import com.example.employee.dtos.response.EmployeeUnderManagerResponse;
import com.example.employee.dtos.response.ManagerResponse;
import com.example.employee.dtos.response.PaginatedResponse;
import com.example.employee.service.EmployeeService;
import com.example.employee.shared.GlobalResponse;
import com.example.employee.utils.PaginationUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
  @PreAuthorize("hasAuthority('ADMIN') or #id==authentication.principal.employee?.id or @securityUtils.isManager(#id)")
  public GlobalResponse<EmployeeResponse> getById(@PathVariable UUID id) {
    return new GlobalResponse<>(service.getById(id));
  }

  @GetMapping()
  @PreAuthorize("hasAuthority('ADMIN')")
  public GlobalResponse<PaginatedResponse<EmployeeResponse>> getAll(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "3") int size,
      HttpServletRequest request) {

    String baseUrl = ServletUriComponentsBuilder
        .fromRequestUri(request)
        .replaceQuery(null)
        .build()
        .toUriString();

    Page<EmployeeResponse> employees = service.getAll(page, size);

    PaginatedResponse<EmployeeResponse> paginatedResponse = PaginationUtil.buildResponse(employees, page, size,
        baseUrl);

    return new GlobalResponse<>(paginatedResponse);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping
  public GlobalResponse<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest req) {

    return new GlobalResponse<EmployeeResponse>(service.create(req));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public GlobalResponse<String> deleteById(@PathVariable UUID id) {
    return new GlobalResponse<String>(service.delete(id));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/{id}")
  public GlobalResponse<EmployeeResponse> update(@PathVariable UUID id, @Valid @RequestBody EmployeeRequest req) {
    return new GlobalResponse<>(service.update(id, req));
  }

  @PreAuthorize("hasAuthority('ADMIN') or #id==authentication.principal.employee?.id")
  @GetMapping("/manager/{id}")
  public GlobalResponse<EmployeeUnderManagerResponse> getAllEmployeesUnderManager(
      @PathVariable UUID id,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "3") int size,
      HttpServletRequest request) {

    String baseUrl = ServletUriComponentsBuilder
        .fromRequestUri(request)
        .replaceQuery(null)
        .build()
        .toUriString();

    ManagerResponse manager = service.getManagerInfo(id);

    Page<EmployeeSummaryResponse> employeesPage = service.getEmployeesUnderManager(id, page, size);

    PaginatedResponse<EmployeeSummaryResponse> paginatedTeam = PaginationUtil.buildResponse(employeesPage,
        page, size, baseUrl);

    EmployeeUnderManagerResponse response = new EmployeeUnderManagerResponse(
        manager,
        paginatedTeam);

    return new GlobalResponse<>(response);
  }

}
