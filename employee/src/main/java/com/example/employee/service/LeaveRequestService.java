package com.example.employee.service;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import com.example.employee.dtos.request.LeaveRequestPatchRequest;
import com.example.employee.dtos.request.LeaveRequestRequest;
import com.example.employee.dtos.request.LeaveRequestStatusUpdateRequest;
import com.example.employee.dtos.request.LeaveRequestUpdateRequest;
import com.example.employee.dtos.response.EmployeeLeaveRequestsResponse;
import com.example.employee.dtos.response.LeaveRequestResponse;
import com.example.employee.dtos.response.LeaveRequestWithEmployeeResponse;
import com.example.employee.entity.Employee;
import com.example.employee.entity.LeaveRequest;
import com.example.employee.entity.LeaveRequestStatus;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.LeaveRequestRepo;
import com.example.employee.shared.CustomResponseException;
import com.example.employee.shared.GlobalResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

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
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Leave request with id " + id + " not found!"));
    return new GlobalResponse<>(toDto(leaveRequest));
  }

  @Cacheable(value = "allLeaveRequests")
  public GlobalResponse<List<LeaveRequestWithEmployeeResponse>> getAll() {
    List<LeaveRequestWithEmployeeResponse> leaveRequests = leaveRequestRepo.findAll()
        .stream().map(this::toDto).toList();
    return new GlobalResponse<>(leaveRequests);
  }

  @Cacheable(value = "employeeLeaveRequests", key = "#id")
  public GlobalResponse<EmployeeLeaveRequestsResponse> getRequestsByEmployee(UUID id) {

    Employee employee = employeeRepo.findById(id).orElseThrow(() -> CustomResponseException
        .resourceNotFoundException("Employee with id: " + id + " not found!"));

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

  @Caching(evict = {
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", key = "#req.employeeId")
  })
  public GlobalResponse<LeaveRequestWithEmployeeResponse> create(LeaveRequestRequest req) {
    Employee employee = employeeRepo.findById(req.getEmployeeId()).orElseThrow(() -> CustomResponseException
        .resourceNotFoundException("Employee with id: " + req.getEmployeeId() + " not found!"));

    LeaveRequest leaveRequest = new LeaveRequest();
    leaveRequest.setStartDate(req.getStartDate());
    leaveRequest.setEndDate(req.getEndDate());
    leaveRequest.setReason(req.getReason());
    leaveRequest.setEmployee(employee);

    return new GlobalResponse<>(toDto(leaveRequestRepo.save(leaveRequest)));
  }

  @Caching(evict = {
      @CacheEvict(value = "leaveRequests", key = "#id"),
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", allEntries = true)
  })
  public void delete(UUID id) {

    if (!leaveRequestRepo.existsById(id)) {
      throw CustomResponseException.resourceNotFoundException("leaveRequestRepo with " + id + " Not found!");
    }

    leaveRequestRepo.deleteById(id);
  }

  @CachePut(value = "leaveRequests", key = "#id")
  @Caching(evict = {
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", allEntries = true)
  })
  public GlobalResponse<LeaveRequestWithEmployeeResponse> update(UUID id, LeaveRequestUpdateRequest req) {

    LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Leave request with id " + id + " not found!"));

    leaveRequest.setStartDate(req.startDate());
    leaveRequest.setStartDate(req.startDate());
    leaveRequest.setEndDate(req.endDate());
    leaveRequest.setReason(req.reason());

    return new GlobalResponse<>(toDto(leaveRequestRepo.save(leaveRequest)));
  }

  @CachePut(value = "leaveRequests", key = "#id")
  @Caching(evict = {
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", allEntries = true)
  })
  public GlobalResponse<LeaveRequestWithEmployeeResponse> patch(UUID id, LeaveRequestPatchRequest req) {

    LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Leave request with id " + id + " not found!"));

    if (req.startDate() != null) {
      leaveRequest.setStartDate(req.startDate());
    }

    if (req.endDate() != null) {
      leaveRequest.setEndDate(req.endDate());
    }

    if (req.reason() != null) {
      leaveRequest.setReason(req.reason());
    }

    if (leaveRequest.getStartDate() != null && leaveRequest.getEndDate() != null) {
      if (!leaveRequest.getEndDate().isAfter(leaveRequest.getStartDate())) {
        throw CustomResponseException.badRequestException("End date must be after start date");
      }
    }

    return new GlobalResponse<>(toDto(leaveRequestRepo.save(leaveRequest)));
  }

  @Caching(evict = {
      @CacheEvict(value = "leaveRequests", key = "#id"),
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", allEntries = true)
  })
  public GlobalResponse<LeaveRequestWithEmployeeResponse> updateLeaveRequestStatus(UUID id,
      LeaveRequestStatusUpdateRequest req) {
    System.out.println("Hello From the service layer!");

    LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Leave request with id " + id + " not found!"));
    leaveRequest.setStatus(LeaveRequestStatus.valueOf(req.status().toUpperCase()));
    System.out.println("Hello From the update layer!");
    return new GlobalResponse<>(toDto(leaveRequestRepo.save(leaveRequest)));
  }

}