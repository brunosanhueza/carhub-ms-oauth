package com.zacky.piezas.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehiculoDto {
    private Integer idVehiculo;
    private String marcaVehiculo;
    private String modeloVehiculo;
    private String anioVehiculo;
    private String tipoBencinaVehiculo;
    private String tipoTransmisionVehiculo;
    private String tipoPropulsionVehiculo;
    private Float precioVehiculo;
}
