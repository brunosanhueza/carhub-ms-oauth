package com.loquendodev.carhub.services;

import com.loquendodev.carhub.models.dto.VentaStreamEventDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Servicio de dominio desacoplado de la infraestructura de mensajería para la analítica de ventas.
 */
public interface AnaliticaVentasService {

    void procesarEventoVenta(VentaStreamEventDto evento);

    List<VentaStreamEventDto> obtenerUltimasVentas();

    Map<String, Object> obtenerMetricasVentas();
}
