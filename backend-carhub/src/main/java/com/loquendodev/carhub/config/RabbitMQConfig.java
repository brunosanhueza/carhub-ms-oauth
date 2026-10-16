package com.loquendodev.carhub.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;


@Configuration 
public class RabbitMQConfig {
    public static final String CARHUB_QUEUE = "carhub.queue";
    @Bean
    public Queue carhubQueue(){
        return new Queue(CARHUB_QUEUE, true); //se declara la cola como durable
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);

        return rabbitAdmin;
    }

    @Bean
    public org.springframework.amqp.support.converter.MessageConverter jsonMessageConverter() {
        return new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter();
    }

    @Bean
    public org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        org.springframework.amqp.rabbit.core.RabbitTemplate template = new org.springframework.amqp.rabbit.core.RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public ApplicationRunner initRabbitMQ(RabbitAdmin rabbitAdmin, Queue carhubQueue){
        return args -> {
            try {
                rabbitAdmin.declareQueue(carhubQueue);
                System.out.println("===================================");
                System.out.println("RabbitMQ Inicializado Correctamente en Carhub");
                System.out.println("Cola base declarada: " + CARHUB_QUEUE);
                System.out.println("===================================");
            } catch (Exception e) {
                System.err.println("==================================================================");
                System.err.println("ADVERTENCIA: No se pudo conectar a RabbitMQ en localhost:5672 (" + e.getMessage() + ")");
                System.err.println("Por favor asegúrate de levantar RabbitMQ antes de probar la mensajería.");
                System.err.println("==================================================================");
            }
        };
    }
}
