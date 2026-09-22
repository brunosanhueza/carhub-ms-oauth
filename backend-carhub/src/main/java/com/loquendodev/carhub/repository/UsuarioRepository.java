package com.loquendodev.carhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loquendodev.carhub.models.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Integer> {

    Optional<Usuario> findByEmailUserAndPasswordUser(String email, String password);

    Optional<Usuario> findByEmailUser(String emailUser);
}
