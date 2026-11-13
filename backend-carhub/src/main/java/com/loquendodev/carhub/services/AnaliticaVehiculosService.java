package com.loquendodev.carhub.services;

import com.loquendodev.carhub.models.dto.VehiculoActividadEventDto;

import java.util.List;
import java.util.Map;

/**
 * Servicio de dominio para telemetría y demanda de vehículos desacoplado de Kafka.
 */
public interface AnaliticaVehiculosService {

    void procesarActividadVehiculo(VehiculoActividadEventDto evento);

    List<VehiculoActividadEventDto> obtenerUltimaActividad();

    Map<String, Object> obtenerRankingDemanda();
}
