package com.example.tutoria1.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RestrictionRabbitConfig {

    private static final String DEAD_LETTER_EXCHANGE =
            "conductor.restriction.dlx";
    private static final String DEAD_LETTER_QUEUE =
            "conductor.restriction.notifications.dlq";
    private static final String DEAD_LETTER_ROUTING_KEY =
            "conductor.restriction.failed";

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    ObjectMapper notificationObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    Queue restrictionNotificationQueue(
            @Value("${notifications.rabbitmq.queue}") String queueName) {
        return QueueBuilder.durable(queueName)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    DirectExchange restrictionDeadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    Queue restrictionNotificationDeadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    Binding restrictionNotificationDeadLetterBinding(
            @Qualifier("restrictionNotificationDeadLetterQueue")
            Queue restrictionNotificationDeadLetterQueue,
            DirectExchange restrictionDeadLetterExchange) {
        return BindingBuilder
                .bind(restrictionNotificationDeadLetterQueue)
                .to(restrictionDeadLetterExchange)
                .with(DEAD_LETTER_ROUTING_KEY);
    }
}
