package com.loquendodev.carhub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración centralizada de RabbitMQ para el microservicio backend-carhub.
 * Declara de forma explícita beans de Queue, Exchange, Binding y DLQ
 * para los casos de uso del sistema.
 */
@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    // ==========================================
    // PROPIEDADES CENTRALIZADAS (application.properties)
    // ==========================================

    @Value("${carhub.rabbitmq.exchange:carhub.exchange}")
    private String exchangeName;

    @Value("${carhub.rabbitmq.topic.exchange:carhub.topic.exchange}")
    private String topicExchangeName;

    @Value("${carhub.rabbitmq.dlx:carhub.dlx}")
    private String dlxExchangeName;

    @Value("${carhub.rabbitmq.queue.usuario:backend-carhub.usuario.queue}")
    private String usuarioQueueName;

    @Value("${carhub.rabbitmq.routingkey.usuario:backend-carhub.usuario.rk}")
    private String usuarioRoutingKey;

    @Value("${carhub.rabbitmq.queue.usuario.dlq:backend-carhub.usuario.dlq}")
    private String usuarioDlqName;

    @Value("${carhub.rabbitmq.routingkey.usuario.dlq:backend-carhub.usuario.dlq.rk}")
    private String usuarioDlqRoutingKey;

    public static final String CARHUB_QUEUE = "carhub.queue";

    // ==========================================
    // EXCHANGES (Direct, Topic y Dead Letter Exchange)
    // ==========================================

    @Bean
    public DirectExchange carhubExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public TopicExchange carhubTopicExchange() {
        return new TopicExchange(topicExchangeName, true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(dlxExchangeName, true, false);
    }

    // ==========================================
    // COLAS (Principal, DLQ y Cola Base)
    // ==========================================

    /**
     * Cola principal de notificaciones de usuario configurada con redirección a DLX.
     */
    @Bean
    public Queue usuarioQueue() {
        return QueueBuilder.durable(usuarioQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", usuarioDlqRoutingKey)
                .build();
    }

    /**
     * Cola Dead Letter Queue (DLQ) para mensajes no entregados o erróneos de usuario.
     */
    @Bean
    public Queue usuarioDlq() {
        return QueueBuilder.durable(usuarioDlqName).build();
    }

    /**
     * Cola base durable del microservicio Carhub.
     */
    @Bean
    public Queue carhubQueue() {
        return new Queue(CARHUB_QUEUE, true);
    }

    // ==========================================
    // BINDINGS (Enlaces entre Colas y Exchanges)
    // ==========================================

    @Bean
    public Binding usuarioBinding(Queue usuarioQueue, DirectExchange carhubExchange) {
        return BindingBuilder.bind(usuarioQueue).to(carhubExchange).with(usuarioRoutingKey);
    }

    @Bean
    public Binding usuarioDlqBinding(Queue usuarioDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(usuarioDlq).to(dlxExchange).with(usuarioDlqRoutingKey);
    }

    @Bean
    public Binding usuarioTopicBinding(Queue usuarioQueue, TopicExchange carhubTopicExchange) {
        return BindingBuilder.bind(usuarioQueue).to(carhubTopicExchange).with("usuario.evento.*");
    }

    @Bean
    public Binding carhubQueueBinding(Queue carhubQueue, DirectExchange carhubExchange) {
        return BindingBuilder.bind(carhubQueue).to(carhubExchange).with("carhub.base.rk");
    }

    // ==========================================
    // CONEXIÓN, SERIALIZACIÓN Y RABBITADMIN
    // ==========================================

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

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

    /**
     * Inicializador de topología RabbitMQ al iniciar Carhub.
     */
    @Bean
    public ApplicationRunner initRabbitMQ(RabbitAdmin rabbitAdmin,
                                          DirectExchange carhubExchange,
                                          TopicExchange carhubTopicExchange,
                                          DirectExchange dlxExchange,
                                          Queue usuarioQueue,
                                          Queue usuarioDlq,
                                          Queue carhubQueue,
                                          Binding usuarioBinding,
                                          Binding usuarioTopicBinding,
                                          Binding usuarioDlqBinding,
                                          Binding carhubQueueBinding) {
        return args -> {
            try {
                // Declarar exchanges (Direct y Topic)
                rabbitAdmin.declareExchange(carhubExchange);
                rabbitAdmin.declareExchange(carhubTopicExchange);
                rabbitAdmin.declareExchange(dlxExchange);

                // Declarar colas (Principal, DLQ y Base)
                rabbitAdmin.declareQueue(usuarioQueue);
                rabbitAdmin.declareQueue(usuarioDlq);
                rabbitAdmin.declareQueue(carhubQueue);

                // Declarar bindings
                rabbitAdmin.declareBinding(usuarioBinding);
                rabbitAdmin.declareBinding(usuarioTopicBinding);
                rabbitAdmin.declareBinding(usuarioDlqBinding);
                rabbitAdmin.declareBinding(carhubQueueBinding);

                log.info("=================================================================");
                log.info(" RabbitMQ Topología Inicializada Exitosamente en Carhub");
                log.info(" Exchange Direct: {} | Topic: {} | DLX: {}", exchangeName, topicExchangeName, dlxExchangeName);
                log.info(" Cola Usuario: {} -> DLQ: {}", usuarioQueueName, usuarioDlqName);
                log.info(" Cola Base Carhub: {}", CARHUB_QUEUE);
                log.info("=================================================================");
            } catch (Exception e) {
                log.warn("=================================================================");
                log.warn("ADVERTENCIA: No se pudo auto-declarar topología en RabbitMQ: {}", e.getMessage());
                log.warn("Por favor asegúrate de que el broker RabbitMQ esté en ejecución en localhost:5672");
                log.warn("=================================================================");
            }
        };
    }
}
