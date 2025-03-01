package com.task.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQQueueConfig {

    @Bean
    public Queue currencyQueue() {
        return new Queue("currency-queue", true);
    }

    @Bean
    public Binding binding(Queue currencyQueue, Exchange currencyExchange) {
        return BindingBuilder.bind(currencyQueue).to(currencyExchange).with("").noargs();
    }
}
