package com.example.employee.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.employee.dtos.request.LeaveRequestPatchRequest;
import com.example.employee.dtos.request.LeaveRequestRequest;
import com.example.employee.dtos.request.LeaveRequestStatusUpdateRequest;
import com.example.employee.dtos.request.LeaveRequestUpdateRequest;
import com.example.employee.dtos.response.EmployeeSummaryResponse;
import com.example.employee.dtos.response.EmployeeLeaveRequestsResponse;
import com.example.employee.dtos.response.LeaveRequestResponse;
import com.example.employee.dtos.response.LeaveRequestWithEmployeeResponse;
import com.example.employee.dtos.response.PaginatedResponse;
import com.example.employee.service.LeaveRequestService;
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
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/leaverequest")
@RequiredArgsConstructor
public class LeaveRequestController {
  private final LeaveRequestService leaveRequestService;

  @PreAuthorize("hasAuthority('ADMIN') or @securityUtils.isOwnerOfLeaveRequest(#id, authentication.principal.employee?.id)")
  @GetMapping("/{id}")
  public GlobalResponse<LeaveRequestWithEmployeeResponse> getById(@PathVariable UUID id) {
    return new GlobalResponse<>(leaveRequestService.getById(id));
  }

  @PreAuthorize("hasAuthority('ADMIN') or #id==authentication.principal.employee?.id")
  @GetMapping("/{id}/requests")
  public GlobalResponse<EmployeeLeaveRequestsResponse> getRequestsByEmployee(@PathVariable UUID id,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "3") int size, HttpServletRequest request) {

    String baseUrl = ServletUriComponentsBuilder
        .fromRequestUri(request)
        .replaceQuery(null)
        .build()
        .toUriString();

    EmployeeSummaryResponse employee = leaveRequestService.getEmployeeInfo(id);

    Page<LeaveRequestResponse> leaveRequestPage = leaveRequestService.getRequestsByEmployee(id, page, size);

    PaginatedResponse<LeaveRequestResponse> paginatedRequests = PaginationUtil.buildResponse(leaveRequestPage,
        page, size,
        baseUrl);

    EmployeeLeaveRequestsResponse response = new EmployeeLeaveRequestsResponse(
        employee,
        paginatedRequests);

    return new GlobalResponse<>(response);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping()
  public GlobalResponse<PaginatedResponse<LeaveRequestWithEmployeeResponse>> getAll(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "3") int size, HttpServletRequest request) {

    String baseUrl = ServletUriComponentsBuilder
        .fromRequestUri(request)
        .replaceQuery(null)
        .build()
        .toUriString();

    Page<LeaveRequestWithEmployeeResponse> leaveRequestsPage = leaveRequestService.getAll(page, size);

    PaginatedResponse<LeaveRequestWithEmployeeResponse> paginatedResponse = PaginationUtil
        .buildResponse(leaveRequestsPage, page, size, baseUrl);

    return new GlobalResponse<>(paginatedResponse);
  }

  @PreAuthorize("#req.employeeId == authentication.principal.employee?.id")
  @PostMapping
  public GlobalResponse<LeaveRequestWithEmployeeResponse> create(@Valid @RequestBody LeaveRequestRequest req) {
    return new GlobalResponse<>(leaveRequestService.create(req));
  }

  @PreAuthorize("hasAuthority('ADMIN') or @securityUtils.isOwnerOfLeaveRequest(#id, authentication.principal.employee?.id)")
  @DeleteMapping("/{id}")
  public GlobalResponse<String> deleteById(@PathVariable UUID id) {
    return new GlobalResponse<String>(leaveRequestService.delete(id));
  }

  @PreAuthorize("@securityUtils.isOwnerOfLeaveRequest(#id, authentication.principal.employee?.id)")
  @PutMapping("/{id}")
  public GlobalResponse<LeaveRequestWithEmployeeResponse> update(@PathVariable UUID id,
      @Valid @RequestBody LeaveRequestUpdateRequest req) {
    return new GlobalResponse<>(leaveRequestService.update(id, req));
  }

  @PreAuthorize("@securityUtils.isOwnerOfLeaveRequest(#id, authentication.principal.employee?.id)")
  @PatchMapping("/{id}")
  public GlobalResponse<LeaveRequestWithEmployeeResponse> patch(@PathVariable UUID id,
      @RequestBody LeaveRequestPatchRequest req) {
    return new GlobalResponse<>(leaveRequestService.patch(id, req));
  }

  @PreAuthorize("@securityUtils.leaveRequestAccessedByManager(#id)")
  @PatchMapping("/{id}/status")
  public GlobalResponse<LeaveRequestWithEmployeeResponse> updateLeaveRequestStatus(@PathVariable UUID id,
      @RequestBody @Valid LeaveRequestStatusUpdateRequest status) {
    return new GlobalResponse<>(leaveRequestService.updateLeaveRequestStatus(id, status));
  }

}
