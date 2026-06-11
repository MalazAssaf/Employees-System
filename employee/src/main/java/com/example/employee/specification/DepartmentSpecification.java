package com.example.employee.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.example.employee.entity.Department;
import com.example.employee.entity.Employee;
import com.example.employee.filter.DepartmentFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Component
public class DepartmentSpecification extends BaseSpecification<Department> {

  @Override
  public Specification<Department> apply(Object filter) {
    return apply((DepartmentFilter) filter);
  }

  public Specification<Department> apply(DepartmentFilter filter) {
    return Specification.where(likeName(filter.getName()))
        .and(hasManagerSpec(filter.getHasManager()))
        .and(minEmployees(filter.getMinEmployees()))
        .and(maxEmployees(filter.getMaxEmployees()));
  }

  private Specification<Department> hasManagerSpec(Boolean hasManager) {
    if (hasManager == null)
      return (root, query, cb) -> null;

    return (root, query, cb) -> hasManager
        ? cb.isNotNull(root.get("manager"))
        : cb.isNull(root.get("manager"));
  }

  private Subquery<Long> employeeCountSubquery(Root<Department> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Subquery<Long> subquery = query.subquery(Long.class);
    Root<Employee> employeeRoot = subquery.from(Employee.class);
    subquery.select(criteriaBuilder.count(employeeRoot))
        .where(criteriaBuilder.equal(employeeRoot.get("department"), root));
    return subquery;
  }

  private Specification<Department> minEmployees(Integer min) {
    return (root, query, criteriaBuilder) -> min == null ? null
        : criteriaBuilder.greaterThanOrEqualTo(
            employeeCountSubquery(root, query, criteriaBuilder), (long) min);
  }

  private Specification<Department> maxEmployees(Integer max) {
    return (root, query, criteriaBuilder) -> max == null ? null
        : criteriaBuilder.lessThanOrEqualTo(
            employeeCountSubquery(root, query, criteriaBuilder), (long) max);
  }

}
