package com.loquendodev.carhub.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
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

    // Métodos getter y setter explícitos para evitar fallos de bytecode (Code length 0) en Java 25 / VS Code
    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
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

    public String getPasswordUser() {
        return passwordUser;
    }

    public void setPasswordUser(String passwordUser) {
        this.passwordUser = passwordUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    public RolUsuario getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(RolUsuario rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return idUser != null && idUser.equals(usuario.idUser);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUser=" + idUser +
                ", nameUser='" + nameUser + '\'' +
                ", emailUser='" + emailUser + '\'' +
                ", phoneUser='" + phoneUser + '\'' +
                ", rolUsuario=" + rolUsuario +
                '}';
    }
}
