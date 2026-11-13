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

    @Autowired
    private com.loquendodev.vehiculos.publisher.VehiculoActividadKafkaProducer kafkaProducer;

    public List<VehiculoModel> obtenerVehiculos(){
        return vehiculoRepository.findAll();
    }

    public VehiculoModel guardarVehiculoModel(VehiculoModel veh){
        VehiculoModel guardado = vehiculoRepository.save(veh);
        kafkaProducer.emitirActividadVehiculo(guardado, "CREADO", null);
        return guardado;
    }

    public Optional<VehiculoModel> obtenerVehiculoPorId(Integer id){
        Optional<VehiculoModel> opt = vehiculoRepository.findById(id);
        opt.ifPresent(v -> kafkaProducer.emitirActividadVehiculo(v, "VISTO", null));
        return opt;
    }

    public boolean eliminarVehiculoPorId (Integer id){ 
        if (vehiculoRepository.existsById(id)){ 
            vehiculoRepository.deleteById(id); 
            return true; 
        } else {
            return false;
        } 
    }

    public Optional<VehiculoModel> actualizarVehiculo(Integer id, VehiculoModel vehiculoActualizado) {
        return vehiculoRepository.findById(id).map(vehiculoExistente -> {
            vehiculoExistente.setMarcaVehiculo(vehiculoActualizado.getMarcaVehiculo());
            vehiculoExistente.setModeloVehiculo(vehiculoActualizado.getModeloVehiculo());
            vehiculoExistente.setAnioVehiculo(vehiculoActualizado.getAnioVehiculo());
            vehiculoExistente.setTipoBencinaVehiculo(vehiculoActualizado.getTipoBencinaVehiculo());
            VehiculoModel guardado = vehiculoRepository.save(vehiculoExistente);
            kafkaProducer.emitirActividadVehiculo(guardado, "ACTUALIZADO", null);
            return guardado;
        });
    }

public List<VehiculoModel> obtenerVehiculoPorMarca(String marca){
    return vehiculoRepository.findByMarcaVehiculo(marca);
}



}
