package com.example.sampleapp.interceptor;

import com.example.sampleapp.exception.CorruptDataException;
import com.example.sampleapp.exception.NotFoundException;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;

@GRpcGlobalInterceptor
@SuppressWarnings(
    "java:S119") // added for sonarqube to ignore naming convention for ReqT & RespT lib classes
public class ExceptionHandler implements ServerInterceptor {

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      final ServerCall<ReqT, RespT> serverCall,
      final Metadata metadata,
      final ServerCallHandler<ReqT, RespT> serverCallHandler) {
    final ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);
    return new ExceptionsListener<>(listener, serverCall, metadata);
  }

  private class ExceptionsListener<ReqT, RespT>
      extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {
    private final ServerCall<ReqT, RespT> serverCall;
    private final Metadata metadata;

    public ExceptionsListener(
        final ServerCall.Listener<ReqT> listener,
        final ServerCall<ReqT, RespT> serverCall,
        final Metadata metadata) {
      super(listener);
      this.serverCall = serverCall;
      this.metadata = metadata;
    }

    @Override
    public void onHalfClose() {
      try {
        super.onHalfClose();
      } catch (RuntimeException ex) {
        handleException(ex, serverCall, metadata);
        throw ex;
      }
    }

    private void handleException(
        final RuntimeException exception,
        final ServerCall<ReqT, RespT> serverCall,
        final Metadata metadata) {
      if (exception instanceof IllegalArgumentException) {
        serverCall.close(Status.INVALID_ARGUMENT.withDescription(exception.getMessage()), metadata);
      } else if (exception instanceof NotFoundException) {
        serverCall.close(Status.NOT_FOUND.withDescription(exception.getMessage()), metadata);
      } else if (exception instanceof CorruptDataException) {
        serverCall.close(Status.DATA_LOSS.withDescription(exception.getMessage()), metadata);
      } else {
        serverCall.close(Status.UNKNOWN.withDescription(exception.getMessage()), metadata);
      }
    }
  }
}
