package com.example.employee.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dtos.request.LeaveRequestPatchRequest;
import com.example.employee.dtos.request.LeaveRequestRequest;
import com.example.employee.dtos.request.LeaveRequestUpdateRequest;
import com.example.employee.dtos.response.EmployeeLeaveRequestsResponse;
import com.example.employee.dtos.response.LeaveRequestWithEmployeeResponse;
import com.example.employee.service.LeaveRequestService;
import com.example.employee.shared.GlobalResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

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
  public GlobalResponse<EmployeeLeaveRequestsResponse> getRequestsByEmployee(@PathVariable UUID id) {
    return leaveRequestService.getRequestsByEmployee(id);
  }

  @GetMapping()
  public GlobalResponse<List<LeaveRequestWithEmployeeResponse>> getAll() {
    return leaveRequestService.getAll();
  }

  @PostMapping
  public GlobalResponse<LeaveRequestWithEmployeeResponse> create(@Valid @RequestBody LeaveRequestRequest req) {
    return leaveRequestService.create(req);
  }

  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable UUID id) {
    leaveRequestService.delete(id);
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

}
