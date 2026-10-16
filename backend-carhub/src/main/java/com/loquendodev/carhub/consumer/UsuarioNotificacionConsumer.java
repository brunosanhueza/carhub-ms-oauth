package com.loquendodev.carhub.consumer;

import com.loquendodev.carhub.models.dto.UsuarioNotificacionMensajeDto;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor asíncrono para eventos y notificaciones de usuario.
 * Gestiona de forma explícita el ACK (confirmación de entrega) y el NACK
 * derivando mensajes erróneos a la Dead Letter Queue (DLQ).
 */
@Component
public class UsuarioNotificacionConsumer {

    private static final Logger log = LoggerFactory.getLogger(UsuarioNotificacionConsumer.class);

    @RabbitListener(queues = "${carhub.rabbitmq.queue.usuario:backend-carhub.usuario.queue}")
    public void procesarNotificacionUsuario(UsuarioNotificacionMensajeDto mensaje,
                                            Channel channel,
                                            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("=================================================================");
        log.info("[CONSUMER-USUARIO] Recibido mensaje de evento de usuario. DeliveryTag: {}", deliveryTag);
        log.info("[CONSUMER-USUARIO] Usuario: {} ({}) | ID: #{}", 
                mensaje != null ? mensaje.getNameUser() : "NULL",
                mensaje != null ? mensaje.getEmailUser() : "NULL",
                mensaje != null ? mensaje.getIdUser() : "NULL");
        log.info("[CONSUMER-USUARIO] Tipo de Evento: {} | Mensaje: {}", 
                mensaje != null ? mensaje.getTipoEvento() : "NULL",
                mensaje != null ? mensaje.getMensaje() : "NULL");

        try {
            // Validación de integridad del mensaje
            if (mensaje == null || mensaje.getEmailUser() == null || mensaje.getEmailUser().isBlank()) {
                throw new IllegalArgumentException("Mensaje de usuario inválido: faltan campos obligatorios o email vacío.");
            }

            // Simulación de procesamiento de la tarea asíncrona (ej. envío de email de bienvenida, auditoría)
            log.info("[CONSUMER-USUARIO] Procesando tarea asíncrona (notificación/bienvenida) para: {}", mensaje.getEmailUser());

            // Confirmación explícita (ACK): El mensaje fue procesado correctamente
            channel.basicAck(deliveryTag, false);
            log.info("[CONSUMER-USUARIO] [ACK CONFIRMADO] Notificación para {} procesada exitosamente.", mensaje.getEmailUser());
            log.info("=================================================================");

        } catch (Exception ex) {
            log.error("[CONSUMER-USUARIO] [ERROR EN CONSUMO] Error al procesar notificación de usuario: {}", 
                    ex.getMessage(), ex);

            // Manejo explícito de error:
            // requeue = false instruye a RabbitMQ a NO reencolarlo en la cola original,
            // sino redirigirlo inmediatamente al Dead Letter Exchange (DLQ).
            channel.basicNack(deliveryTag, false, false);
            log.warn("[CONSUMER-USUARIO] [NACK ENVIADO A DLQ] Mensaje derivado a cola DLQ. DeliveryTag: {}", deliveryTag);
            log.info("=================================================================");
        }
    }
}
