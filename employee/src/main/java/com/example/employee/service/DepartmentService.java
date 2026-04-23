package com.example.employee.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.employee.dtos.request.DepartmentRequest;
import com.example.employee.dtos.response.DepartmentResponse;
import com.example.employee.dtos.response.DepartmentWithEmployeesResponse;
import com.example.employee.dtos.response.EmployeeInDepartmentResponse;
import com.example.employee.entity.Department;
import com.example.employee.repo.DepartmentRepo;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.shared.CustomResponseException;
import com.example.employee.shared.GlobalResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {
  private final DepartmentRepo departmentRepo;
  private final EmployeeRepo employeeRepo;

  private DepartmentResponse toDto(Department department) {
    return new DepartmentResponse(
        department.getId(),
        department.getName());
  }

  public GlobalResponse<DepartmentResponse> getById(UUID id) {
    Department department = departmentRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException("Department with " + id + " Not found!"));
    return new GlobalResponse<>(toDto(department));
  }

  public GlobalResponse<List<DepartmentResponse>> getAll() {
    List<DepartmentResponse> departments = departmentRepo.findAll().stream().map(this::toDto).toList();
    return new GlobalResponse<List<DepartmentResponse>>(departments);
  }

  public GlobalResponse<DepartmentResponse> create(DepartmentRequest req) {
    Department department = new Department();
    department.setName(req.getName());
    return new GlobalResponse<>(toDto(departmentRepo.save(department)));
  }

  public void delete(UUID id) {
    if (!departmentRepo.existsById(id)) {
      throw CustomResponseException.resourceNotFoundException("Department with " + id + " Not found!");
    }
    departmentRepo.deleteById(id);
  }

  public GlobalResponse<DepartmentResponse> update(UUID id, DepartmentRequest req) {
    Department department = departmentRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException("Department with " + id + " Not found!"));
    department.setName(req.getName());
    return new GlobalResponse<>(toDto(departmentRepo.save(department)));
  }

  public GlobalResponse<DepartmentWithEmployeesResponse> getDepartmentWithEmployees(UUID id) {

    Department department = departmentRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Department with " + id + " Not found!"));

    List<EmployeeInDepartmentResponse> employees = employeeRepo
        .findAllByDepartmentId(id)
        .stream()
        .map(emp -> new EmployeeInDepartmentResponse(
            emp.getId(),
            emp.getName(),
            emp.getEmail()))
        .toList();

    DepartmentWithEmployeesResponse response = new DepartmentWithEmployeesResponse(
        department.getId(),
        department.getName(),
        employees);

    return new GlobalResponse<>(response);
  }
}
