package com.project.ecommerce.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key.order-created}")
    private String orderCreatedRoutingKey;

    @Value("${app.rabbitmq.routing-key.payment-result}")
    private String paymentResultRoutingKey;

    @Value("${app.rabbitmq.queue.payment-result}")
    private String paymentResultQueue;

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue paymentResultQueue() {
        return new Queue(paymentResultQueue, true);
    }

    @Bean
    public Binding paymentResultBinding() {
        return BindingBuilder
            .bind(paymentResultQueue())
            .to(orderExchange())
            .with(paymentResultRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}