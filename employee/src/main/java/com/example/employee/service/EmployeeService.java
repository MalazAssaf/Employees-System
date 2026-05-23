package com.example.employee.service;

import java.util.UUID;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employee.dtos.request.EmployeeRequest;
import com.example.employee.dtos.request.EmployeeUpdateRequest;
import com.example.employee.dtos.response.DepartmentResponse;
import com.example.employee.dtos.response.EmployeeResponse;
import com.example.employee.dtos.response.EmployeeSummaryResponse;
import com.example.employee.dtos.response.ManagerResponse;
import com.example.employee.entity.ActivationToken;
import com.example.employee.entity.Department;
import com.example.employee.entity.Employee;
import com.example.employee.repo.ActivationTokenRepo;
import com.example.employee.repo.DepartmentRepo;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.LeaveRequestRepo;
import com.example.employee.repo.UserRepo;
import com.example.employee.shared.CustomResponseException;
import com.example.employee.utils.PaginationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepo employeeRepo;
  private final DepartmentRepo departmentRepo;
  private final LeaveRequestRepo leaveRequestRepo;
  private final ActivationTokenRepo activationTokenRepo;
  private final UserRepo userRepo;
  private final EmailService emailService;

  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;

  private EmployeeResponse toDto(Employee employee) {

    EmployeeSummaryResponse managerSummary = null;

    if (employee.getDepartment().getManager() != null) {
      Employee deptManager = employee.getDepartment().getManager();

      // if the employee is their own department manager, don't put as manager
      if (!deptManager.getId().equals(employee.getId())) {
        managerSummary = new EmployeeSummaryResponse(deptManager.getId(), deptManager.getName());
      }
    }

    DepartmentResponse departmentResponse = new DepartmentResponse(
        employee.getDepartment().getId(),
        employee.getDepartment().getName(),
        managerSummary // null if employee is their own manager
    );

    return new EmployeeResponse(
        employee.getId(),
        employee.getName(),
        employee.getEmail(),
        employee.getPhoneNumber(),
        employee.getHireDate(),
        employee.getIsActivated(),
        employee.getRole(),
        departmentResponse);
  }

  public EmployeeResponse getById(UUID id) {
    Employee employee = employeeRepo.findById(id).orElseThrow(() -> CustomResponseException
        .resourceNotFoundException("Employee with id: " + id + " not found!"));
    return toDto(employee);
  }

  public Page<EmployeeResponse> getAll(int page, int size) {
    Pageable pageable = PaginationUtil.createPageable(page, size);

    return employeeRepo.findAll(pageable)
        .map(this::toDto);
  }

  @Transactional
  public EmployeeResponse create(EmployeeRequest req) {
    Employee employee = new Employee();

    Department department = departmentRepo.findById(req.getDepartmentId())
        .orElseThrow(() -> CustomResponseException
            .resourceNotFoundException("Department with " + req.getDepartmentId() + " Not found!"));

    employee.setName(req.getName());
    employee.setEmail(req.getEmail());
    employee.setPhoneNumber(req.getPhoneNumber());
    employee.setHireDate(req.getHireDate());
    employee.setDepartment(department);
    employee.setRole(req.getRole());

    if (department.getManager() != null) {
      employee.setManager(department.getManager());
    }

    employee = employeeRepo.save(employee);

    String generatedToken = UUID.randomUUID().toString();
    ActivationToken activationToken = new ActivationToken();

    activationToken.setToken(generatedToken);
    activationToken.setExpiryDate(new Date(System.currentTimeMillis() + jwtExpiration));
    activationToken.setEmployee(employee);

    activationTokenRepo.save(activationToken);

    emailService.sendActivationEmail(req.getEmail(), generatedToken);

    return toDto(employee);
  }

  @Transactional
  public String delete(UUID id) {

    if (!employeeRepo.existsById(id)) {
      throw CustomResponseException.resourceNotFoundException("Employee with id " + id + " not found!");
    }

    if (departmentRepo.existsByManagerId(id)) {
      throw CustomResponseException.badRequestException(
          "Cannot delete employee: They are currently managing a department. Please remove them from department management first.");
    }

    if (employeeRepo.existsByManagerId(id)) {
      throw CustomResponseException.badRequestException(
          "Cannot delete employee: They are managing other employees. Please reassign their team to another manager first.");
    }

    if (leaveRequestRepo.existsByEmployeeId(id)) {
      throw CustomResponseException.badRequestException(
          "Cannot delete employee: They have existing leave requests. Deleting them would corrupt historical HR data.");
    }

    // Delete the user account related to it
    userRepo.deleteByEmployeeId(id);

    employeeRepo.deleteById(id);

    return "Employee with id " + id + " and their associated user account have been successfully deleted";
  }

  public EmployeeResponse update(UUID id, EmployeeUpdateRequest req) {
    Employee employee = employeeRepo.findById(id)
        .orElseThrow(() -> CustomResponseException
            .resourceNotFoundException("Employee with id: " + id + " not found!"));

    Department department = departmentRepo.findById(req.departmentId())
        .orElseThrow(() -> CustomResponseException
            .resourceNotFoundException("Department with " + req.departmentId() + " Not found!"));

    employee.setName(req.name());
    employee.setPhoneNumber(req.phoneNumber());
    employee.setDepartment(department);
    employee.setRole(req.role());

    // manager always comes from department
    if (department.getManager() != null) {
      if (employee.getId().equals(department.getManager().getId())) {
        throw CustomResponseException
            .badRequestException("Invalid Action: Employee cannot be their own manager!");
      }
      employee.setManager(department.getManager());
    } else {
      employee.setManager(null); // department has no manager
    }

    employee = employeeRepo.save(employee);

    return toDto(employee);
  }

  @Cacheable(value = "managerInfo", key = "#managerId")
  @Transactional(readOnly = true)
  public ManagerResponse getManagerInfo(UUID managerId) {

    Employee manager = employeeRepo.findById(managerId).orElseThrow(
        () -> CustomResponseException.resourceNotFoundException("Manager with id " + managerId + " not found!"));

    EmployeeSummaryResponse managerSummary = new EmployeeSummaryResponse(manager.getId(), manager.getName());

    return new ManagerResponse(
        managerSummary,
        manager.getDepartment().getId(),
        manager.getDepartment().getName());
  }

  @Transactional(readOnly = true)
  public Page<EmployeeSummaryResponse> getEmployeesUnderManager(UUID managerId, int page, int size) {

    Pageable pageable = PaginationUtil.createPageable(page, size);

    Page<EmployeeSummaryResponse> employeesPage = employeeRepo.findAllByManagerId(managerId, pageable)
        .map(emp -> new EmployeeSummaryResponse(
            emp.getId(),
            emp.getName()));

    return employeesPage;
  }

}
