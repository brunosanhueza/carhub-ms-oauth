package com.loquendodev.vehiculos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loquendodev.vehiculos.models.entity.VehiculoModel;
import java.util.List;



@Repository
public interface VehiculoRepository extends JpaRepository <VehiculoModel, Integer>{
    List<VehiculoModel> findByMarcaVehiculo(String marcaVehiculo);

}
