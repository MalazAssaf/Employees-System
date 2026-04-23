package com.example.employee.shared;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.jsonwebtoken.JwtException;

@ControllerAdvice
public class GlobalExceptionResponse {

  @ExceptionHandler(CustomResponseException.class)
  public ResponseEntity<GlobalResponse<Void>> handleCustomException(CustomResponseException ex) {

    var errors = List.of(new GlobalResponse.ErrorItem(ex.getMessage()));

    return new ResponseEntity<>(new GlobalResponse<>(errors), ex.getStatus());
  }

  @ExceptionHandler({ BadCredentialsException.class, AuthenticationException.class })
  public ResponseEntity<GlobalResponse<Void>> handleAuthenticationException() {

    var errors = List.of(new GlobalResponse.ErrorItem("Username or password is incorrect"));

    return new ResponseEntity<>(new GlobalResponse<>(errors), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<GlobalResponse<Void>> handleJwtExceptions(JwtException ex) {

    var errors = List.of(new GlobalResponse.ErrorItem("Invalid or expired Token! Please login again"));

    return new ResponseEntity<>(new GlobalResponse<>(errors), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<GlobalResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {

    var errors = List.of(new GlobalResponse.ErrorItem(ex.getMessage()));

    return new ResponseEntity<>(new GlobalResponse<>(errors), HttpStatus.FORBIDDEN);

  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<GlobalResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {

    var errors = Stream.concat(
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> new GlobalResponse.ErrorItem(err.getField() + ": " + err.getDefaultMessage())),

        ex.getBindingResult().getGlobalErrors().stream()
            .map(err -> new GlobalResponse.ErrorItem(err.getDefaultMessage())))
        .toList();

    return new ResponseEntity<>(new GlobalResponse<>(errors), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<GlobalResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {

    var errors = List.of(new GlobalResponse.ErrorItem(
        "Duplicate or invalid data provided" + ex.getMessage()));
    ;

    return new ResponseEntity<>(new GlobalResponse<>(errors),
        HttpStatus.CONFLICT);
  }
}