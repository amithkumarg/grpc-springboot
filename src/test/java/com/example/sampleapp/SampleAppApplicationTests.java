package com.example.sampleapp;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.lognet.springboot.grpc.context.LocalRunningGrpcPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;

import java.util.Optional;

public abstract class SampleAppApplicationTests {

  static final MSSQLServerContainer MSSQL_CONTAINER;

  static {
    MSSQL_CONTAINER =
        new MSSQLServerContainer("mcr.microsoft.com/mssql/server:latest").acceptLicense();
    MSSQL_CONTAINER.start();
  }

  protected ManagedChannel channel;

  @LocalRunningGrpcPort private int grpcPort;

  @DynamicPropertySource
  static void mssqlProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", MSSQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", MSSQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", MSSQL_CONTAINER::getPassword);
  }

  @BeforeAll
  void classSetup() {
    channel = ManagedChannelBuilder.forAddress("localhost", grpcPort).usePlaintext().build();
    beforeAll();
  }

  @AfterAll
  void classCleanUp() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdownNow);
    afterAll();
  }

  protected abstract void beforeAll();

  protected abstract void afterAll();
}
