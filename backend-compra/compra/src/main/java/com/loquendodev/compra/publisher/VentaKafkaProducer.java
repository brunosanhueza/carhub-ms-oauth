package com.loquendodev.compra.publisher;

import com.loquendodev.compra.model.dtos.VentaStreamEventDto;
import com.loquendodev.compra.model.entity.facturaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

/**
 * Productor Kafka para emitir eventos de analítica de ventas al tópico carhub.ventas.streaming.
 * Emplea clave de particionamiento (ID de usuario o Factura) para balanceo y orden secuencial.
 */
@Component
public class VentaKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(VentaKafkaProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${carhub.kafka.topic.ventas:carhub.ventas.streaming}")
    private String topicVentas;

    @Autowired
    public VentaKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Envía de forma asíncrona el evento de venta tipado con DTO a Kafka.
     */
    public void emitirEventoVentaStreaming(facturaModel factura) {
        try {
            BigDecimal monto = factura.getSubtotal_factura() != null
                    ? BigDecimal.valueOf(factura.getSubtotal_factura())
                    : BigDecimal.ZERO;

            Long nroFactura = factura.getNro_factura() != null
                    ? factura.getNro_factura().longValue()
                    : null;

            Long idUsuario = factura.getIdUser() != null
                    ? factura.getIdUser().longValue()
                    : null;

            LocalDateTime fecha = LocalDateTime.now();
            if (factura.getFecha_emision_factura() != null) {
                fecha = factura.getFecha_emision_factura().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }

            VentaStreamEventDto evento = VentaStreamEventDto.builder()
                    .idVenta(nroFactura)
                    .idUsuario(idUsuario)
                    .marca(factura.getMarcaVehiculo())
                    .modelo(factura.getModeloVehiculo())
                    .montoTotal(monto)
                    .metodoPago(factura.getMetodo_pago_factura())
                    .canal("WEB")
                    .nombreAutomotriz(factura.getNombreAutomotriz())
                    .fechaEmision(fecha)
                    .build();

            // Llave de partición basada en idUsuario o Factura para ordenamiento y balanceo
            String partitionKey = idUsuario != null
                    ? String.valueOf(idUsuario)
                    : (nroFactura != null ? String.valueOf(nroFactura) : "DEFAULT");

            log.info("🚀 [KAFKA PRODUCER VENTAS] Publicando evento de venta a tópico '{}' [Llave: {}]: Factura #{}",
                    topicVentas, partitionKey, nroFactura);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicVentas, partitionKey, evento);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ [KAFKA PRODUCER VENTAS] Evento de Factura #{} publicado exitosamente en Partición: {}, Offset: {}",
                            nroFactura,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("❌ [KAFKA PRODUCER VENTAS] Error al publicar evento de Factura #{} en Kafka: {}",
                            nroFactura, ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("❌ [KAFKA PRODUCER VENTAS] Excepción imprevista al preparar mensaje de venta: {}", e.getMessage(), e);
        }
    }
}
