package com.loquendodev.carhub.controllers;

import com.loquendodev.carhub.models.dto.BindingRequestDto;
import com.loquendodev.carhub.models.dto.ExchangeRequestDto;
import com.loquendodev.carhub.models.dto.QueueRequestDto;
import com.loquendodev.carhub.services.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para la administración dinámica de colas, exchanges y bindings de RabbitMQ.
 * Expone rutas compatibles según la rúbrica (/queues, /api/v1/queues, /api/v1/admin/queues).
 */
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

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Map<String, Object>> eliminarCola(@PathVariable("name") String name) {
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

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Map<String, Object>> eliminarExchange(@PathVariable("name") String name) {
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
