package com.example.employee.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee.entity.User;

public interface UserRepo extends JpaRepository<User, UUID> {
  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  void deleteByEmployeeId(UUID id);
}
