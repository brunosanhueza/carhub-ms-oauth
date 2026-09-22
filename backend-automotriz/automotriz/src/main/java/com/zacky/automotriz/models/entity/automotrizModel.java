package com.zacky.automotriz.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

}
