package com.key.anggota.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Logging Filter untuk menambahkan MDC context ke setiap HTTP request.
 * MDC (Mapped Diagnostic Context) memungkinkan tracking correlation-id
 * across semua log entries dalam satu request.
 * 
 * Context yang ditambahkan:
 * - correlationId: Unique ID untuk tracking request
 * - serviceName: Nama service yang memproses request
 * - requestUri: URI yang dipanggil
 * - httpMethod: HTTP method (GET, POST, etc.)
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String SERVICE_NAME_KEY = "serviceName";
    private static final String REQUEST_URI_KEY = "requestUri";
    private static final String HTTP_METHOD_KEY = "httpMethod";

    @Value("${spring.application.name:unknown}")
    private String serviceName;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Generate or extract correlation-id from header
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Set MDC values for structured logging
        MDC.put(CORRELATION_ID_KEY, correlationId);
        MDC.put(SERVICE_NAME_KEY, serviceName);
        MDC.put(REQUEST_URI_KEY, request.getRequestURI());
        MDC.put(HTTP_METHOD_KEY, request.getMethod());

        // Pass correlation-id to response header
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clear MDC to prevent memory leaks
            MDC.clear();
        }
    }
}
