package com.loquendodev.carhub.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para la creación dinámica de tópicos en Apache Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de solicitud para declarar tópicos en el clúster Kafka")
public class TopicRequestDto implements Serializable {

    @NotBlank(message = "El nombre del tópico no puede estar vacío")
    @Schema(description = "Nombre del tópico", example = "carhub.ventas.streaming")
    private String name;

    @Positive(message = "Las particiones deben ser mayores a 0")
    @Builder.Default
    @Schema(description = "Número de particiones para balanceo", example = "3", defaultValue = "3")
    private int partitions = 3;

    @Positive(message = "El factor de replicación debe ser mayor a 0")
    @Builder.Default
    @Schema(description = "Factor de replicación entre brokers", example = "2", defaultValue = "2")
    private short replicationFactor = 2;

    @Builder.Default
    @Schema(description = "Tiempo de retención en milisegundos (ej. 7 días = 604800000)", example = "604800000", defaultValue = "604800000")
    private Long retentionMs = 604800000L;

    @Builder.Default
    @Schema(description = "Política de limpieza de logs (delete o compact)", example = "delete", defaultValue = "delete")
    private String cleanupPolicy = "delete";
}
