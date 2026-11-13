package com.loquendodev.carhub.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para la modificación dinámica de configuraciones de un tópico existente en Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de solicitud para modificar configuraciones o particiones de un tópico Kafka")
public class TopicUpdateRequestDto implements Serializable {

    @Schema(description = "Nuevo número de particiones (solo puede incrementarse)", example = "4")
    private Integer partitions;

    @Schema(description = "Tiempo de retención en milisegundos (retention.ms)", example = "86400000")
    private Long retentionMs;

    @Schema(description = "Política de limpieza de logs (delete o compact)", example = "compact")
    private String cleanupPolicy;
}
