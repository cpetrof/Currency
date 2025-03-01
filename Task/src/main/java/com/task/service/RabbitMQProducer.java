package com.task.service;
import com.task.dto.request.RequestLogMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
@Service
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendRequestLog(String serviceName, String requestId, String clientId) {
        RequestLogMessage message = new RequestLogMessage(serviceName, requestId, Instant.now().toEpochMilli(), clientId);

        rabbitTemplate.convertAndSend(exchange, "", message);

    }
}
