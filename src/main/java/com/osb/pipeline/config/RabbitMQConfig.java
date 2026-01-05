package com.osb.pipeline.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Define input queues for the 6 pipelines
    @Bean
    public Queue orderProcessingQueue() {
        return new Queue("order-processing-queue", true);
    }
    
    @Bean
    public Queue paymentProcessingQueue() {
        return new Queue("payment-processing-queue", true);
    }
    
    @Bean
    public Queue customerNotificationQueue() {
        return new Queue("customer-notification-queue", true);
    }
    
    @Bean
    public Queue inventoryUpdateQueue() {
        return new Queue("inventory-update-queue", true);
    }
    
    @Bean
    public Queue auditLoggingQueue() {
        return new Queue("audit-logging-queue", true);
    }
    
    @Bean
    public Queue errorHandlingQueue() {
        return new Queue("error-handling-queue", true);
    }
    
    // Define output queues (for queue-based routing)
    @Bean
    public Queue notificationOutputQueue() {
        return new Queue("notification-output-queue", true);
    }
    
    @Bean
    public Queue auditOutputQueue() {
        return new Queue("audit-output-queue", true);
    }
    
    @Bean
    public Queue errorOutputQueue() {
        return new Queue("error-output-queue", true);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMaxConcurrentConsumers(10);
        factory.setConcurrentConsumers(5);
        return factory;
    }
}
