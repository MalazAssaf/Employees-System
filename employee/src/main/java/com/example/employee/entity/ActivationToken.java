package com.example.employee.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ActivationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true)
  private String token;

  private Date expiryDate;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", unique = true)
  private Employee employee;
}