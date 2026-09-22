package com.zacky.piezas.repository;

import com.zacky.piezas.model.entity.PiezaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PiezaRepository extends JpaRepository<PiezaModel, Integer> {
    List<PiezaModel> findByMarcaVehiculo(String marcaVehiculo);
    List<PiezaModel> findByModeloVehiculo(String modeloVehiculo);
    List<PiezaModel> findByTipoPieza(String tipoPieza);
    List<PiezaModel> findByMarcaVehiculoAndModeloVehiculo(String marcaVehiculo, String modeloVehiculo);
}
