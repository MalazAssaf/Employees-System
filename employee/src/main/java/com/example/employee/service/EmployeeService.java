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
import com.example.employee.shared.CustomResponseException;
import com.example.employee.shared.GlobalResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepo employeeRepo;
  private final DepartmentRepo departmentRepo;

  private EmployeeResponse toDto(Employee employee) {
    UUID managerId = null;
    String managerName = null;

    if (employee.getManager() != null) {
      managerId = employee.getManager().getId();
      managerName = employee.getManager().getName();
    }

    return new EmployeeResponse(
        employee.getId(),
        employee.getName(),
        employee.getEmail(),
        employee.getPhoneNumber(),
        employee.getHireDate(),
        employee.getDepartment().getId(),
        employee.getDepartment().getName(),
        managerId,
        managerName);
  }

  public GlobalResponse<EmployeeResponse> getById(UUID id) {
    Employee employee = employeeRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException("Employee with id " + id + " not found!"));
    return new GlobalResponse<>(toDto(employee));
  }

  public GlobalResponse<List<EmployeeResponse>> getAll() {
    List<EmployeeResponse> employees = employeeRepo.findAll().stream().map(this::toDto).toList();
    return new GlobalResponse<>(employees);
  }

  public GlobalResponse<EmployeeResponse> create(EmployeeRequest req) {
    Department department = departmentRepo.findById(req.getDepartmentId())
        .orElseThrow(() -> CustomResponseException
            .resourceNotFoundException("Department with " + req.getDepartmentId() + " Not found!"));
    Employee employee = new Employee();
    employee.setName(req.getName());
    employee.setEmail(req.getEmail());
    employee.setPhoneNumber(req.getPhoneNumber());
    employee.setHireDate(req.getHireDate());
    employee.setDepartment(department);

    if (req.getManagerId() != null) {
      Employee manager = employeeRepo.findById(req.getManagerId()).orElseThrow(() -> CustomResponseException
          .resourceNotFoundException("Manager with id " + req.getManagerId() + " Not found!"));
      employee.setManager(manager);
    }

    return new GlobalResponse<>(toDto(employeeRepo.save(employee)));
  }

  public void delete(UUID id) {
    if (!employeeRepo.existsById(id)) {
      throw CustomResponseException.resourceNotFoundException("Employee with " + id + " Not found!");
    }
    employeeRepo.deleteById(id);
  }

  public GlobalResponse<EmployeeResponse> update(UUID id, EmployeeRequest req) {
    Employee employee = employeeRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Employee with id " + id + " not found!"));

    Department department = departmentRepo.findById(req.getDepartmentId())
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Department with " + req.getDepartmentId() + " Not found!"));

    employee.setName(req.getName());
    employee.setEmail(req.getEmail());
    employee.setPhoneNumber(req.getPhoneNumber());
    employee.setHireDate(req.getHireDate());
    employee.setDepartment(department);

    if (req.getManagerId() != null) {
      if (employee.getId().equals(req.getManagerId())) {
        throw CustomResponseException.badRequestException("Invalid Action: Employee cannot be their own manager!");
      }

      Employee manager = employeeRepo.findById(req.getManagerId())
          .orElseThrow(() -> CustomResponseException
              .resourceNotFoundException("Manager with id " + req.getManagerId() + " Not found!"));

      employee.setManager(manager);
    }

    else {
      employee.setManager(null);
    }

    return new GlobalResponse<>(toDto(employeeRepo.save(employee)));
  }

}
