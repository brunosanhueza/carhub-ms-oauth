package com.loquendodev.compra.consumer;

import com.loquendodev.compra.model.dtos.FacturaTicketMensajeDto;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor asíncrono para la generación y emisión de tickets de compra.
 * Implementa confirmación manual de mensajes (ACK explícito) y envío a Dead Letter Queue (DLQ).
 */
@Component
public class CompraTicketConsumer {

    private static final Logger log = LoggerFactory.getLogger(CompraTicketConsumer.class);

    @RabbitListener(queues = "${carhub.rabbitmq.queue.compra:backend-compra.ticket.queue}")
    public void procesarTicketCompra(FacturaTicketMensajeDto mensaje,
                                     Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("=================================================================");
        log.info("[CONSUMER] Recibido mensaje para emisión de Ticket de Compra. DeliveryTag: {}", deliveryTag);
        log.info("[CONSUMER] Factura ID: #{}", mensaje.getNroFactura());
        log.info("[CONSUMER] Cliente: {} ({})", mensaje.getNameUser(), mensaje.getEmailUser());
        log.info("[CONSUMER] Vehículo: {} {} ({})", mensaje.getMarcaVehiculo(), mensaje.getModeloVehiculo(), mensaje.getAnioVehiculo());
        log.info("[CONSUMER] Monto: ${} | Automotriz: {}", mensaje.getSubtotal(), mensaje.getNombreAutomotriz());

        try {
            // Validación de integridad del mensaje recibido
            if (mensaje.getNroFactura() == null || mensaje.getEmailUser() == null || mensaje.getEmailUser().isBlank()) {
                throw new IllegalArgumentException("Datos del ticket incompletos: ID de factura o correo faltante.");
            }

            // Simulación del procesamiento asíncrono (ej. generación de PDF / ticket / notificación)
            log.info("[CONSUMER] Generando documento digital y comprobante para factura #{}...", mensaje.getNroFactura());

            // Confirmación explícita (ACK): El mensaje fue procesado correctamente
            channel.basicAck(deliveryTag, false);
            log.info("[CONSUMER] [ACK CONFIRMADO] Ticket de compra #{} procesado exitosamente.", mensaje.getNroFactura());
            log.info("=================================================================");

        } catch (Exception ex) {
            log.error("[CONSUMER] [ERROR EN CONSUMO] Error al procesar ticket de compra #{}: {}", 
                    mensaje != null ? mensaje.getNroFactura() : "DESCONOCIDO", ex.getMessage(), ex);

            // Manejo explícito de error:
            // requeue = false instruye a RabbitMQ a NO reencolarlo en la cola original,
            // sino redirigirlo inmediatamente al Dead Letter Exchange (DLQ).
            channel.basicNack(deliveryTag, false, false);
            log.warn("[CONSUMER] [NACK ENVIADO A DLQ] Mensaje con DeliveryTag {} derivado a cola DLQ.", deliveryTag);
            log.info("=================================================================");
        }
    }
}
