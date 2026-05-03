package com.example.employee.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
  private final JavaMailSender mailSender;

  @Value("${backend.origin}")
  private String ORIGIN;

  @Value("${spring.mail.username}")
  private String EMAIL;

  public void sendActivationEmail(String toEmail, String token) {
    String activationLink = ORIGIN + "?token=" + token;
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(EMAIL);
    message.setTo(toEmail);
    message.setSubject("Welcome to the Company - Account Activation");

    message.setText(
        "Hello You have been successfully added to our system. Please activate your account and set your password using the link below:\n\n"
            +
            activationLink + "\n\n" +
            "Note: This link is valid for 24 hours only.\n\n" +
            "Best Regards,\nSystem Admin");

    mailSender.send(message);
  }
}
