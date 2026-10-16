package com.loquendodev.carhub.services;

import com.loquendodev.carhub.models.dto.BindingRequestDto;
import com.loquendodev.carhub.models.dto.ExchangeRequestDto;
import com.loquendodev.carhub.models.dto.QueueRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio encargado de encapsular toda la lógica de administración
 * de colas, exchanges y bindings mediante RabbitAdmin.
 */
@Service
public class RabbitAdminService {

    private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);

    private final RabbitAdmin rabbitAdmin;

    @Autowired
    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    /**
     * Declara una nueva cola en RabbitMQ con soporte para DLQ.
     */
    public String crearCola(QueueRequestDto dto) {
        log.info("Creando cola '{}', durable={}, exclusive={}, autoDelete={}", 
                dto.getName(), dto.isDurable(), dto.isExclusive(), dto.isAutoDelete());

        QueueBuilder builder = dto.isDurable() 
                ? QueueBuilder.durable(dto.getName()) 
                : QueueBuilder.nonDurable(dto.getName());

        if (dto.isExclusive()) {
            builder.exclusive();
        }
        if (dto.isAutoDelete()) {
            builder.autoDelete();
        }

        // Si se define Dead Letter Exchange
        if (dto.getDlxExchange() != null && !dto.getDlxExchange().trim().isEmpty()) {
            builder.withArgument("x-dead-letter-exchange", dto.getDlxExchange().trim());
            log.info("Configurado DLX '{}' para cola '{}'", dto.getDlxExchange(), dto.getName());
        }

        // Si se define Dead Letter Routing Key
        if (dto.getDlqRoutingKey() != null && !dto.getDlqRoutingKey().trim().isEmpty()) {
            builder.withArgument("x-dead-letter-routing-key", dto.getDlqRoutingKey().trim());
            log.info("Configurado DLQ routing key '{}' para cola '{}'", dto.getDlqRoutingKey(), dto.getName());
        }

        // Argumentos adicionales opcionales
        if (dto.getArguments() != null) {
            dto.getArguments().forEach(builder::withArgument);
        }

        Queue queue = builder.build();
        return rabbitAdmin.declareQueue(queue);
    }

    /**
     * Elimina una cola por su nombre.
     */
    public boolean eliminarCola(String nombreCola) {
        log.info("Eliminando cola '{}'", nombreCola);
        return rabbitAdmin.deleteQueue(nombreCola);
    }

    /**
     * Obtiene estadísticas e información de una cola existente.
     */
    public java.util.Properties obtenerInformacionCola(String nombreCola) {
        log.info("Consultando propiedades de la cola '{}'", nombreCola);
        return rabbitAdmin.getQueueProperties(nombreCola);
    }

    /**
     * Declara un nuevo Exchange en RabbitMQ.
     */
    public void crearExchange(ExchangeRequestDto dto) {
        log.info("Creando exchange '{}' de tipo '{}'", dto.getName(), dto.getType());

        Exchange exchange;
        String type = dto.getType().toLowerCase().trim();

        switch (type) {
            case "topic":
                exchange = new TopicExchange(dto.getName(), dto.isDurable(), dto.isAutoDelete(), dto.getArguments());
                break;
            case "fanout":
                exchange = new FanoutExchange(dto.getName(), dto.isDurable(), dto.isAutoDelete(), dto.getArguments());
                break;
            case "headers":
                exchange = new HeadersExchange(dto.getName(), dto.isDurable(), dto.isAutoDelete(), dto.getArguments());
                break;
            case "direct":
            default:
                exchange = new DirectExchange(dto.getName(), dto.isDurable(), dto.isAutoDelete(), dto.getArguments());
                break;
        }

        rabbitAdmin.declareExchange(exchange);
    }

    /**
     * Elimina un Exchange por su nombre.
     */
    public boolean eliminarExchange(String nombreExchange) {
        log.info("Eliminando exchange '{}'", nombreExchange);
        return rabbitAdmin.deleteExchange(nombreExchange);
    }

    /**
     * Crea un binding entre una cola y un exchange con una routing key.
     */
    public void crearBinding(BindingRequestDto dto) {
        log.info("Creando binding: cola '{}' <-> exchange '{}' con routing key '{}'", 
                dto.getQueueName(), dto.getExchangeName(), dto.getRoutingKey());

        Binding binding = new Binding(
                dto.getQueueName(),
                Binding.DestinationType.QUEUE,
                dto.getExchangeName(),
                dto.getRoutingKey(),
                dto.getArguments() != null ? dto.getArguments() : new HashMap<>()
        );

        rabbitAdmin.declareBinding(binding);
    }

    /**
     * Elimina un binding existente.
     */
    public void eliminarBinding(BindingRequestDto dto) {
        log.info("Eliminando binding: cola '{}' <-> exchange '{}' con routing key '{}'", 
                dto.getQueueName(), dto.getExchangeName(), dto.getRoutingKey());

        Binding binding = new Binding(
                dto.getQueueName(),
                Binding.DestinationType.QUEUE,
                dto.getExchangeName(),
                dto.getRoutingKey(),
                dto.getArguments() != null ? dto.getArguments() : new HashMap<>()
        );

        rabbitAdmin.removeBinding(binding);
    }
}
