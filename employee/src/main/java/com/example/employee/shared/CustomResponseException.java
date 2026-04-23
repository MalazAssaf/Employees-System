package com.example.employee.shared;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomResponseException extends RuntimeException {
  private final HttpStatus status;
  private final String message;

  public static CustomResponseException badCredentials() {
    return new CustomResponseException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect");
  }

  public static CustomResponseException resourceNotFoundException(String message) {
    return new CustomResponseException(HttpStatus.NOT_FOUND, message);
  }

  public static CustomResponseException badRequestException(String message) {
    return new CustomResponseException(HttpStatus.BAD_REQUEST, message);
  }
}
