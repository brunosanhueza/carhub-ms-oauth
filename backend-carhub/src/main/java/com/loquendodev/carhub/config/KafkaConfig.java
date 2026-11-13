package com.loquendodev.carhub.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración integral de Apache Kafka para CarHub:
 * - Declaración programática de tópicos (particiones, réplicas, retención, cleanup policy).
 * - AdminClient oficial para el microservicio de administración.
 * - Productor y Consumidor tipados con serialización JSON.
 * - Resiliencia y manejo avanzado de errores con reintentos y logging contextual.
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    @Value("${spring.kafka.bootstrap-servers:localhost:5030}")
    private String bootstrapServers;

    @Value("${carhub.kafka.topic.ventas:carhub.ventas.streaming}")
    private String topicVentas;

    @Value("${carhub.kafka.topic.actividad:carhub.actividad.vehiculos}")
    private String topicActividad;

    // ==========================================
    // 1. DECLARACIÓN DE TÓPICOS INICIALES (RÉPLICAS Y PARTICIONES)
    // ==========================================

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 10000);
        return AdminClient.create(configs);
    }

    /**
     * Tópico 1: Analítica de Ventas y Facturación
     * 3 particiones, factor de replicación 2, retención 7 días (604800000 ms), cleanup policy delete.
     */
    @Bean
    public NewTopic topicVentasStreaming() {
        return TopicBuilder.name(topicVentas)
                .partitions(3)
                .replicas(2)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }

    /**
     * Tópico 2: Telemetría y Demanda de Vehículos
     * 3 particiones, factor de replicación 2, retención 3 días (259200000 ms), cleanup policy delete.
     */
    @Bean
    public NewTopic topicActividadVehiculos() {
        return TopicBuilder.name(topicActividad)
                .partitions(3)
                .replicas(2)
                .config(TopicConfig.RETENTION_MS_CONFIG, "259200000")
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }

    // ==========================================
    // 2. CONFIGURACIÓN DEL PRODUCTOR KAFKA
    // ==========================================

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ==========================================
    // 3. CONFIGURACIÓN DEL CONSUMIDOR KAFKA Y RESILIENCIA
    // ==========================================

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Deserializador seguro que envuelve a JsonDeserializer para aislar poison pills / payloads corruptos
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Manejador de errores con reintentos exponenciales/fijos y logging contextual.
     * Realiza hasta 3 intentos antes de rechazar el mensaje y registrar contexto completo de fallo.
     */
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            log.error("❌ [KAFKA ERROR HANDLER] Fallo irrecuperable en procesamiento de evento. Tópico: {}, Partición: {}, Offset: {}, Llave: {}, Causa: {}",
                    record.topic(), record.partition(), record.offset(), record.key(), exception.getMessage());
        }, new FixedBackOff(1000L, 3L)); // 3 reintentos separados por 1 segundo

        return errorHandler;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3); // 3 hilos concurrentes para procesar las 3 particiones balanceadas
        factory.setCommonErrorHandler(kafkaErrorHandler());
        return factory;
    }
}
