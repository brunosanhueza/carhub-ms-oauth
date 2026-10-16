package com.zacky.automotriz.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Automotriz")
public class automotrizModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "Id_Automotriz", unique= true, nullable = false)
    private Integer idAutomotriz;

    @Column(name="Nombre_automotriz", unique = true, nullable = false)
    private String nombreAutomotriz;

    @Column(name="Direccion_automotriz", unique = false, nullable = false)
    private String direccionAutomotriz;

    public Integer getIdAutomotriz() {
        return idAutomotriz;
    }

    public void setIdAutomotriz(Integer idAutomotriz) {
        this.idAutomotriz = idAutomotriz;
    }

    public String getNombreAutomotriz() {
        return nombreAutomotriz;
    }

    public void setNombreAutomotriz(String nombreAutomotriz) {
        this.nombreAutomotriz = nombreAutomotriz;
    }

    public String getDireccionAutomotriz() {
        return direccionAutomotriz;
    }

    public void setDireccionAutomotriz(String direccionAutomotriz) {
        this.direccionAutomotriz = direccionAutomotriz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        automotrizModel that = (automotrizModel) o;
        return Objects.equals(idAutomotriz, that.idAutomotriz);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "automotrizModel{" +
                "idAutomotriz=" + idAutomotriz +
                ", nombreAutomotriz='" + nombreAutomotriz + '\'' +
                ", direccionAutomotriz='" + direccionAutomotriz + '\'' +
                '}';
    }
}
