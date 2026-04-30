package com.example.employee.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employee.dtos.request.AssignManagerRequest;
import com.example.employee.dtos.request.DepartmentCreateRequest;
import com.example.employee.dtos.response.DepartmentResponse;
import com.example.employee.dtos.response.DepartmentWithEmployeesResponse;
import com.example.employee.dtos.response.EmployeeInDepartmentResponse;
import com.example.employee.entity.Department;
import com.example.employee.entity.Employee;
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

    UUID managerId = department.getManager() != null ? department.getManager().getId() : null;
    String managerName = department.getManager() != null ? department.getManager().getName() : null;
    return new DepartmentResponse(
        department.getId(),
        department.getName(),
        managerId,
        managerName);
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

  @Transactional
  public GlobalResponse<String> assignManagerToDepartment(UUID departmentId, AssignManagerRequest req) {

    Department department = departmentRepo.findById(departmentId).orElseThrow(
        () -> CustomResponseException.resourceNotFoundException("Department with " + departmentId + " Not found!"));

    Employee manager = employeeRepo.findById(req.managerId()).orElseThrow(
        () -> CustomResponseException.resourceNotFoundException("Manager with" + req.managerId() + " Not found!"));

    if (manager.getDepartment() == null || !manager.getDepartment().getId().equals(departmentId)) {
      throw CustomResponseException.badRequestException("Employee must belong to this department first!");
    }

    if (!employeeRepo.existsByManagerId(manager.getId())) {
      throw CustomResponseException
          .badRequestException("This employee does not manage any team members. They cannot head a department!");
    }

    if (departmentRepo.existsByManagerId(manager.getId())
        && (department.getManager() == null || !department.getManager().getId().equals(manager.getId()))) {
      throw CustomResponseException.badRequestException("This employee is already managing another department!");
    }

    department.setManager(manager);

    return new GlobalResponse<>(
        "Successfully assigned " + manager.getName() + " as the manager of " + department.getName());
  }

  public GlobalResponse<DepartmentResponse> create(DepartmentCreateRequest req) {
    Department department = new Department();
    department.setName(req.getName());
    return new GlobalResponse<>(toDto(departmentRepo.save(department)));
  }

  @Transactional
  public void delete(UUID id) {
    if (!departmentRepo.existsById(id)) {
      throw CustomResponseException.resourceNotFoundException("Department with " + id + " Not found!");
    }

    if (employeeRepo.existsByDepartmentId(id)) {
      throw CustomResponseException
          .badRequestException("Cannot Delete the department since employees are registered on it!");
    }
    departmentRepo.deleteById(id);
  }

  @Transactional
  public GlobalResponse<String> removeManagerFromDepartment(UUID departmentId) {

    Department department = departmentRepo.findById(departmentId)
        .orElseThrow(() -> CustomResponseException
            .resourceNotFoundException("Department with id " + departmentId + " not found!"));

    if (department.getManager() == null) {
      throw CustomResponseException.badRequestException("This department currently has no manager to remove!");
    }

    String managerName = department.getManager().getName();

    department.setManager(null);

    return new GlobalResponse<>(
        "Successfully removed " + managerName + " from managing the " + department.getName() + " department.");
  }

  public GlobalResponse<DepartmentResponse> update(UUID id, DepartmentCreateRequest req) {
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
