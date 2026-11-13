package com.loquendodev.vehiculos.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento analítico tipado para telemetría y demanda de vehículos en Apache Kafka.
 * Tópico: carhub.actividad.vehiculos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Evento de telemetría y actividad de vehículos para Apache Kafka")
public class VehiculoActividadEventDto implements Serializable {

    @Schema(description = "ID del vehículo consultado o modificado", example = "5")
    private Long idVehiculo;

    @Schema(description = "Tipo de acción registrada (VISTO, COTIZADO, FAVORITO, ACTUALIZADO, CREADO)", example = "VISTO")
    private String tipoAccion;

    @Schema(description = "Marca del vehículo", example = "Toyota")
    private String marca;

    @Schema(description = "Modelo del vehículo", example = "Corolla")
    private String modelo;

    @Schema(description = "Precio del vehículo", example = "15000000")
    private Double precio;

    @Schema(description = "ID del usuario que originó la actividad (si aplica)", example = "3")
    private Long usuarioId;

    @Schema(description = "Timestamp del evento")
    private LocalDateTime timestamp;
}
