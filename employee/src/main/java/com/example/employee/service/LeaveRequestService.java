package com.example.employee.service;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employee.dtos.request.LeaveRequestPatchRequest;
import com.example.employee.dtos.request.LeaveRequestRequest;
import com.example.employee.dtos.request.LeaveRequestStatusUpdateRequest;
import com.example.employee.dtos.request.LeaveRequestUpdateRequest;
import com.example.employee.dtos.response.EmployeeSummaryResponse;
import com.example.employee.dtos.response.LeaveRequestResponse;
import com.example.employee.dtos.response.LeaveRequestWithEmployeeResponse;
import com.example.employee.entity.Employee;
import com.example.employee.entity.LeaveRequest;
import com.example.employee.entity.LeaveRequestStatus;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.LeaveRequestRepo;
import com.example.employee.shared.CustomResponseException;
import com.example.employee.utils.PaginationUtil;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

  public LeaveRequestWithEmployeeResponse getById(UUID id) {
    LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Leave request with id " + id + " not found!"));
    return toDto(leaveRequest);
  }

  @Cacheable(value = "allLeaveRequests")
  public Page<LeaveRequestWithEmployeeResponse> getAll(int page, int size) {

    Pageable pageable = PaginationUtil.createPageable(page, size);

    return leaveRequestRepo.findAll(pageable).map(this::toDto);

  }

  public EmployeeSummaryResponse getEmployeeInfo(UUID employeeId) {

    Employee employee = employeeRepo.findById(employeeId).orElseThrow(
        () -> CustomResponseException.resourceNotFoundException("Manager with id " + employeeId + " not found!"));

    return new EmployeeSummaryResponse(
        employee.getId(),
        employee.getName());
  }

  @Cacheable(value = "employeeLeaveRequests", key = "{#id, #page, #size}")
  public Page<LeaveRequestResponse> getRequestsByEmployee(UUID id, int page, int size) {

    Pageable pageable = PaginationUtil.createPageable(page, size);

    Page<LeaveRequestResponse> leaverequestsPage = leaveRequestRepo
        .findAllByEmployeeId(id, pageable)
        .map(r -> new LeaveRequestResponse(
            r.getId(),
            r.getStartDate(),
            r.getEndDate(),
            r.getReason(),
            r.getStatus()));

    return leaverequestsPage;

  }

  @Caching(evict = {
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", key = "#req.employeeId")
  })
  public LeaveRequestWithEmployeeResponse create(LeaveRequestRequest req) {

    Employee employee = employeeRepo.findById(req.getEmployeeId()).orElseThrow(() -> CustomResponseException
        .resourceNotFoundException("Employee with id: " + req.getEmployeeId() + " not found!"));

    LeaveRequest leaveRequest = new LeaveRequest();
    leaveRequest.setStartDate(req.getStartDate());
    leaveRequest.setEndDate(req.getEndDate());
    leaveRequest.setReason(req.getReason());
    leaveRequest.setEmployee(employee);

    leaveRequest = leaveRequestRepo.save(leaveRequest);

    return toDto(leaveRequest);
  }

  @Transactional
  @Caching(evict = {
      @CacheEvict(value = "leaveRequests", key = "#id"),
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", allEntries = true)
  })
  public String delete(UUID id) {

    if (!leaveRequestRepo.existsById(id)) {
      throw CustomResponseException.resourceNotFoundException("Leave request with " + id + " Not found!");
    }

    leaveRequestRepo.deleteById(id);

    return ("The leave Request with id " + id + "is deleted successfully!");
  }

  @CachePut(value = "leaveRequests", key = "#id")
  @Caching(evict = {
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", allEntries = true)
  })
  public LeaveRequestWithEmployeeResponse update(UUID id, LeaveRequestUpdateRequest req) {

    LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Leave request with id " + id + " not found!"));

    if ((leaveRequest.getStatus() != LeaveRequestStatus.PENDING)) {
      throw CustomResponseException.badRequestException(
          "Cannot edit the request because it is no longer pending.");
    }

    leaveRequest.setStartDate(req.startDate());
    leaveRequest.setEndDate(req.endDate());
    leaveRequest.setReason(req.reason());

    leaveRequest = leaveRequestRepo.save(leaveRequest);

    return toDto(leaveRequest);
  }

  @CachePut(value = "leaveRequests", key = "#id")
  @Caching(evict = {
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", allEntries = true)
  })
  public LeaveRequestWithEmployeeResponse patch(UUID id, LeaveRequestPatchRequest req) {

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

    leaveRequest = leaveRequestRepo.save(leaveRequest);

    return toDto(leaveRequest);
  }

  @Caching(evict = {
      @CacheEvict(value = "leaveRequests", key = "#id"),
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", allEntries = true)
  })
  public LeaveRequestWithEmployeeResponse updateLeaveRequestStatus(UUID id,
      LeaveRequestStatusUpdateRequest req) {

    LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Leave request with id " + id + " not found!"));

    leaveRequest.setStatus(LeaveRequestStatus.valueOf(req.status().toUpperCase()));

    leaveRequest = leaveRequestRepo.save(leaveRequest);

    return toDto(leaveRequest);
  }

}