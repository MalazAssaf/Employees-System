package com.example.employee.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee.entity.User;

public interface UserRepo extends JpaRepository<User, UUID> {

}
