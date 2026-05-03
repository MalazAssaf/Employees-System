package com.example.employee.service;

import java.util.List;
import java.util.UUID;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employee.dtos.request.EmployeeRequest;
import com.example.employee.dtos.response.EmployeeInDepartmentResponse;
import com.example.employee.dtos.response.EmployeeResponse;
import com.example.employee.dtos.response.EmployeeUnderManagerResponse;
import com.example.employee.entity.ActivationToken;
import com.example.employee.entity.Department;
import com.example.employee.entity.Employee;
import com.example.employee.repo.ActivationTokenRepo;
import com.example.employee.repo.DepartmentRepo;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.LeaveRequestRepo;
import com.example.employee.repo.UserRepo;
import com.example.employee.shared.CustomResponseException;
import com.example.employee.shared.GlobalResponse;
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
    Employee employee = employeeRepo.findById(id).orElseThrow(() -> CustomResponseException
        .resourceNotFoundException("Employee with id: " + id + " not found!"));
    return new GlobalResponse<>(toDto(employee));
  }

  public GlobalResponse<List<EmployeeResponse>> getAll() {
    List<EmployeeResponse> employees = employeeRepo.findAll().stream().map(this::toDto).toList();
    return new GlobalResponse<>(employees);
  }

  @Transactional
  public GlobalResponse<EmployeeResponse> create(EmployeeRequest req) {
    Employee employee = new Employee();

    Department department = departmentRepo.findById(req.getDepartmentId())
        .orElseThrow(() -> CustomResponseException
            .resourceNotFoundException("Department with " + req.getDepartmentId() + " Not found!"));

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

    employee = employeeRepo.save(employee);

    String generatedToken = UUID.randomUUID().toString();
    ActivationToken activationToken = new ActivationToken();

    activationToken.setToken(generatedToken);
    activationToken.setExpiryDate(new Date(System.currentTimeMillis() + jwtExpiration));
    activationToken.setEmployee(employee);

    activationTokenRepo.save(activationToken);

    emailService.sendActivationEmail(req.getEmail(), generatedToken);

    return new GlobalResponse<EmployeeResponse>(toDto(employee));
  }

  @Transactional
  public GlobalResponse<String> delete(UUID id) {

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

    return new GlobalResponse<>(
        "Employee with id " + id + " and their associated user account have been successfully deleted");
  }

  public GlobalResponse<EmployeeResponse> update(UUID id, EmployeeRequest req) {
    Employee employee = employeeRepo.findById(id).orElseThrow(() -> CustomResponseException
        .resourceNotFoundException("Employee with id: " + id + " not found!"));

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

  public GlobalResponse<EmployeeUnderManagerResponse> getAllEmployeesUnderManager(UUID managerId) {

    Employee manager = employeeRepo.findById(managerId)
        .orElseThrow(
            () -> CustomResponseException.resourceNotFoundException("Manager with id " + managerId + " not found!"));

    if (!employeeRepo.existsByManagerId(manager.getId())) {
      throw CustomResponseException
          .badRequestException("This employee does not manage any team members");
    }

    List<EmployeeInDepartmentResponse> employees = employeeRepo.findAllByManagerId(managerId)
        .stream()
        .map(employee -> new EmployeeInDepartmentResponse(employee.getId(),
            employee.getName(), employee.getEmail()))
        .toList();

    if (employees.isEmpty()) {
      throw CustomResponseException
          .badRequestException("This employee currently has no team members assigned to them.");
    }

    EmployeeUnderManagerResponse response = new EmployeeUnderManagerResponse(
        manager.getId(),
        manager.getName(),
        manager.getDepartment().getId(),
        manager.getDepartment().getName(),
        employees);

    return new GlobalResponse<>(response);
  }

}
