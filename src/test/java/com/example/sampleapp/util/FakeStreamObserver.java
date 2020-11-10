package com.example.sampleapp.util;

import io.grpc.stub.StreamObserver;
import lombok.Data;

@Data
public class FakeStreamObserver<T> implements StreamObserver<T> {

  private T response;

  @Override
  public void onNext(final T value) {
    response = value;
  }

  @Override
  public void onError(final Throwable throwable) {
    // ignore
  }

  @Override
  public void onCompleted() {
    // ignore
  }
}
