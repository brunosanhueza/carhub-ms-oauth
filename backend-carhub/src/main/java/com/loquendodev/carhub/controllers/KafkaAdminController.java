package com.loquendodev.carhub.controllers;

import com.loquendodev.carhub.models.dto.*;
import com.loquendodev.carhub.services.AnaliticaVehiculosService;
import com.loquendodev.carhub.services.AnaliticaVentasService;
import com.loquendodev.carhub.services.KafkaAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la administración y gestión del clúster Apache Kafka
 * y consulta de métricas de streaming en tiempo real.
 */
@Tag(name = "Kafka Administration & Analytics", description = "Endpoints para la gestión dinámica de tópicos, configuraciones, consumer groups y analítica en streaming")
@RestController
@RequestMapping({"/api/v1/admin/kafka", "/api/v1/kafka"})
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH
})
public class KafkaAdminController {

    private final KafkaAdminService kafkaAdminService;
    private final AnaliticaVentasService analiticaVentasService;
    private final AnaliticaVehiculosService analiticaVehiculosService;

    @Autowired
    public KafkaAdminController(KafkaAdminService kafkaAdminService,
                                AnaliticaVentasService analiticaVentasService,
                                AnaliticaVehiculosService analiticaVehiculosService) {
        this.kafkaAdminService = kafkaAdminService;
        this.analiticaVentasService = analiticaVentasService;
        this.analiticaVehiculosService = analiticaVehiculosService;
    }

    // ==========================================
    // 1. ADMINISTRACIÓN DE TÓPICOS
    // ==========================================

    @Operation(summary = "Crear nuevo tópico Kafka", description = "Declara dinámicamente un tópico en Kafka con particiones, factor de replicación y política de retención.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tópico creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error en el clúster Kafka")
    })
    @PostMapping("/topics")
    public ResponseEntity<Map<String, Object>> crearTopico(@Valid @RequestBody TopicRequestDto dto) {
        String mensaje = kafkaAdminService.crearTopico(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("mensaje", mensaje);
        response.put("topic", dto.getName());
        response.put("partitions", dto.getPartitions());
        response.put("replicationFactor", dto.getReplicationFactor());
        response.put("retentionMs", dto.getRetentionMs());
        response.put("cleanupPolicy", dto.getCleanupPolicy());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar tópicos Kafka", description = "Retorna todos los tópicos del clúster con sus particiones y factor de replicación.")
    @GetMapping("/topics")
    public ResponseEntity<Map<String, Object>> listarTopicos() {
        List<TopicResponseDto> topics = kafkaAdminService.listarTopicos();

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("total", topics.size());
        response.put("topics", topics);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener detalle y configuraciones de un tópico", description = "Obtiene particiones, réplicas, líderes y configuraciones avanzadas (retention.ms, cleanup.policy).")
    @GetMapping("/topics/{name}")
    public ResponseEntity<Map<String, Object>> obtenerDetalleTopico(
            @Parameter(description = "Nombre del tópico", required = true)
            @PathVariable("name") String name) {
        TopicResponseDto topic = kafkaAdminService.obtenerDetalleTopico(name);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("topic", topic);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Modificar tópico Kafka", description = "Permite aumentar particiones o actualizar políticas de retención y cleanup del tópico.")
    @PatchMapping("/topics/{name}")
    public ResponseEntity<Map<String, Object>> modificarTopico(
            @Parameter(description = "Nombre del tópico a modificar", required = true)
            @PathVariable("name") String name,
            @RequestBody TopicUpdateRequestDto dto) {
        String mensaje = kafkaAdminService.modificarTopico(name, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("mensaje", mensaje);
        response.put("topic", name);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar tópico Kafka", description = "Elimina un tópico existente en el clúster Kafka.")
    @DeleteMapping("/topics/{name}")
    public ResponseEntity<Map<String, Object>> eliminarTopico(
            @Parameter(description = "Nombre del tópico a eliminar", required = true)
            @PathVariable("name") String name) {
        String mensaje = kafkaAdminService.eliminarTopico(name);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("mensaje", mensaje);
        response.put("topic", name);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 2. MONITOREO DE CONSUMER GROUPS
    // ==========================================

    @Operation(summary = "Listar e inspeccionar Consumer Groups", description = "Retorna los grupos de consumidores registrados en Kafka, sus estados y miembros activos.")
    @GetMapping("/consumer-groups")
    public ResponseEntity<Map<String, Object>> listarConsumerGroups() {
        List<ConsumerGroupResponseDto> groups = kafkaAdminService.listarConsumerGroups();

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("total", groups.size());
        response.put("consumerGroups", groups);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 3. CONSULTA DE MÉTRICAS ANALÍTICAS DE STREAMING
    // ==========================================

    @Operation(summary = "Métricas en tiempo real de ventas", description = "Consulta las métricas agregadas obtenidas del flujo streaming de ventas.")
    @GetMapping("/analytics/ventas")
    public ResponseEntity<Map<String, Object>> obtenerAnaliticaVentas() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("metricas", analiticaVentasService.obtenerMetricasVentas());
        response.put("ultimosEventos", analiticaVentasService.obtenerUltimasVentas());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Métricas en tiempo real de telemetría vehicular", description = "Consulta el ranking de demanda y telemetría obtenido del flujo de actividad de vehículos.")
    @GetMapping("/analytics/vehiculos")
    public ResponseEntity<Map<String, Object>> obtenerAnaliticaVehiculos() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("rankingDemanda", analiticaVehiculosService.obtenerRankingDemanda());
        response.put("ultimosEventos", analiticaVehiculosService.obtenerUltimaActividad());
        return ResponseEntity.ok(response);
    }
}
