package com.example.employee.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.employee.entity.Employee;
import com.example.employee.entity.UserRole;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, UUID> {

  @Override
  @Cacheable(value = "leaveRequests", key = "#id")
  Optional<Employee> findById(UUID id);

  @EntityGraph(attributePaths = { "department" }) // Solve N+1 Query problem
  List<Employee> findAll();

  Page<Employee> findAllByDepartmentId(UUID departmentId, Pageable Pageable);

  Page<Employee> findAllByManagerId(UUID managerId, Pageable Pegable);

  boolean existsByManagerId(UUID managerId);

  boolean existsByDepartmentId(UUID departmentId);

  boolean existsByEmail(String email);

  boolean existsByPhoneNumber(String phoneNumber);

  int countByDepartmentId(UUID departmentId);

  List<Employee> findByRole(UserRole role);
}
