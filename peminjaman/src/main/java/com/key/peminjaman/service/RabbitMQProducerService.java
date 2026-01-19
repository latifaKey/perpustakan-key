package com.key.peminjaman.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.key.peminjaman.dto.PeminjamanEventDTO;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * RabbitMQ Producer Service dengan Structured Logging
 */
@Service
public class RabbitMQProducerService {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQProducerService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    public void sendPeminjamanEvent(PeminjamanEventDTO event) {
        String correlationId = MDC.get("correlationId");
        if (correlationId != null) {
            event.setCorrelationId(correlationId);
        }

        log.info("Publishing event to RabbitMQ",
                kv("eventType", event.getEventType()),
                kv("exchange", exchangeName),
                kv("routingKey", routingKey),
                kv("eventCorrelationId", event.getCorrelationId()));

        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
            log.info("Event published successfully",
                    kv("eventType", event.getEventType()),
                    kv("status", "SUCCESS"));
        } catch (Exception e) {
            log.error("Failed to publish event",
                    kv("eventType", event.getEventType()),
                    kv("status", "FAILED"),
                    kv("error", e.getMessage()), e);
        }
    }
}
