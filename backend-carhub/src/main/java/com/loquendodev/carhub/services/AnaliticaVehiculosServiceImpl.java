package com.loquendodev.carhub.services;

import com.loquendodev.carhub.models.dto.VehiculoActividadEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementación de la lógica de dominio para telemetría y demanda vehicular en streaming.
 */
@Service
public class AnaliticaVehiculosServiceImpl implements AnaliticaVehiculosService {

    private static final Logger log = LoggerFactory.getLogger(AnaliticaVehiculosServiceImpl.class);

    private final List<VehiculoActividadEventDto> actividadBuffer = new CopyOnWriteArrayList<>();
    private final Map<Long, Integer> demandaPorVehiculo = new ConcurrentHashMap<>();

    @Override
    public synchronized void procesarActividadVehiculo(VehiculoActividadEventDto evento) {
        log.info("🚗 [DOMINIO TELEMETRÍA VEHÍCULOS] Acción '{}' en Vehículo ID: {} ({} {}), Precio: ${}",
                evento.getTipoAccion(), evento.getIdVehiculo(), evento.getMarca(), evento.getModelo(), evento.getPrecio());

        actividadBuffer.add(0, evento);
        if (actividadBuffer.size() > 50) {
            actividadBuffer.remove(actividadBuffer.size() - 1);
        }

        if (evento.getIdVehiculo() != null) {
            demandaPorVehiculo.merge(evento.getIdVehiculo(), 1, Integer::sum);
        }

        log.info("📊 [DOMINIO TELEMETRÍA VEHÍCULOS] Vehículo ID: {} acumula {} interacciones registradas",
                evento.getIdVehiculo(), demandaPorVehiculo.get(evento.getIdVehiculo()));
    }

    @Override
    public List<VehiculoActividadEventDto> obtenerUltimaActividad() {
        return new ArrayList<>(actividadBuffer);
    }

    @Override
    public Map<String, Object> obtenerRankingDemanda() {
        Map<String, Object> ranking = new HashMap<>();
        ranking.put("totalInteracciones", actividadBuffer.size());
        ranking.put("vehiculosMonitoreados", demandaPorVehiculo.size());
        ranking.put("conteoPorVehiculoId", demandaPorVehiculo);
        return ranking;
    }
}
