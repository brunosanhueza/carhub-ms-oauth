package com.loquendodev.compra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración centralizada de RabbitMQ para el microservicio backend-compra.
 * Declara colas, exchanges, bindings y la cola de mensajes muertos (DLQ).
 */
@Configuration
public class RabbitMQConfig {

    @Value("${carhub.rabbitmq.exchange:carhub.exchange}")
    private String exchangeName;

    @Value("${carhub.rabbitmq.dlx:carhub.dlx}")
    private String dlxExchangeName;

    @Value("${carhub.rabbitmq.queue.compra:backend-compra.ticket.queue}")
    private String compraQueueName;

    @Value("${carhub.rabbitmq.routingkey.compra:backend-compra.ticket.rk}")
    private String compraRoutingKey;

    @Value("${carhub.rabbitmq.queue.compra.dlq:backend-compra.ticket.dlq}")
    private String compraDlqName;

    @Value("${carhub.rabbitmq.routingkey.compra.dlq:backend-compra.ticket.dlq.rk}")
    private String compraDlqRoutingKey;

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
     * Cola principal de compra configurada para redirigir fallos al DLX.
     */
    @Bean
    public Queue compraQueue() {
        return QueueBuilder.durable(compraQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", compraDlqRoutingKey)
                .build();
    }

    /**
     * Cola de mensajes muertos (Dead Letter Queue) para compras fallidas.
     */
    @Bean
    public Queue compraDlq() {
        return QueueBuilder.durable(compraDlqName).build();
    }

    // ==========================================
    // BINDINGS (Enlaces entre Colas y Exchanges)
    // ==========================================

    @Bean
    public Binding compraBinding(Queue compraQueue, DirectExchange carhubExchange) {
        return BindingBuilder.bind(compraQueue).to(carhubExchange).with(compraRoutingKey);
    }

    @Bean
    public Binding compraDlqBinding(Queue compraDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(compraDlq).to(dlxExchange).with(compraDlqRoutingKey);
    }

    // ==========================================
    // SERIALIZACIÓN JSON Y RABBIT TEMPLATE
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
