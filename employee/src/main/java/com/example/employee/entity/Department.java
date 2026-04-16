package com.example.employee.entity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Department {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID id;
  @Column(name = "name", unique = true)
  private String name;
  @OneToMany(mappedBy = "department")
  private List<Employee> employees;
}
