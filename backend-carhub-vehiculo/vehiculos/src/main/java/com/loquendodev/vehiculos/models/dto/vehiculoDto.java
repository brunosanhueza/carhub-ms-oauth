package com.loquendodev.vehiculos.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class vehiculoDto {
    private Integer idVehiculo;
    private String marcaVehiculo;
    private String modeloVehiculo;
    private String anioVehiculo;
    private String tipoBencinaVehiculo;
    private String tipoTransmisionVehiculo;
    private String tipoPropulsionVehiculo;

}
