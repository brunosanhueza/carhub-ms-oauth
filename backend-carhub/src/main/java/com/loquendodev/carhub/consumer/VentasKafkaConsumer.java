package com.loquendodev.carhub.consumer;

import com.loquendodev.carhub.models.dto.VentaStreamEventDto;
import com.loquendodev.carhub.services.AnaliticaVentasService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumidor Kafka para el flujo analítico de ventas en tiempo real.
 * Consumer Group: carhub-analytics-ventas-group
 * Tópico: carhub.ventas.streaming
 *
 * Mantiene la separación estricta entre la infraestructura de mensajería
 * y la lógica de dominio (delegando a AnaliticaVentasService).
 */
@Component
public class VentasKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(VentasKafkaConsumer.class);

    private final AnaliticaVentasService analiticaVentasService;

    @Autowired
    public VentasKafkaConsumer(AnaliticaVentasService analiticaVentasService) {
        this.analiticaVentasService = analiticaVentasService;
    }

    @KafkaListener(
            topics = "${carhub.kafka.topic.ventas:carhub.ventas.streaming}",
            groupId = "${carhub.kafka.group.ventas:carhub-analytics-ventas-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirEventoVenta(ConsumerRecord<String, VentaStreamEventDto> record) {
        log.info("📥 [KAFKA CONSUMER VENTAS] Evento recibido. Tópico: {}, Partición: {}, Offset: {}, Llave: {}",
                record.topic(), record.partition(), record.offset(), record.key());

        VentaStreamEventDto evento = record.value();

        if (evento == null) {
            log.warn("⚠️ [KAFKA CONSUMER VENTAS] Mensaje recibido con payload nulo o corrupto. Saltando procesamiento.");
            return;
        }

        // Validación defensiva del payload
        if (evento.getIdVenta() == null || evento.getMontoTotal() == null) {
            log.error("❌ [KAFKA CONSUMER VENTAS] Payload con campos obligatorios faltantes: {}", evento);
            throw new IllegalArgumentException("El evento de venta carece de idVenta o montoTotal.");
        }

        // Delegación limpia a la capa de dominio
        analiticaVentasService.procesarEventoVenta(evento);
    }
}
