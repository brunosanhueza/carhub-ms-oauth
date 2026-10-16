package com.zacky.reclamos.publisher;

import com.zacky.reclamos.model.dtos.ReclamoTicketMensajeDto;
import com.zacky.reclamos.model.entity.ReclamoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Productor desacoplado para publicar eventos de reclamos/soporte hacia RabbitMQ.
 */
@Component
public class ReclamoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ReclamoEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${carhub.rabbitmq.exchange:carhub.exchange}")
    private String exchangeName;

    @Value("${carhub.rabbitmq.routingkey.reclamos:backend-reclamos.ticket.rk}")
    private String routingKey;

    @Autowired
    public ReclamoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica de forma asíncrona la creación de un nuevo reclamo para generar el ticket de soporte.
     */
    public void publicarTicketReclamo(ReclamoModel reclamo) {
        try {
            ReclamoTicketMensajeDto mensaje = new ReclamoTicketMensajeDto(
                    reclamo.getIdReclamo(),
                    reclamo.getIdUsuario(),
                    reclamo.getNameUser(),
                    reclamo.getEmailUser(),
                    reclamo.getPhoneUser(),
                    reclamo.getTipoReclamo(),
                    reclamo.getDescripcionReclamo(),
                    reclamo.getEstadoReclamo(),
                    reclamo.getFechaReclamo()
            );

            log.info("Publicando evento de nuevo reclamo en Exchange '{}' con RoutingKey '{}': Reclamo ID #{}",
                    exchangeName, routingKey, reclamo.getIdReclamo());

            rabbitTemplate.convertAndSend(exchangeName, routingKey, mensaje);
            log.info("Mensaje de reclamo ID #{} enviado exitosamente a RabbitMQ", reclamo.getIdReclamo());

        } catch (Exception e) {
            log.error("Error al publicar mensaje de reclamo en RabbitMQ: {}", e.getMessage(), e);
        }
    }
}
