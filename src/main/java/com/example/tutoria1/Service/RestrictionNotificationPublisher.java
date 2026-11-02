package com.example.tutoria1.Service;

import com.example.tutoria1.Dto.Notificacion.RestriccionConductorEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RestrictionNotificationPublisher {

    private static final Logger logger =
            LoggerFactory.getLogger(RestrictionNotificationPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String queueName;

    public RestrictionNotificationPublisher(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            @Value("${notifications.rabbitmq.queue}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.queueName = queueName;
    }

    public void publicar(RestriccionConductorEvent evento) {
        try {
            String mensaje = objectMapper.writeValueAsString(evento);
            rabbitTemplate.convertAndSend(queueName, mensaje);
            logger.info(
                    "Notificación de restricción enviada a RabbitMQ para el conductor {}",
                    evento.conductorId());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "No se pudo serializar la notificación de restricción",
                    exception);
        }
    }
}
