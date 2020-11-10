package com.example.sampleapp.service;

import com.google.protobuf.Empty;
import com.example.sampleapp.generated.grpc.HealthCheckServiceGrpc;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RPCServerHealthIndicatorTest {

  @Test
  void testHealthReportedDownForFailedRPCConnection() {
    final RPCServerHealthIndicator subject = new RPCServerHealthIndicator("123");
    assertEquals(Health.down().build(), subject.health(), "Service was expected to be down");
  }

  @Test
  @SuppressFBWarnings(
      value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
      justification = "reported bug in github for this warning with try with resources")
  void testHealthReportedUpForSuccessfulRPCConnection() {
    try (MockedStatic<HealthCheckServiceGrpc> mockedRPCService =
        Mockito.mockStatic(HealthCheckServiceGrpc.class)) {
      // arrange
      final HealthCheckServiceGrpc.HealthCheckServiceBlockingStub blockingStub =
          Mockito.mock(HealthCheckServiceGrpc.HealthCheckServiceBlockingStub.class);
      mockedRPCService
          .when(() -> HealthCheckServiceGrpc.newBlockingStub(Mockito.any()))
          .thenReturn(blockingStub);
      Mockito.when(blockingStub.checkHealth(Mockito.any())).thenReturn(Empty.newBuilder().build());
      final RPCServerHealthIndicator subject = new RPCServerHealthIndicator("123");
      // act & assert
      assertEquals(Health.up().build(), subject.health(), "Service didn't report healthy");
    }
  }
}
