package com.zacky.reclamos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración centralizada de RabbitMQ para el microservicio backend-reclamos.
 * Declara colas, exchanges, bindings y la cola DLQ para tickets de reclamos/soporte.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${carhub.rabbitmq.exchange:carhub.exchange}")
    private String exchangeName;

    @Value("${carhub.rabbitmq.dlx:carhub.dlx}")
    private String dlxExchangeName;

    @Value("${carhub.rabbitmq.queue.reclamos:backend-reclamos.ticket.queue}")
    private String reclamosQueueName;

    @Value("${carhub.rabbitmq.routingkey.reclamos:backend-reclamos.ticket.rk}")
    private String reclamosRoutingKey;

    @Value("${carhub.rabbitmq.queue.reclamos.dlq:backend-reclamos.ticket.dlq}")
    private String reclamosDlqName;

    @Value("${carhub.rabbitmq.routingkey.reclamos.dlq:backend-reclamos.ticket.dlq.rk}")
    private String reclamosDlqRoutingKey;

    // ==========================================
    // EXCHANGES (Principal y Dead Letter Exchange)
    // ==========================================

    @Bean
    public DirectExchange carhubExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(dlxExchangeName, true, false);
    }

    // ==========================================
    // COLAS (Principal con DLQ y Cola DLQ)
    // ==========================================

    /**
     * Cola principal de reclamos configurada con redirección a DLX en caso de rechazo.
     */
    @Bean
    public Queue reclamosQueue() {
        return QueueBuilder.durable(reclamosQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", reclamosDlqRoutingKey)
                .build();
    }

    /**
     * Cola de mensajes muertos (Dead Letter Queue) para reclamos no procesados.
     */
    @Bean
    public Queue reclamosDlq() {
        return QueueBuilder.durable(reclamosDlqName).build();
    }

    // ==========================================
    // BINDINGS (Enlaces de Mensajería)
    // ==========================================

    @Bean
    public Binding reclamosBinding(Queue reclamosQueue, DirectExchange carhubExchange) {
        return BindingBuilder.bind(reclamosQueue).to(carhubExchange).with(reclamosRoutingKey);
    }

    @Bean
    public Binding reclamosDlqBinding(Queue reclamosDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(reclamosDlq).to(dlxExchange).with(reclamosDlqRoutingKey);
    }

    // ==========================================
    // CONVERSIÓN JSON Y TEMPLATE
    // ==========================================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
