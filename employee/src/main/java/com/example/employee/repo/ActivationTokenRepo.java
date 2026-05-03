package com.example.employee.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee.entity.ActivationToken;

public interface ActivationTokenRepo extends JpaRepository<ActivationToken, UUID> {
  Optional<ActivationToken> findByToken(String token);
}
