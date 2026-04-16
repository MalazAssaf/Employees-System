package com.example.employee.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.employee.entity.Department;

@Repository
public interface DepartmentRepo extends JpaRepository<Department, UUID> {
  boolean existsByNameIgnoreCase(String name);
}
