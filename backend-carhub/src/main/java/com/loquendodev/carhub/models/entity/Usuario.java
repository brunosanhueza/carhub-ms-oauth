package com.loquendodev.carhub.models.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_Usuario", nullable = false, unique = true)
    private Integer idUser;

    @Column(name= "Nombre_usuario", nullable = false, unique = false)
    private String nameUser;

    @Column(name= "Correo_usuario", nullable = false, unique = true)
    private String emailUser;

    @Column(name= "Password_usuario", nullable = false, unique = true)
    private String passwordUser;

    @Column(name= "Numero_tel_usuario", nullable = false, unique = true)
    private String phoneUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "Rol_usuario", nullable = false)
    private RolUsuario rolUsuario = RolUsuario.USUARIO;

}
