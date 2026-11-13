package com.loquendodev.carhub.services;

import com.loquendodev.carhub.models.dto.VentaStreamEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementación de la lógica de dominio para analítica en streaming de ventas.
 */
@Service
public class AnaliticaVentasServiceImpl implements AnaliticaVentasService {

    private static final Logger log = LoggerFactory.getLogger(AnaliticaVentasServiceImpl.class);

    // Registro en memoria de streaming para auditoría y analítica en tiempo real
    private final List<VentaStreamEventDto> streamBuffer = new CopyOnWriteArrayList<>();
    private final Map<Long, Integer> ventasPorUsuario = new ConcurrentHashMap<>();
    private BigDecimal totalIngresosStream = BigDecimal.ZERO;

    @Override
    public synchronized void procesarEventoVenta(VentaStreamEventDto evento) {
        log.info("📊 [DOMINIO ANALÍTICA VENTAS] Procesando evento analítico - Venta ID: {}, Vehículo: {} {}, Monto: ${}, Canal: {}",
                evento.getIdVenta(), evento.getMarca(), evento.getModelo(), evento.getMontoTotal(), evento.getCanal());

        streamBuffer.add(0, evento);
        if (streamBuffer.size() > 50) {
            streamBuffer.remove(streamBuffer.size() - 1);
        }

        if (evento.getIdUsuario() != null) {
            ventasPorUsuario.merge(evento.getIdUsuario(), 1, Integer::sum);
        }

        if (evento.getMontoTotal() != null) {
            totalIngresosStream = totalIngresosStream.add(evento.getMontoTotal());
        }

        log.info("📈 [DOMINIO ANALÍTICA VENTAS] Total acumulado en streaming: ${} ({} transacciones procesadas)",
                totalIngresosStream, streamBuffer.size());
    }

    @Override
    public List<VentaStreamEventDto> obtenerUltimasVentas() {
        return new ArrayList<>(streamBuffer);
    }

    @Override
    public Map<String, Object> obtenerMetricasVentas() {
        Map<String, Object> metricas = new HashMap<>();
        metricas.put("totalVentasRegistradas", streamBuffer.size());
        metricas.put("ingresosAcumulados", totalIngresosStream);
        metricas.put("clientesUnicos", ventasPorUsuario.size());
        return metricas;
    }
}
