package com.loquendodev.vehiculos.publisher;

import com.loquendodev.vehiculos.models.dto.VehiculoActividadEventDto;
import com.loquendodev.vehiculos.models.entity.VehiculoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Productor Kafka para emitir eventos de telemetría y demanda de vehículos al tópico carhub.actividad.vehiculos.
 * Usa ID de vehículo como clave de partición para asegurar orden y balanceo por entidad.
 */
@Component
public class VehiculoActividadKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(VehiculoActividadKafkaProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${carhub.kafka.topic.actividad:carhub.actividad.vehiculos}")
    private String topicActividad;

    @Autowired
    public VehiculoActividadKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Envía evento de actividad/telemetría vehicular tipado a Kafka.
     */
    public void emitirActividadVehiculo(VehiculoModel vehiculo, String tipoAccion, Long usuarioId) {
        try {
            if (vehiculo == null || vehiculo.getIdVehiculo() == null) {
                return;
            }

            Long idVehiculo = vehiculo.getIdVehiculo().longValue();
            Double precio = vehiculo.getPrecioVehiculo() != null
                    ? vehiculo.getPrecioVehiculo().doubleValue()
                    : null;

            VehiculoActividadEventDto evento = VehiculoActividadEventDto.builder()
                    .idVehiculo(idVehiculo)
                    .tipoAccion(tipoAccion != null ? tipoAccion : "VISTO")
                    .marca(vehiculo.getMarcaVehiculo())
                    .modelo(vehiculo.getModeloVehiculo())
                    .precio(precio)
                    .usuarioId(usuarioId)
                    .timestamp(LocalDateTime.now())
                    .build();

            String partitionKey = String.valueOf(idVehiculo);

            log.info("🚀 [KAFKA PRODUCER TELEMETRÍA] Publicando evento '{}' en tópico '{}' [Llave: {}]: Vehículo ID #{}",
                    evento.getTipoAccion(), topicActividad, partitionKey, idVehiculo);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicActividad, partitionKey, evento);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ [KAFKA PRODUCER TELEMETRÍA] Evento de Vehículo ID #{} publicado en Partición: {}, Offset: {}",
                            idVehiculo,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("❌ [KAFKA PRODUCER TELEMETRÍA] Error al publicar evento de Vehículo ID #{} en Kafka: {}",
                            idVehiculo, ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("❌ [KAFKA PRODUCER TELEMETRÍA] Excepción al enviar evento a Kafka: {}", e.getMessage(), e);
        }
    }
}
