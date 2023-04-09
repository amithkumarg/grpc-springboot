package com.example.sampleapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppHealthCheck {
  private final HealthEndpoint healthEndpoint;

  @GetMapping("/health")
  public ResponseEntity<Void> healthCheck() {
    if (Status.UP.equals(healthEndpoint.health().getStatus())) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
  }
}
