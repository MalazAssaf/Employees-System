package com.example.employee.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.employee.entity.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, UUID> {
  @EntityGraph(attributePaths = { "department" }) // Solve N+1 Query problem
  List<Employee> findAll();

  List<Employee> findAllByDepartmentId(UUID departmentId);
}
