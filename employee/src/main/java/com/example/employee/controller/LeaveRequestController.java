package com.example.employee.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dtos.request.LeaveRequestPatchRequest;
import com.example.employee.dtos.request.LeaveRequestRequest;
import com.example.employee.dtos.request.LeaveRequestStatusUpdateRequest;
import com.example.employee.dtos.request.LeaveRequestUpdateRequest;
import com.example.employee.dtos.response.EmployeeLeaveRequestsResponse;
import com.example.employee.dtos.response.LeaveRequestWithEmployeeResponse;
import com.example.employee.dtos.response.PaginatedResponse;
import com.example.employee.service.LeaveRequestService;
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
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/leaverequest")
@RequiredArgsConstructor
public class LeaveRequestController {
  private final LeaveRequestService leaveRequestService;

  @GetMapping("/{id}")
  public GlobalResponse<LeaveRequestWithEmployeeResponse> getById(@PathVariable UUID id) {
    return leaveRequestService.getById(id);
  }

  @GetMapping("/{id}/requests")
  public GlobalResponse<EmployeeLeaveRequestsResponse> getRequestsByEmployee(@PathVariable UUID id,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "3") int size, HttpServletRequest request) {
    String baseUrl = request.getRequestURI();

    EmployeeLeaveRequestsResponse paginatedResponse = leaveRequestService.getRequestsByEmployee(id,
        page, size, baseUrl);

    return new GlobalResponse<>(paginatedResponse);
  }

  @GetMapping()
  public GlobalResponse<PaginatedResponse<LeaveRequestWithEmployeeResponse>> getAll(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "3") int size, HttpServletRequest request) {

    String baseUrl = request.getRequestURI();

    PaginatedResponse<LeaveRequestWithEmployeeResponse> paginatedResponse = leaveRequestService.getAll(page, size,
        baseUrl);
    return new GlobalResponse<>(paginatedResponse);
  }

  @PostMapping
  public GlobalResponse<LeaveRequestWithEmployeeResponse> create(@Valid @RequestBody LeaveRequestRequest req) {
    return leaveRequestService.create(req);
  }

  @DeleteMapping("/{id}")
  public GlobalResponse<String> deleteById(@PathVariable UUID id) {
    return leaveRequestService.delete(id);
  }

  @PutMapping("/{id}")
  public GlobalResponse<LeaveRequestWithEmployeeResponse> update(@PathVariable UUID id,
      @Valid @RequestBody LeaveRequestUpdateRequest req) {
    return leaveRequestService.update(id, req);
  }

  @PatchMapping("/{id}")
  public GlobalResponse<LeaveRequestWithEmployeeResponse> patch(@PathVariable UUID id,
      @RequestBody LeaveRequestPatchRequest req) {
    return leaveRequestService.patch(id, req);
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("@securityUtils.leaveRequestAccessedByManager(#id)")
  public GlobalResponse<LeaveRequestWithEmployeeResponse> updateLeaveRequestStatus(@PathVariable UUID id,
      @RequestBody @Valid LeaveRequestStatusUpdateRequest status) {
    return leaveRequestService.updateLeaveRequestStatus(id, status);
  }

}
