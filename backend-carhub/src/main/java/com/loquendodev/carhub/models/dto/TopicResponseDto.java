package com.loquendodev.carhub.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO con el detalle completo de un tópico Kafka inspeccionado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalle de configuración e infraestructura de un tópico Kafka")
public class TopicResponseDto implements Serializable {

    @Schema(description = "Nombre del tópico", example = "carhub.ventas.streaming")
    private String name;

    @Schema(description = "Cantidad de particiones asignadas", example = "3")
    private int partitions;

    @Schema(description = "Factor de replicación", example = "2")
    private int replicationFactor;

    @Schema(description = "Indica si es un tópico interno del sistema Kafka", example = "false")
    private boolean internal;

    @Schema(description = "Configuraciones avanzadas (retention.ms, cleanup.policy, etc.)")
    private Map<String, String> configurations;
}
