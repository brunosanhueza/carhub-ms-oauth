package com.loquendodev.compra.model.entity;
import java.sql.Date;

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



public class facturaModel{


    //atributos que contiene la factura en sí

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="NRO_Factura", unique = true, nullable = false)
    private Integer nro_factura;

    @Column(name = "Subtotal")
    private Float subtotal_factura;

    @Column(name= "Metodo_de_pago")
    private String metodo_pago_factura;

    @Column(name= "Fecha_Emision_factura")
    private java.util.Date fecha_emision_factura;




    //atributos del Dto de usuario:
    @Column(name="ID_Usuario", nullable = false)
    private Integer idUser;

    @Column(name= "Nombre_usuario", nullable = false)
    private String nameUser;

    @Column(name= "Correo_usuario", nullable = false)
    private String emailUser;

    @Column(name= "Numero_tel_usuario", nullable = false)
    private String phoneUser;

    //Dto Vehiculos:

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

    //Dto Automotriz
    
    @Column(name="Nombre_automotriz", nullable = false)
    private String nombreAutomotriz;

    @Column(name="Direccion_automotriz", unique = false, nullable = false)
    private String direccionAutomotriz;



}