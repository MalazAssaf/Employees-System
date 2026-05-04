package com.example.employee.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee.entity.LeaveRequest;

public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, UUID> {

  @Override
  @Cacheable(value = "leaveRequests", key = "#id")
  Optional<LeaveRequest> findById(UUID id);

  Page<LeaveRequest> findAllByEmployeeId(UUID employeeId, Pageable pageable);

  boolean existsByEmployeeId(UUID id);

}
