package com.example.sampleapp.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import org.springframework.stereotype.Component;

@Component
public class OtelTraceServerInterceptor implements ServerInterceptor {
  private final ServerInterceptor grpcTelemetryInterceptor;

  public OtelTraceServerInterceptor(OpenTelemetry openTelemetry) {
    this.grpcTelemetryInterceptor = GrpcTelemetry.create(openTelemetry).newServerInterceptor();
  }

  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> serverCall,
      Metadata metadata,
      ServerCallHandler<ReqT, RespT> serverCallHandler) {
    return grpcTelemetryInterceptor.interceptCall(serverCall, metadata, serverCallHandler);
  }
}
