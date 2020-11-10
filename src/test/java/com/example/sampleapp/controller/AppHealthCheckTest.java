package com.example.sampleapp.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppHealthCheck.class)
class AppHealthCheckTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private HealthEndpoint healthEndpoint;

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testStatusReportedOKForHealthyEndpoint() throws Exception {
    Mockito.when(healthEndpoint.health()).thenReturn(Health.up().build());
    mockMvc.perform(get("/health")).andExpect(status().isOk());
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testStatusReportedServiceUnavailableForUnHealthyEndpoint() throws Exception {
    Mockito.when(healthEndpoint.health()).thenReturn(Health.down().build());
    mockMvc.perform(get("/health")).andExpect(status().isServiceUnavailable());
  }
}