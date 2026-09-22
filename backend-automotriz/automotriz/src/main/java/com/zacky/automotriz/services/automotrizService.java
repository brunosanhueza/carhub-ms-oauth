package com.zacky.automotriz.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zacky.automotriz.models.entity.automotrizModel;
import com.zacky.automotriz.repository.automotrizRepository;

@Service
public class automotrizService {

    @Autowired
    private automotrizRepository automotrizRepo;


   public List<automotrizModel> obtenerAutomotrices() {
    return automotrizRepo.findAll();
}

public automotrizModel guardarAutomotriz(automotrizModel automotriz) {
    return automotrizRepo.save(automotriz);
}

public Optional<automotrizModel> obtenerAutomotrizPorId(Integer id) {
    return automotrizRepo.findById(id);
}

public Optional<automotrizModel> modificarAutomotriz(Integer id, automotrizModel automotrizActualizada) {
    return automotrizRepo.findById(id).map(automotrizExistente -> {
        automotrizExistente.setNombreAutomotriz(automotrizActualizada.getNombreAutomotriz());
        automotrizExistente.setDireccionAutomotriz(automotrizActualizada.getDireccionAutomotriz());
        return automotrizRepo.save(automotrizExistente);
    });
}

public boolean eliminarAutomotrizPorId(Integer id) {
    if (automotrizRepo.existsById(id)) {
        automotrizRepo.deleteById(id);
        return true;
    }
    return false;
}

}
