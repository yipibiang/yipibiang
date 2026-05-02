package com.example.userservice.infrastructure.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenTelemetry 配置类
 */
@Configuration
public class OpenTelemetryConfig {

    @Value("${otel.service.name:user-service}")
    private String serviceName;

    @Bean
    public OpenTelemetry openTelemetry() {
        return OpenTelemetrySdk.builder().buildAndRegisterGlobal();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(serviceName);
    }
}