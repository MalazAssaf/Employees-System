package com.example.employee.utils;

import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.example.employee.entity.Employee;
import com.example.employee.entity.LeaveRequest;
import com.example.employee.entity.User;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.repo.LeaveRequestRepo;
import com.example.employee.shared.CustomResponseException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

  private final EmployeeRepo employeeRepo;
  private final LeaveRequestRepo leaveRequestRepo;

  public boolean isManager(UUID employeeId) {
    Employee currentEmployee = getCurrentUserEmployee();
    Employee targetEmployee = employeeRepo.findById(employeeId).orElseThrow(() -> CustomResponseException
        .resourceNotFoundException("Employee with id: " + employeeId + " not found!"));
    ;

    if (targetEmployee.getManager() == null) {
      throw CustomResponseException.badRequestException("This employee has no assigned manager.");
    }

    return targetEmployee.getManager().getId().equals(currentEmployee.getId());
  }

  public boolean leaveRequestAccessedByManager(UUID leaveRequestId) {
    LeaveRequest leaveRequest = leaveRequestRepo.findById(leaveRequestId).orElseThrow(() -> CustomResponseException
        .resourceNotFoundException("Leave Request with id: " + leaveRequestId + " not found!"));

    return isManager(leaveRequest.getEmployee().getId());
  }

  private Employee getCurrentUserEmployee() {
    final User currentUser = (User) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

    if (currentUser.getEmployee() == null) {
      throw CustomResponseException.forbiddenException("User is not associated with an employee record.");
    }

    return currentUser.getEmployee();
  }

}