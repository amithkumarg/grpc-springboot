package com.example.sampleapp.config;

import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtelConfiguration {

  @Bean
  public TextMapPropagator jaegerPropagator() {
    return JaegerPropagator.getInstance();
  }

  @Bean
  // TODO remove when OTLP exporter auto-configuration will be released
  // https://github.com/spring-projects/spring-boot/pull/34508
  public SpanExporter otlpExporter() {
    return OtlpGrpcSpanExporter.getDefault();
  }
}
