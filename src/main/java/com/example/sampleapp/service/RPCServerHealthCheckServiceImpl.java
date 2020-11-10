package com.example.sampleapp.service;

import com.google.protobuf.Empty;
import com.example.sampleapp.generated.grpc.HealthCheckServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class RPCServerHealthCheckServiceImpl
    extends HealthCheckServiceGrpc.HealthCheckServiceImplBase {

  @Override
  public void checkHealth(final Empty request, final StreamObserver<Empty> responseObserver) {
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }
}
