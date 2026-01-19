package com.key.peminjaman.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.key.peminjaman.dto.PeminjamanEventDTO;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * RabbitMQ Consumer Service dengan Structured Logging
 */
@Service
public class RabbitMQConsumerService {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConsumerService.class);

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumePeminjamanEvent(PeminjamanEventDTO event) {
        // Set correlation-id from event to MDC for tracing
        if (event.getCorrelationId() != null) {
            MDC.put("correlationId", event.getCorrelationId());
        }

        log.info("Received event from RabbitMQ",
                kv("eventType", event.getEventType()),
                kv("eventCorrelationId", event.getCorrelationId()));

        try {
            switch (event.getEventType()) {
                case "PEMINJAMAN_CREATED":
                    handlePeminjamanCreated(event);
                    break;
                case "PEMINJAMAN_UPDATED":
                    log.info("Processing update event", kv("eventType", event.getEventType()));
                    break;
                default:
                    log.warn("Unknown event type received", kv("eventType", event.getEventType()));
            }
            log.info("Event processed successfully",
                    kv("eventType", event.getEventType()),
                    kv("status", "SUCCESS"));
        } catch (Exception e) {
            log.error("Failed to process event",
                    kv("eventType", event.getEventType()),
                    kv("status", "FAILED"),
                    kv("error", e.getMessage()), e);
        } finally {
            MDC.remove("correlationId");
        }
    }

    private void handlePeminjamanCreated(PeminjamanEventDTO event) {
        log.info("Handling PEMINJAMAN_CREATED event", kv("action", "SEND_EMAIL"));
        if (event.getData() != null && event.getData().getAnggota() != null) {
            emailService.sendPeminjamanNotification(event.getData());
        } else {
            log.warn("Skipping email notification - missing anggota data",
                    kv("reason", "MISSING_DATA"));
        }
    }
}
