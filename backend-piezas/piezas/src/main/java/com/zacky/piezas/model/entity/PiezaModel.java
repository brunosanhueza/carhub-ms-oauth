package com.zacky.piezas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Pieza")
public class PiezaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Pieza", unique = true, nullable = false)
    private Integer idPieza;

    @Column(name = "Tipo_pieza", nullable = false)
    private String tipoPieza;

    @Column(name = "Nombre_pieza", nullable = false)
    private String nombrePieza;

    @Column(name = "Material_pieza", nullable = false)
    private String materialPieza;

    @Column(name = "Precio_pieza", nullable = false)
    private Float precioPieza;

    // ORIGINAL o ALTERNATIVO
    @Column(name = "Tipo_marca_pieza", nullable = false)
    private String tipoMarcaPieza;

    @Column(name = "Stock_pieza", nullable = false)
    private Integer stockPieza;

    @Column(name = "Descripcion_pieza", length = 500)
    private String descripcionPieza;

    // Del DTO de Vehiculo
    @Column(name = "ID_Vehiculo", nullable = false)
    private Integer idVehiculo;

    @Column(name = "Marca_vehiculo", nullable = false)
    private String marcaVehiculo;

    @Column(name = "Modelo_vehiculo", nullable = false)
    private String modeloVehiculo;

    // Del DTO de Usuario (quien registra la pieza)
    @Column(name = "ID_Usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "Nombre_usuario", nullable = false)
    private String nameUser;

    @Column(name = "Correo_usuario", nullable = false)
    private String emailUser;

    @Column(name = "Telefono_usuario", nullable = false)
    private String phoneUser;
}
