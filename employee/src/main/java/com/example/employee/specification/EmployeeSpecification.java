package com.example.employee.specification;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.example.employee.entity.Employee;
import com.example.employee.filter.EmployeeFilter;

@Component
public class EmployeeSpecification extends BaseSpecification<Employee> {

  @Override
  public Specification<Employee> apply(Object filter) {
    return apply((EmployeeFilter) filter);
  }

  public Specification<Employee> apply(EmployeeFilter filter) {
    return Specification.where(likeName(filter.getName()))
        .and(likeField("email", filter.getEmail()))
        .and(likeField("phoneNumber", filter.getPhoneNumber()))
        .and(equalField("isActivated", filter.getIsActivated()))
        .and(equalField("role", filter.getRole()))
        .and(hasDepartment(filter.getDepartmentId()))
        .and(greaterThanOrEqual("hireDate", filter.getHireDateFrom()))
        .and(lessThanOrEqual("hireDate", filter.getHireDateTo()));
  }

  private Specification<Employee> hasDepartment(String departmentId) {
    return (root, query, criteriaBuilder) -> {
      if (!hasText(departmentId))
        return null;
      return criteriaBuilder.equal(
          root.get("department").get("id"), UUID.fromString(departmentId)); // Inner Join
    };
  }

}
