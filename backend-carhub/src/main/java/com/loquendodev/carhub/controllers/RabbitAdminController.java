package com.loquendodev.carhub.controllers;

import com.loquendodev.carhub.models.dto.BindingRequestDto;
import com.loquendodev.carhub.models.dto.ExchangeRequestDto;
import com.loquendodev.carhub.models.dto.QueueRequestDto;
import com.loquendodev.carhub.services.RabbitAdminService;
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
import java.util.Map;
import java.util.Properties;

/**
 * Controlador REST para la administración dinámica de colas, exchanges y bindings de RabbitMQ.
 * Expone rutas compatibles según la rúbrica (/queues, /api/v1/queues, /api/v1/admin/queues).
 */
@Tag(name = "RabbitMQ Administration", description = "Endpoints REST para la administración de topología RabbitMQ (Colas, Exchanges, Bindings y DLQ)")
@RestController
@RequestMapping({"/api/v1/admin", "/api/v1", ""})
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH
})
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    @Autowired
    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    // ==========================================
    // GESTIÓN DE COLAS (QUEUES)
    // ==========================================

    @Operation(summary = "Crear nueva cola", description = "Declara una cola en RabbitMQ con soporte para DLQ (Dead Letter Exchange) y configuración de durabilidad.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cola creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos o nombre de cola vacío")
    })
    @PostMapping("/queues")
    public ResponseEntity<Map<String, Object>> crearCola(@Valid @RequestBody QueueRequestDto dto) {
        String resultado = rabbitAdminService.crearCola(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("mensaje", "Cola creada exitosamente");
        response.put("cola", dto.getName());
        response.put("durable", dto.isDurable());
        response.put("dlxExchange", dto.getDlxExchange());
        response.put("dlqRoutingKey", dto.getDlqRoutingKey());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Consultar propiedades de una cola", description = "Retorna el estado de una cola: cantidad de mensajes, consumidores activos, etc.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propiedades de la cola obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "La cola no existe o no tiene propiedades registradas")
    })
    @GetMapping("/queues/{name}")
    public ResponseEntity<Map<String, Object>> consultarCola(
            @Parameter(description = "Nombre de la cola a consultar", required = true)
            @PathVariable("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("error", "El nombre de la cola no puede estar vacío");
            return ResponseEntity.badRequest().body(error);
        }

        Properties props = rabbitAdminService.obtenerInformacionCola(name.trim());
        Map<String, Object> response = new HashMap<>();

        if (props == null) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("mensaje", "La cola '" + name + "' no existe o no se encuentra en el broker");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("status", HttpStatus.OK.value());
        response.put("cola", name);
        response.put("mensajes", props.get("QUEUE_MESSAGE_COUNT"));
        response.put("consumidores", props.get("QUEUE_CONSUMER_COUNT"));
        response.put("propiedades", props);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar cola", description = "Elimina una cola existente en RabbitMQ.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cola eliminada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Nombre de la cola vacío o inválido")
    })
    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Map<String, Object>> eliminarCola(
            @Parameter(description = "Nombre de la cola a eliminar", required = true)
            @PathVariable("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("error", "El nombre de la cola no puede estar vacío");
            return ResponseEntity.badRequest().body(error);
        }

        boolean eliminada = rabbitAdminService.eliminarCola(name.trim());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("mensaje", eliminada ? "Cola eliminada exitosamente" : "La cola no existía o ya fue eliminada");
        response.put("cola", name);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // GESTIÓN DE EXCHANGES
    // ==========================================

    @Operation(summary = "Crear nuevo exchange", description = "Declara un exchange en RabbitMQ de tipo direct, topic, fanout o headers.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exchange creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Tipo de exchange o parámetros no válidos")
    })
    @PostMapping("/exchanges")
    public ResponseEntity<Map<String, Object>> crearExchange(@Valid @RequestBody ExchangeRequestDto dto) {
        rabbitAdminService.crearExchange(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("mensaje", "Exchange creado exitosamente");
        response.put("exchange", dto.getName());
        response.put("tipo", dto.getType());
        response.put("durable", dto.isDurable());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar exchange", description = "Elimina un exchange existente por su nombre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exchange eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Nombre de exchange vacío o inválido")
    })
    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Map<String, Object>> eliminarExchange(
            @Parameter(description = "Nombre del exchange a eliminar", required = true)
            @PathVariable("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("error", "El nombre del exchange no puede estar vacío");
            return ResponseEntity.badRequest().body(error);
        }

        boolean eliminado = rabbitAdminService.eliminarExchange(name.trim());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("mensaje", eliminado ? "Exchange eliminado exitosamente" : "El exchange no existía o ya fue eliminado");
        response.put("exchange", name);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // GESTIÓN DE BINDINGS
    // ==========================================

    @Operation(summary = "Crear binding", description = "Enlaza una cola a un exchange con una routing key específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Binding creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros del binding inválidos")
    })
    @PostMapping("/bindings")
    public ResponseEntity<Map<String, Object>> crearBinding(@Valid @RequestBody BindingRequestDto dto) {
        rabbitAdminService.crearBinding(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("mensaje", "Binding creado exitosamente");
        response.put("cola", dto.getQueueName());
        response.put("exchange", dto.getExchangeName());
        response.put("routingKey", dto.getRoutingKey());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar binding", description = "Elimina un binding existente entre una cola y un exchange.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Binding eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros del binding inválidos")
    })
    @DeleteMapping("/bindings")
    public ResponseEntity<Map<String, Object>> eliminarBinding(@Valid @RequestBody BindingRequestDto dto) {
        rabbitAdminService.eliminarBinding(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("mensaje", "Binding eliminado exitosamente");
        response.put("cola", dto.getQueueName());
        response.put("exchange", dto.getExchangeName());
        response.put("routingKey", dto.getRoutingKey());

        return ResponseEntity.ok(response);
    }
}
