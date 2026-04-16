package com.example.employee.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.employee.dtos.request.LeaveRequestRequest;
import com.example.employee.dtos.request.LeaveRequestUpdateRequest;
import com.example.employee.dtos.response.EmployeeLeaveRequestsResponse;
import com.example.employee.dtos.response.LeaveRequestResponse;
import com.example.employee.dtos.response.LeaveRequestWithEmployeeResponse;
import com.example.employee.entity.Employee;
import com.example.employee.entity.LeaveRequest;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.LeaveRequestRepo;
import com.example.employee.shared.GlobalResponse;
import com.example.employee.shared.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

  private final LeaveRequestRepo leaveRequestRepo;
  private final EmployeeRepo employeeRepo;

  private LeaveRequestWithEmployeeResponse toDto(LeaveRequest leaveRequest) {
    return new LeaveRequestWithEmployeeResponse(leaveRequest.getId(),
        leaveRequest.getStartDate(),
        leaveRequest.getEndDate(),
        leaveRequest.getReason(),
        leaveRequest.getStatus(),
        leaveRequest.getEmployee().getId());
  }

  public GlobalResponse<LeaveRequestWithEmployeeResponse> getById(UUID id) {

    LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Leave Request with " + id + " not found!"));

    return new GlobalResponse<>(toDto(leaveRequest));
  }

  public GlobalResponse<List<LeaveRequestWithEmployeeResponse>> getAll() {
    List<LeaveRequestWithEmployeeResponse> leaveRequests = leaveRequestRepo.findAll().stream().map(this::toDto)
        .toList();
    return new GlobalResponse<>(leaveRequests);
  }

  public GlobalResponse<EmployeeLeaveRequestsResponse> getRequestsByEmployee(UUID id) {

    Employee employee = employeeRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Employee with " + id + " Not found!"));

    List<LeaveRequestResponse> requests = leaveRequestRepo
        .findAllByEmployeeId(id)
        .stream()
        .map(r -> new LeaveRequestResponse(
            r.getId(),
            r.getStartDate(),
            r.getEndDate(),
            r.getReason(),
            r.getStatus()))
        .toList();

    EmployeeLeaveRequestsResponse response = new EmployeeLeaveRequestsResponse(
        employee.getId(),
        employee.getName(),
        requests);

    return new GlobalResponse<>(response);
  }

  public GlobalResponse<LeaveRequestWithEmployeeResponse> create(LeaveRequestRequest req) {

    Employee employee = employeeRepo.findById(req.getEmployeeId())
        .orElseThrow(() -> new ResourceNotFoundException("Employee with " + req.getEmployeeId() + " Not found!"));

    LeaveRequest leaveRequest = new LeaveRequest();
    leaveRequest.setStartDate(req.getStartDate());
    leaveRequest.setEndDate(req.getEndDate());
    leaveRequest.setReason(req.getReason());
    leaveRequest.setEmployee(employee);

    return new GlobalResponse<>(toDto(leaveRequestRepo.save(leaveRequest)));
  }

  public void delete(UUID id) {

    if (!leaveRequestRepo.existsById(id)) {
      throw new ResourceNotFoundException("leaveRequestRepo with " + id + " Not found!");
    }

    leaveRequestRepo.deleteById(id);
  }

  public GlobalResponse<LeaveRequestWithEmployeeResponse> update(UUID id, LeaveRequestUpdateRequest req) {

    LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest with " + id + " Not found!"));

    leaveRequest.setStartDate(req.startDate());
    leaveRequest.setEndDate(req.endDate());
    leaveRequest.setReason(req.reason());

    return new GlobalResponse<>(toDto(leaveRequestRepo.save(leaveRequest)));
  }

}