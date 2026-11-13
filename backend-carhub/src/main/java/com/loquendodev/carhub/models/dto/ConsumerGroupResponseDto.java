package com.loquendodev.carhub.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO para la inspección y monitoreo de Consumer Groups en Apache Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalle y estado de un grupo de consumidores Kafka")
public class ConsumerGroupResponseDto implements Serializable {

    @Schema(description = "ID del grupo de consumidores", example = "carhub-analytics-ventas-group")
    private String groupId;

    @Schema(description = "Estado del grupo (STABLE, PREPARING_REBALANCE, DEAD, EMPTY)", example = "STABLE")
    private String state;

    @Schema(description = "Indica si es un consumidor simple sin asignador de particiones", example = "false")
    private boolean simpleConsumerGroup;

    @Schema(description = "Cantidad de miembros activos en el grupo", example = "2")
    private int membersCount;

    @Schema(description = "Lista de identificadores de miembros activos")
    private List<String> members;
}
