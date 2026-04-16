package com.example.employee.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequest {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID id;
  private LocalDate startDate;
  private LocalDate endDate;
  private String reason;

  @Enumerated(EnumType.STRING)
  private RequestStatus status = RequestStatus.PENDING;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id")
  @JsonProperty("employeeId")
  private Employee employee;
}
