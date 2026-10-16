package com.zacky.piezas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    public Integer getIdPieza() {
        return idPieza;
    }

    public void setIdPieza(Integer idPieza) {
        this.idPieza = idPieza;
    }

    public String getTipoPieza() {
        return tipoPieza;
    }

    public void setTipoPieza(String tipoPieza) {
        this.tipoPieza = tipoPieza;
    }

    public String getNombrePieza() {
        return nombrePieza;
    }

    public void setNombrePieza(String nombrePieza) {
        this.nombrePieza = nombrePieza;
    }

    public String getMaterialPieza() {
        return materialPieza;
    }

    public void setMaterialPieza(String materialPieza) {
        this.materialPieza = materialPieza;
    }

    public Float getPrecioPieza() {
        return precioPieza;
    }

    public void setPrecioPieza(Float precioPieza) {
        this.precioPieza = precioPieza;
    }

    public String getTipoMarcaPieza() {
        return tipoMarcaPieza;
    }

    public void setTipoMarcaPieza(String tipoMarcaPieza) {
        this.tipoMarcaPieza = tipoMarcaPieza;
    }

    public Integer getStockPieza() {
        return stockPieza;
    }

    public void setStockPieza(Integer stockPieza) {
        this.stockPieza = stockPieza;
    }

    public String getDescripcionPieza() {
        return descripcionPieza;
    }

    public void setDescripcionPieza(String descripcionPieza) {
        this.descripcionPieza = descripcionPieza;
    }

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

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PiezaModel that = (PiezaModel) o;
        return Objects.equals(idPieza, that.idPieza);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "PiezaModel{" +
                "idPieza=" + idPieza +
                ", nombrePieza='" + nombrePieza + '\'' +
                ", tipoPieza='" + tipoPieza + '\'' +
                ", precioPieza=" + precioPieza +
                '}';
    }
}
