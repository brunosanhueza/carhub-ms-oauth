package com.zacky.reclamos.consumer;

import com.rabbitmq.client.Channel;
import com.zacky.reclamos.model.dtos.ReclamoTicketMensajeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor asíncrono para la gestión de tickets de reclamo / soporte al cliente.
 * Implementa confirmación manual (ACK explícito) y envío a Dead Letter Queue (DLQ).
 */
@Component
public class ReclamoTicketConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReclamoTicketConsumer.class);

    @RabbitListener(queues = "${carhub.rabbitmq.queue.reclamos:backend-reclamos.ticket.queue}")
    public void procesarTicketReclamo(ReclamoTicketMensajeDto mensaje,
                                      Channel channel,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("=================================================================");
        log.info("[CONSUMER-RECLAMOS] Recibido mensaje para emisión de Ticket de Reclamo. DeliveryTag: {}", deliveryTag);
        log.info("[CONSUMER-RECLAMOS] Reclamo ID: #{}", mensaje.getIdReclamo());
        log.info("[CONSUMER-RECLAMOS] Cliente: {} ({})", mensaje.getNameUser(), mensaje.getEmailUser());
        log.info("[CONSUMER-RECLAMOS] Tipo de Reclamo: {} | Estado: {}", mensaje.getTipoReclamo(), mensaje.getEstadoReclamo());
        log.info("[CONSUMER-RECLAMOS] Descripción: {}", mensaje.getDescripcionReclamo());

        try {
            // Validación de contenido
            if (mensaje.getIdReclamo() == null || mensaje.getDescripcionReclamo() == null || mensaje.getDescripcionReclamo().isBlank()) {
                throw new IllegalArgumentException("Datos del reclamo incompletos o descripción vacía.");
            }

            // Procesamiento del ticket de atención
            log.info("[CONSUMER-RECLAMOS] Generando ticket de soporte técnico para el reclamo #{}...", mensaje.getIdReclamo());

            // Confirmación explícita (ACK)
            channel.basicAck(deliveryTag, false);
            log.info("[CONSUMER-RECLAMOS] [ACK CONFIRMADO] Reclamo #{} procesado exitosamente.", mensaje.getIdReclamo());
            log.info("=================================================================");

        } catch (Exception ex) {
            log.error("[CONSUMER-RECLAMOS] [ERROR EN CONSUMO] Error al procesar reclamo #{}: {}", 
                    mensaje != null ? mensaje.getIdReclamo() : "DESCONOCIDO", ex.getMessage(), ex);

            // Redirección explícita a Dead Letter Queue (DLQ)
            channel.basicNack(deliveryTag, false, false);
            log.warn("[CONSUMER-RECLAMOS] [NACK ENVIADO A DLQ] Reclamo derivado a DLQ. Tag: {}", deliveryTag);
            log.info("=================================================================");
        }
    }
}
