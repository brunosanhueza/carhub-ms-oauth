package com.loquendodev.compra.publisher;

import com.loquendodev.compra.model.dtos.FacturaTicketMensajeDto;
import com.loquendodev.compra.model.entity.facturaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Productor desacoplado para publicar eventos de compra hacia RabbitMQ.
 */
@Component
public class CompraEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CompraEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${carhub.rabbitmq.exchange:carhub.exchange}")
    private String exchangeName;

    @Value("${carhub.rabbitmq.routingkey.compra:backend-compra.ticket.rk}")
    private String routingKey;

    @Autowired
    public CompraEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica de forma asíncrona el evento de compra para generar el ticket/factura digital.
     */
    public void publicarTicketCompra(facturaModel factura) {
        try {
            FacturaTicketMensajeDto mensaje = new FacturaTicketMensajeDto(
                    factura.getNro_factura(),
                    factura.getIdUser(),
                    factura.getNameUser(),
                    factura.getEmailUser(),
                    factura.getPhoneUser(),
                    factura.getMarcaVehiculo(),
                    factura.getModeloVehiculo(),
                    factura.getAnioVehiculo(),
                    factura.getNombreAutomotriz(),
                    factura.getMetodo_pago_factura(),
                    factura.getSubtotal_factura(),
                    factura.getFecha_emision_factura()
            );

            log.info("Publicando evento de compra asíncrona en Exchange '{}' con RoutingKey '{}': Factura Nro #{}",
                    exchangeName, routingKey, factura.getNro_factura());

            rabbitTemplate.convertAndSend(exchangeName, routingKey, mensaje);
            log.info("Mensaje de compra Nro #{} enviado exitosamente a RabbitMQ", factura.getNro_factura());

        } catch (Exception e) {
            log.error("Error al publicar mensaje de compra en RabbitMQ: {}", e.getMessage(), e);
        }
    }
}
