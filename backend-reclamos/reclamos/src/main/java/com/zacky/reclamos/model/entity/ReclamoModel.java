package com.zacky.reclamos.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ReclamoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Reclamo", unique = true, nullable = false)
    private Integer idReclamo;

    @Column(name = "Tipo_reclamo", nullable = false)
    private String tipoReclamo;

    @Column(name = "Descripcion_reclamo", nullable = false, length = 1000)
    private String descripcionReclamo;

    @Column(name = "Estado_reclamo", nullable = false)
    private String estadoReclamo;

    @Column(name = "Fecha_reclamo", nullable = false)
    private Date fechaReclamo;

    // Datos del DTO de usuario
    @Column(name = "ID_Usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "Nombre_usuario", nullable = false)
    private String nameUser;

    @Column(name = "Correo_usuario", nullable = false)
    private String emailUser;

    @Column(name = "Telefono_usuario", nullable = false)
    private String phoneUser;
}
