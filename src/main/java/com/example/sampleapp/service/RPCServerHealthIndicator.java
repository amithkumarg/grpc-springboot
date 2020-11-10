package com.example.sampleapp.service;

import com.google.protobuf.Empty;
import com.example.sampleapp.generated.grpc.HealthCheckServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RPCServerHealthIndicator implements HealthIndicator {

  private static final String LOCALHOST = "localhost";
  private final HealthCheckServiceGrpc.HealthCheckServiceBlockingStub blockingStub;

  public RPCServerHealthIndicator(@Value("${grpc.port}") final String port) {
    blockingStub =
        HealthCheckServiceGrpc.newBlockingStub(
            ManagedChannelBuilder.forAddress(LOCALHOST, Integer.parseInt(port))
                .usePlaintext()
                .build());
  }

  @Override
  public Health health() {
    Health result;
    try {
      blockingStub.checkHealth(Empty.newBuilder().build());
      result = Health.up().build();
    } catch (Exception e) {
      log.error("RPC health check call failed", e);
      result = Health.down().build();
    }
    return result;
  }
}
