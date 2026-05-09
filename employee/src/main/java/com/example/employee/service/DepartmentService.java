package com.example.employee.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

import com.example.employee.dtos.request.AssignManagerRequest;
import com.example.employee.dtos.request.DepartmentCreateRequest;
import com.example.employee.dtos.response.DepartmentResponse;
import com.example.employee.dtos.response.EmployeeSummaryResponse;
import com.example.employee.entity.Department;
import com.example.employee.entity.Employee;
import com.example.employee.repo.DepartmentRepo;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.shared.CustomResponseException;
import com.example.employee.utils.PaginationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {
  private final DepartmentRepo departmentRepo;
  private final EmployeeRepo employeeRepo;

  private DepartmentResponse toDto(Department department) {

    Employee mgr = department.getManager();

    EmployeeSummaryResponse manager = (mgr == null)
        ? null
        : new EmployeeSummaryResponse(mgr.getId(), mgr.getName());

    return new DepartmentResponse(department.getId(), department.getName(), manager);
  }

  public DepartmentResponse getById(UUID id) {
    Department department = departmentRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException("Department with " + id + " Not found!"));

    return toDto(department);
  }

  public Page<DepartmentResponse> getAll(int page, int size) {

    Pageable pageable = PaginationUtil.createPageable(page, size);

    return departmentRepo.findAll(pageable).map(this::toDto);
  }

  @Transactional
  public String assignManagerToDepartment(UUID departmentId, AssignManagerRequest req) {

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

    return "Successfully assigned " + manager.getName() + " as the manager of " + department.getName();
  }

  public DepartmentResponse create(DepartmentCreateRequest req) {

    Department department = new Department();
    department.setName(req.getName());
    department = departmentRepo.save(department);

    return toDto(department);
  }

  @Transactional
  public String delete(UUID id) {
    if (!departmentRepo.existsById(id)) {
      throw CustomResponseException.resourceNotFoundException("Department with " + id + " Not found!");
    }

    if (employeeRepo.existsByDepartmentId(id)) {
      throw CustomResponseException
          .badRequestException("Cannot Delete the department since employees are registered on it!");
    }
    departmentRepo.deleteById(id);

    return "Department with id " + id + " has been successfully deleted";
  }

  @Transactional
  public String removeManagerFromDepartment(UUID departmentId) {

    Department department = departmentRepo.findById(departmentId)
        .orElseThrow(() -> CustomResponseException
            .resourceNotFoundException("Department with id " + departmentId + " not found!"));

    if (department.getManager() == null) {
      throw CustomResponseException.badRequestException("This department currently has no manager to remove!");
    }

    String managerName = department.getManager().getName();

    department.setManager(null);

    return "Successfully removed " + managerName + " from managing the " + department.getName() + " department.";
  }

  public DepartmentResponse update(UUID id, DepartmentCreateRequest req) {
    Department department = departmentRepo.findById(id)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException("Department with " + id + " Not found!"));
    department.setName(req.getName());

    department = departmentRepo.save(department);

    return (toDto(department));
  }

  @Transactional(readOnly = true)
  public Page<EmployeeSummaryResponse> getDepartmentWithEmployees(UUID departmentId, int page, int size) {

    Pageable pageable = PaginationUtil.createPageable(page, size);

    Page<EmployeeSummaryResponse> employeesPage = employeeRepo.findAllByDepartmentId(departmentId, pageable)
        .map(emp -> new EmployeeSummaryResponse(
            emp.getId(),
            emp.getName()));

    if (employeesPage.getTotalElements() == 0) {
      throw CustomResponseException.resourceNotFoundException("This department has no employee");
    }

    if (page > employeesPage.getTotalPages() && employeesPage.getTotalPages() > 0) {
      throw CustomResponseException.badRequestException("Page out of range.");
    }

    return employeesPage;
  }
}
