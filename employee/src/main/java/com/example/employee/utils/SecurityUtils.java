package com.example.employee.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.example.employee.entity.Employee;
import com.example.employee.entity.User;
import com.example.employee.repo.EmployeeRepo;
import com.example.employee.shared.CustomResponseException;

@Component
public class SecurityUtils {

  @Autowired
  private EmployeeRepo employeeRepo;

  public boolean isManager(UUID employeeId) {
    final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final UUID currentUserId = currentUser.getEmployee().getId();

    Employee employee = employeeRepo.findById(employeeId)
        .orElseThrow(() -> CustomResponseException.resourceNotFoundException(
            "Employee with id" + employeeId + "not found!"));

    if (employee.getManager() == null) {
      return false;
    }

    return employee.getManager().getId().equals(currentUserId);
  }
}
