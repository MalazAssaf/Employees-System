package com.example.employee.shared;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResourceNotFoundException extends RuntimeException {
  private final HttpStatus status;

  public ResourceNotFoundException(String message) {
    super(message);
    status = HttpStatus.NOT_FOUND;
  }
}
