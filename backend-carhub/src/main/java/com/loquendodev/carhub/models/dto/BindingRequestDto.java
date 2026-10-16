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
public class BindingRequestDto {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Nombre de cola inválido")
    private String queueName;

    @NotBlank(message = "El nombre del exchange es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Nombre de exchange inválido")
    private String exchangeName;

    @NotBlank(message = "La routing key es obligatoria")
    private String routingKey;

    private Map<String, Object> arguments;
}
