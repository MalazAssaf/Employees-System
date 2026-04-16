package com.example.employee.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee.entity.LeaveRequest;

public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, UUID> {
  List<LeaveRequest> findAllByEmployeeId(UUID employeeId);
}
