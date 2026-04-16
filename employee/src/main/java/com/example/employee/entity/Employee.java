package com.example.employee.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID id;
  private String name;
  @Column(unique = true)
  private String email;
  @Column(unique = true)
  private String phoneNumber;
  private LocalDate hireDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  @JsonProperty("departmentId")
  private Department department;
}
