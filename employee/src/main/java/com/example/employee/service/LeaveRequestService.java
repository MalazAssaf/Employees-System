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
import com.example.employee.dtos.response.EmployeeLeaveRequestsResponse;
import com.example.employee.dtos.response.LeaveRequestResponse;
import com.example.employee.dtos.response.LeaveRequestWithEmployeeResponse;
import com.example.employee.dtos.response.PaginatedResponse;
import com.example.employee.entity.Employee;
import com.example.employee.entity.LeaveRequest;
import com.example.employee.entity.LeaveRequestStatus;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.LeaveRequestRepo;
import com.example.employee.shared.CustomResponseException;
import com.example.employee.shared.GlobalResponse;
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

  public GlobalResponse<LeaveRequestWithEmployeeResponse> getById(UUID id) {
    LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Leave request with id " + id + " not found!"));
    return new GlobalResponse<>(toDto(leaveRequest));
  }

  @Cacheable(value = "allLeaveRequests")
  public PaginatedResponse<LeaveRequestWithEmployeeResponse> getAll(int page, int size, String Url) {

    Pageable pageable = PaginationUtil.createPageable(page, size);

    Page<LeaveRequestWithEmployeeResponse> leaveRequests = leaveRequestRepo.findAll(pageable).map(this::toDto);

    return PaginationUtil.buildResponse(leaveRequests, page, size, Url);

  }

  @Cacheable(value = "employeeLeaveRequests", key = "{#id, #page, #size}")
  public EmployeeLeaveRequestsResponse getRequestsByEmployee(UUID id, int page, int size,
      String baseUrl) {

    Pageable pageable = PaginationUtil.createPageable(page, size);

    Employee employee = employeeRepo.findById(id).orElseThrow(() -> CustomResponseException
        .resourceNotFoundException("Employee with id: " + id + " not found!"));

    Page<LeaveRequestResponse> leaverequestsPage = leaveRequestRepo
        .findAllByEmployeeId(id, pageable)
        .map(r -> new LeaveRequestResponse(
            r.getId(),
            r.getStartDate(),
            r.getEndDate(),
            r.getReason(),
            r.getStatus()));

    PaginatedResponse<LeaveRequestResponse> paginatedResponse = PaginationUtil.buildResponse(
        leaverequestsPage, page,
        size, baseUrl);

    return new EmployeeLeaveRequestsResponse(
        employee.getId(),
        employee.getName(),
        paginatedResponse);

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

  @Transactional
  @Caching(evict = {
      @CacheEvict(value = "leaveRequests", key = "#id"),
      @CacheEvict(value = "allLeaveRequests", allEntries = true),
      @CacheEvict(value = "employeeLeaveRequests", allEntries = true)
  })
  public GlobalResponse<String> delete(UUID id) {

    if (!leaveRequestRepo.existsById(id)) {
      throw CustomResponseException.resourceNotFoundException("Leave request with " + id + " Not found!");
    }

    leaveRequestRepo.deleteById(id);

    return new GlobalResponse<String>("The leave Request with id " + id + "is deleted successfully!");
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

    LeaveRequest leaveRequest = leaveRequestRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Leave request with id " + id + " not found!"));

    leaveRequest.setStatus(LeaveRequestStatus.valueOf(req.status().toUpperCase()));

    return new GlobalResponse<>(toDto(leaveRequestRepo.save(leaveRequest)));
  }

}