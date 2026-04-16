package com.example.employee.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.employee.dtos.request.EmployeeRequest;
import com.example.employee.dtos.response.EmployeeResponse;
import com.example.employee.entity.Department;
import com.example.employee.entity.Employee;
import com.example.employee.repo.DepartmentRepo;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.shared.GlobalResponse;
import com.example.employee.shared.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepo repo;
  private final DepartmentRepo departmentRepo;

  private EmployeeResponse toDto(Employee employee) {
    return new EmployeeResponse(
        employee.getId(),
        employee.getName(),
        employee.getEmail(),
        employee.getPhoneNumber(),
        employee.getHireDate(),
        employee.getDepartment().getId(),
        employee.getDepartment().getName());
  }

  public GlobalResponse<EmployeeResponse> getById(UUID id) {
    Employee employee = repo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found!"));
    return new GlobalResponse<>(toDto(employee));
  }

  public GlobalResponse<List<EmployeeResponse>> getAll() {
    List<EmployeeResponse> employees = repo.findAll().stream().map(this::toDto).toList();
    return new GlobalResponse<>(employees);
  }

  public GlobalResponse<EmployeeResponse> create(EmployeeRequest req) {
    Department department = departmentRepo.findById(req.getDepartmentId())
        .orElseThrow(() -> new ResourceNotFoundException("Department with " + req.getDepartmentId() + " Not found!"));
    Employee employee = new Employee();
    employee.setName(req.getName());
    employee.setEmail(req.getEmail());
    employee.setPhoneNumber(req.getPhoneNumber());
    employee.setHireDate(req.getHireDate());
    employee.setDepartment(department);

    return new GlobalResponse<>(toDto(repo.save(employee)));
  }

  public void delete(UUID id) {
    if (!repo.existsById(id)) {
      throw new ResourceNotFoundException("Employee with " + id + " Not found!");
    }
    repo.deleteById(id);
  }

  public GlobalResponse<EmployeeResponse> update(UUID id, EmployeeRequest req) {
    Employee employee = repo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Employee with id " + id + " not found!"));

    Department department = departmentRepo.findById(req.getDepartmentId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Department with " + req.getDepartmentId() + " Not found!"));

    employee.setName(req.getName());
    employee.setEmail(req.getEmail());
    employee.setPhoneNumber(req.getPhoneNumber());
    employee.setHireDate(req.getHireDate());
    employee.setDepartment(department);

    return new GlobalResponse<>(toDto(repo.save(employee)));
  }
}
