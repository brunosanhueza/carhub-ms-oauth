package com.loquendodev.compra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loquendodev.compra.model.entity.facturaModel;
import java.util.List;


@Repository
public interface compraRepository extends JpaRepository<facturaModel, Integer>{

    List<facturaModel> findByIdUser(Integer idUser);

}
