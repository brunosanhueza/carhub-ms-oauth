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
    public ApplicationRunner initRabbitMQ(RabbitAdmin rabbitAdmin, Queue carhubQueue){
        return args -> {
            rabbitAdmin.declareQueue(carhubQueue);
            System.out.println("===================================");
            System.out.println("EL RABIS EME QU ASIDO CONECTADO SUSECSFULI");
            System.out.println("Cola creada: "+ CARHUB_QUEUE );
            System.out.println("===================================");
        };
    };
    
}
