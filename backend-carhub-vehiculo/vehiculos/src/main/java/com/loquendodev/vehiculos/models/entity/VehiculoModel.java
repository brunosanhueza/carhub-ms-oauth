package com.loquendodev.vehiculos.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity

public class VehiculoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "ID_Vehiculo", unique = true, nullable = false)
    private Integer idVehiculo;

    @Column(name="Marca_vehiculo", unique = false, nullable = false)
    private String marcaVehiculo;

    @Column(name= "Modelo_vehiculo", unique = false, nullable = false)
    private String modeloVehiculo;

    @Column(name= "Anio_vehiculo", unique = false, nullable = true)
    private String anioVehiculo;

    @Column(name="Tipo_combustible", unique = false, nullable = false)
    private String tipoBencinaVehiculo;

    @Column(name="Tipo_transmision", unique = false, nullable = false)
    private String tipoTransmisionVehiculo;

    @Column(name = "Tipo_propulsion", unique = false, nullable = false)
    private String tipoPropulsionVehiculo;

    @Column(name = "Precio_vehiculo", nullable = true)
    private Float precioVehiculo;

}
