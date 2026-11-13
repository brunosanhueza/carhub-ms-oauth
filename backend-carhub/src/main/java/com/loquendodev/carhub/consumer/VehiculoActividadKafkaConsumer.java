package com.loquendodev.carhub.consumer;

import com.loquendodev.carhub.models.dto.VehiculoActividadEventDto;
import com.loquendodev.carhub.services.AnaliticaVehiculosService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor Kafka para telemetría, consultas y demanda de vehículos.
 * Consumer Group: carhub-telemetria-vehiculos-group
 * Tópico: carhub.actividad.vehiculos
 *
 * Mantiene la separación estricta entre la infraestructura de mensajería
 * y la lógica de dominio (delegando a AnaliticaVehiculosService).
 */
@Component
public class VehiculoActividadKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(VehiculoActividadKafkaConsumer.class);

    private final AnaliticaVehiculosService analiticaVehiculosService;

    @Autowired
    public VehiculoActividadKafkaConsumer(AnaliticaVehiculosService analiticaVehiculosService) {
        this.analiticaVehiculosService = analiticaVehiculosService;
    }

    @KafkaListener(
            topics = "${carhub.kafka.topic.actividad:carhub.actividad.vehiculos}",
            groupId = "${carhub.kafka.group.actividad:carhub-telemetria-vehiculos-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirActividadVehiculo(ConsumerRecord<String, VehiculoActividadEventDto> record) {
        log.info("📥 [KAFKA CONSUMER TELEMETRÍA] Evento recibido. Tópico: {}, Partición: {}, Offset: {}, Llave: {}",
                record.topic(), record.partition(), record.offset(), record.key());

        VehiculoActividadEventDto evento = record.value();

        if (evento == null) {
            log.warn("⚠️ [KAFKA CONSUMER TELEMETRÍA] Mensaje nulo o descartado por deserializador.");
            return;
        }

        if (evento.getIdVehiculo() == null) {
            log.error("❌ [KAFKA CONSUMER TELEMETRÍA] Evento de actividad sin idVehiculo: {}", evento);
            throw new IllegalArgumentException("El evento de actividad de vehículo carece de idVehiculo.");
        }

        // Delegación a la capa de dominio
        analiticaVehiculosService.procesarActividadVehiculo(evento);
    }
}
