
package com.example.employee.shared;

import java.util.List;
import java.util.stream.Stream;

import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionResponse {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<GlobalResponse<Void>> handleNoResourceFoundException(ResourceNotFoundException ex) {

    var errors = List.of(new GlobalResponse.ErrorItem(ex.getMessage()));

    return new ResponseEntity<>(new GlobalResponse<>(errors), ex.getStatus());
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

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<GlobalResponse<Void>> handleBadRequest(BadRequestException ex) {
    var errors = List.of(new GlobalResponse.ErrorItem(ex.getMessage()));
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