package com.example.tutoria1.Component;

import com.example.tutoria1.Dto.Notificacion.RestriccionConductorEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@ConditionalOnProperty(
        name = "notifications.email.enabled",
        havingValue = "true")
public class RestrictionNotificationListener {

    private static final Logger logger =
            LoggerFactory.getLogger(RestrictionNotificationListener.class);

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;
    private final String senderAddress;

    public RestrictionNotificationListener(
            JavaMailSender mailSender,
            ObjectMapper objectMapper,
            @Value("${notifications.email.from:}") String senderAddress) {
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
        this.senderAddress = senderAddress;
    }

    @RabbitListener(queues = "${notifications.rabbitmq.queue}")
    public void enviarNotificacion(String mensaje) {
        RestriccionConductorEvent evento = deserializar(mensaje);

        if (senderAddress.isBlank()) {
            throw new IllegalStateException(
                    "Configure MAIL_FROM para habilitar el envío de correos");
        }

        if (evento.correo() == null || evento.correo().isBlank()) {
            throw new IllegalArgumentException(
                    "El conductor no tiene una dirección de correo registrada");
        }

        try {
            MimeMessage correo = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    correo,
                    false,
                    StandardCharsets.UTF_8.name());
            helper.setFrom(senderAddress);
            helper.setTo(evento.correo());
            helper.setSubject(
                    "Restricción para operar rutas - "
                            + evento.nombreConductor());
            helper.setText(construirMensaje(evento), false);
            mailSender.send(correo);

            logger.info(
                    "Notificación de restricción enviada para el conductor {}",
                    evento.conductorId());
        } catch (jakarta.mail.MessagingException exception) {
            throw new IllegalStateException(
                    "No se pudo preparar el correo de restricción para el conductor "
                            + evento.conductorId(),
                    exception);
        }
    }

    private RestriccionConductorEvent deserializar(String mensaje) {
        try {
            return objectMapper.readValue(
                    mensaje,
                    RestriccionConductorEvent.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "El mensaje RabbitMQ de restricción no tiene un formato válido",
                    exception);
        }
    }

    private String construirMensaje(RestriccionConductorEvent evento) {
        List<String> placas = evento.placas();
        List<String> rutas = evento.rutas();
        String listadoPlacas = placas.isEmpty()
                ? "No hay vehículos asociados."
                : String.join(", ", placas);
        String listadoRutas = rutas.isEmpty()
                ? "No se encontraron rutas asignadas."
                : String.join("\n", rutas.stream()
                        .map(ruta -> "- " + ruta)
                        .toList());

        return """
                Hola %s:

                Te informamos que tu licencia de conducción venció el %s.
                Por este motivo, tu estado fue cambiado a Restringido para Operar (RO).
                No puedes operar las rutas asociadas a los siguientes vehículos y rutas:

                Vehículos: %s
                Rutas:
                %s

                Por favor, actualiza la documentación correspondiente y comunícate con el área administrativa.
                """.formatted(
                evento.nombreConductor(),
                evento.fechaVencimientoLicencia(),
                listadoPlacas,
                listadoRutas);
    }
}
