package com.example.employee.specification;

import org.springframework.data.jpa.domain.Specification;

public abstract class BaseSpecification<T> {

  protected Specification<T> likeName(String name) {
    return (root, query, cb) -> hasText(name)
        ? cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
        : null;
  }

  protected Specification<T> likeField(String fieldName, String value) {
    return (root, query, cb) -> hasText(value)
        ? cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%")
        : null;
  }

  protected <V> Specification<T> equalField(String fieldName, V value) {
    return (root, query, cb) -> value != null
        ? cb.equal(root.get(fieldName), value)
        : null;
  }

  protected <V extends Comparable<V>> Specification<T> greaterThanOrEqual(String fieldName, V value) {
    return (root, query, cb) -> value != null
        ? cb.greaterThanOrEqualTo(root.get(fieldName), value)
        : null;
  }

  protected <V extends Comparable<V>> Specification<T> lessThanOrEqual(String fieldName, V value) {
    return (root, query, cb) -> value != null
        ? cb.lessThanOrEqualTo(root.get(fieldName), value)
        : null;
  }

  protected boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  public abstract Specification<T> apply(Object filter);
}