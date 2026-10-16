package com.loquendodev.vehiculos.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
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

    // Getters y Setters explícitos para evitar fallos de bytecode (Code length 0) en Java 21 / VS Code
    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getMarcaVehiculo() {
        return marcaVehiculo;
    }

    public void setMarcaVehiculo(String marcaVehiculo) {
        this.marcaVehiculo = marcaVehiculo;
    }

    public String getModeloVehiculo() {
        return modeloVehiculo;
    }

    public void setModeloVehiculo(String modeloVehiculo) {
        this.modeloVehiculo = modeloVehiculo;
    }

    public String getAnioVehiculo() {
        return anioVehiculo;
    }

    public void setAnioVehiculo(String anioVehiculo) {
        this.anioVehiculo = anioVehiculo;
    }

    public String getTipoBencinaVehiculo() {
        return tipoBencinaVehiculo;
    }

    public void setTipoBencinaVehiculo(String tipoBencinaVehiculo) {
        this.tipoBencinaVehiculo = tipoBencinaVehiculo;
    }

    public String getTipoTransmisionVehiculo() {
        return tipoTransmisionVehiculo;
    }

    public void setTipoTransmisionVehiculo(String tipoTransmisionVehiculo) {
        this.tipoTransmisionVehiculo = tipoTransmisionVehiculo;
    }

    public String getTipoPropulsionVehiculo() {
        return tipoPropulsionVehiculo;
    }

    public void setTipoPropulsionVehiculo(String tipoPropulsionVehiculo) {
        this.tipoPropulsionVehiculo = tipoPropulsionVehiculo;
    }

    public Float getPrecioVehiculo() {
        return precioVehiculo;
    }

    public void setPrecioVehiculo(Float precioVehiculo) {
        this.precioVehiculo = precioVehiculo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehiculoModel that = (VehiculoModel) o;
        return Objects.equals(idVehiculo, that.idVehiculo);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "VehiculoModel{" +
                "idVehiculo=" + idVehiculo +
                ", marcaVehiculo='" + marcaVehiculo + '\'' +
                ", modeloVehiculo='" + modeloVehiculo + '\'' +
                ", anioVehiculo='" + anioVehiculo + '\'' +
                ", tipoBencinaVehiculo='" + tipoBencinaVehiculo + '\'' +
                ", tipoTransmisionVehiculo='" + tipoTransmisionVehiculo + '\'' +
                ", tipoPropulsionVehiculo='" + tipoPropulsionVehiculo + '\'' +
                ", precioVehiculo=" + precioVehiculo +
                '}';
    }
}
