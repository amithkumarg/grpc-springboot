package com.example.sampleapp.service;

import com.google.protobuf.Empty;
import com.example.sampleapp.util.FakeStreamObserver;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RPCServerHealthCheckServiceImplTest {
  private final RPCServerHealthCheckServiceImpl subject =
      new RPCServerHealthCheckServiceImpl();

  @Test
  void testCheckHealthEmptyResponse() {
    final FakeStreamObserver<Empty> responseObserver = new FakeStreamObserver<>();
    subject.checkHealth(Empty.newBuilder().build(), responseObserver);
    assertEquals(
        Empty.newBuilder().build(),
        responseObserver.getResponse(),
        "Didn't receive empty response");
  }
}
