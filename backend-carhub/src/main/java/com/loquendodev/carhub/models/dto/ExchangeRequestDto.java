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
public class ExchangeRequestDto {

    @NotBlank(message = "El nombre del exchange es obligatorio y no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre del exchange contiene caracteres no válidos (solo letras, números, '.', '-' y '_')")
    private String name;

    @NotBlank(message = "El tipo de exchange es obligatorio (ej: direct, topic, fanout, headers)")
    @Pattern(regexp = "^(?i)(direct|topic|fanout|headers)$", message = "Tipo de exchange inválido. Tipos permitidos: direct, topic, fanout, headers")
    private String type = "direct";

    private boolean durable = true;

    private boolean autoDelete = false;

    private Map<String, Object> arguments;
}
