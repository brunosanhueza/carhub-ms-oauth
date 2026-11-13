package com.loquendodev.compra.model.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento analítico tipado para streaming en Apache Kafka.
 * Tópico: carhub.ventas.streaming
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Evento de streaming de analítica de ventas para Apache Kafka")
public class VentaStreamEventDto implements Serializable {

    @Schema(description = "Identificador de la transacción/factura", example = "105")
    private Long idVenta;

    @Schema(description = "ID del usuario comprador", example = "1")
    private Long idUsuario;

    @Schema(description = "Marca del vehículo adquirido", example = "Toyota")
    private String marca;

    @Schema(description = "Modelo del vehículo adquirido", example = "Corolla")
    private String modelo;

    @Schema(description = "Monto total de la transacción", example = "18500000.00")
    private BigDecimal montoTotal;

    @Schema(description = "Método de pago utilizado", example = "TRANSFERENCIA")
    private String metodoPago;

    @Schema(description = "Canal de venta (WEB, CONCESIONARIA, DIRECTO)", example = "WEB")
    private String canal;

    @Schema(description = "Nombre de la concesionaria asociada", example = "Automotriz Central")
    private String nombreAutomotriz;

    @Schema(description = "Fecha y hora del evento")
    private LocalDateTime fechaEmision;
}

