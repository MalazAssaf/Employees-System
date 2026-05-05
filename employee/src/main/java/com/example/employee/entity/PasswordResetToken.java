package com.example.employee.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PasswordResetToken {
  @Id
  @GeneratedValue
  private UUID id;

  @Column(unique = true)
  private String token;

  private LocalDateTime expiryDate;

  @OneToOne
  @JoinColumn(name = "user_id", unique = true)
  private User user;

}
