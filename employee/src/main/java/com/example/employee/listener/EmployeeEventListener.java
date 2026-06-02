package com.example.employee.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.employee.event.EmployeeCreatedEvent;
import com.example.employee.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeEventListener {

  private final EmailService emailService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleEmployeeCreated(
      EmployeeCreatedEvent event) {

    emailService.sendActivationEmail(
        event.email(),
        event.token());
  }
}