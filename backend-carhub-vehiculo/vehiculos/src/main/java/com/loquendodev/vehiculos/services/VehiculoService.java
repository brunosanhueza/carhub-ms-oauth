package com.loquendodev.vehiculos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loquendodev.vehiculos.models.entity.VehiculoModel;
import com.loquendodev.vehiculos.repository.VehiculoRepository;


@Service
public class VehiculoService {


    @Autowired
    private VehiculoRepository vehiculoRepository;



    public List<VehiculoModel> obtenerVehiculos(){
        return vehiculoRepository.findAll();
    }

    public VehiculoModel guardarVehiculoModel(VehiculoModel veh){
        return vehiculoRepository.save(veh);
    }

    public Optional<VehiculoModel> obtenerVehiculoPorId(Integer id){
        return vehiculoRepository.findById(id);
    }

    public boolean eliminarVehiculoPorId (Integer id){ 
    if (vehiculoRepository.existsById(id)){ 
    vehiculoRepository.deleteById(id); 
    return true; 
    } else {return false;} 

}

public Optional<VehiculoModel> actualizarVehiculo(Integer id, VehiculoModel vehiculoActualizado) {
    return vehiculoRepository.findById(id).map(vehiculoExistente -> {
        vehiculoExistente.setMarcaVehiculo(vehiculoActualizado.getMarcaVehiculo());
        vehiculoExistente.setModeloVehiculo(vehiculoActualizado.getModeloVehiculo());
        vehiculoExistente.setAnioVehiculo(vehiculoActualizado.getAnioVehiculo());
        vehiculoExistente.setTipoBencinaVehiculo(vehiculoActualizado.getTipoBencinaVehiculo());
        return vehiculoRepository.save(vehiculoExistente);
    });
}

public List<VehiculoModel> obtenerVehiculoPorMarca(String marca){
    return vehiculoRepository.findByMarcaVehiculo(marca);
}



}
