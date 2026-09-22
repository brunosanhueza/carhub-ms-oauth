package com.zacky.piezas.services;

import com.zacky.piezas.model.dtos.UsuarioDto;
import com.zacky.piezas.model.dtos.VehiculoDto;
import com.zacky.piezas.model.entity.PiezaModel;
import com.zacky.piezas.repository.PiezaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class PiezaService {

    @Autowired
    private PiezaRepository piezaRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String URL_VEHICULO = "http://100.57.199.0:8062/api/v2/vehiculos/";
    private final String URL_USUARIO  = "http://100.57.199.0:8066/api/v1/usuarios/";

    public PiezaModel registrarPieza(Integer idVehiculo, Integer idUsuario,
                                     String tipoPieza, String nombrePieza,
                                     String materialPieza, Float precioPieza,
                                     String tipoMarcaPieza, Integer stockPieza,
                                     String descripcionPieza) {

        VehiculoDto vehiculo = restTemplate.getForObject(URL_VEHICULO + idVehiculo, VehiculoDto.class);
        UsuarioDto  usuario  = restTemplate.getForObject(URL_USUARIO  + idUsuario,  UsuarioDto.class);

        PiezaModel pieza = new PiezaModel();
        pieza.setTipoPieza(tipoPieza);
        pieza.setNombrePieza(nombrePieza);
        pieza.setMaterialPieza(materialPieza);
        pieza.setPrecioPieza(precioPieza);
        pieza.setTipoMarcaPieza(tipoMarcaPieza);
        pieza.setStockPieza(stockPieza);
        pieza.setDescripcionPieza(descripcionPieza);

        pieza.setIdVehiculo(vehiculo.getIdVehiculo());
        pieza.setMarcaVehiculo(vehiculo.getMarcaVehiculo());
        pieza.setModeloVehiculo(vehiculo.getModeloVehiculo());

        pieza.setIdUsuario(usuario.getIdUser());
        pieza.setNameUser(usuario.getNameUser());
        pieza.setEmailUser(usuario.getEmailUser());
        pieza.setPhoneUser(usuario.getPhoneUser());

        return piezaRepository.save(pieza);
    }

    public List<PiezaModel> listarPiezas() {
        return piezaRepository.findAll();
    }

    public Optional<PiezaModel> obtenerPorId(Integer id) {
        return piezaRepository.findById(id);
    }

    public List<PiezaModel> listarPorMarca(String marcaVehiculo) {
        return piezaRepository.findByMarcaVehiculo(marcaVehiculo);
    }

    public List<PiezaModel> listarPorModelo(String modeloVehiculo) {
        return piezaRepository.findByModeloVehiculo(modeloVehiculo);
    }

    public List<PiezaModel> listarPorMarcaYModelo(String marcaVehiculo, String modeloVehiculo) {
        return piezaRepository.findByMarcaVehiculoAndModeloVehiculo(marcaVehiculo, modeloVehiculo);
    }

    public boolean eliminarPieza(Integer id) {
        if (piezaRepository.existsById(id)) {
            piezaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
