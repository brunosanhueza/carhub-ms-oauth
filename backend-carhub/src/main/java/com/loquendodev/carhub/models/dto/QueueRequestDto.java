package com.loquendodev.carhub.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueRequestDto {

    @NotBlank(message = "El nombre de la cola es obligatorio y no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre de la cola contiene caracteres no válidos (solo letras, números, '.', '-' y '_')")
    private String name;

    private boolean durable = true;

    private boolean exclusive = false;

    private boolean autoDelete = false;

    private String dlxExchange;

    private String dlqRoutingKey;

    private Map<String, Object> arguments;
}
