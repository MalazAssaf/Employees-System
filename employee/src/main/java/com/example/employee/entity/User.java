package com.example.employee.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_account")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID id;
  @Column(unique = true)
  private String username;
  private String password;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", unique = true)
  private Employee employee;
}
