package com.example.employee.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee.entity.LeaveRequest;

public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, UUID> {

  @Override
  @Cacheable(value = "leaveRequests", key = "#id")
  Optional<LeaveRequest> findById(UUID id);

  List<LeaveRequest> findAllByEmployeeId(UUID employeeId);

}
