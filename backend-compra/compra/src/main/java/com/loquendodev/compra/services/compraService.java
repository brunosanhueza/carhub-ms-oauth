package com.loquendodev.compra.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.loquendodev.compra.model.entity.facturaModel;
import com.loquendodev.compra.model.dtos.UsuarioDto;
import com.loquendodev.compra.model.dtos.vehiculoDto;
import com.loquendodev.compra.model.dtos.automotrizDto;
import com.loquendodev.compra.repository.compraRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class compraService {

    @Autowired
    private compraRepository compraRepo;



    /*toooda la configuracion del restTemplate */
    @Autowired
    private RestTemplate restTemplate;
                                        
    private final String URL_USUARIO    = "http://100.57.199.0:8066/api/v1/usuarios/";
    private final String URL_VEHICULO   = "http://100.57.199.0:8062/api/v2/vehiculos/";
    private final String URL_AUTOMOTRIZ = "http://100.57.199.0:8061/api/v3/automotriz/";

    public facturaModel emitirCompra(Integer idUsuario, Integer idVehiculo,
                                   Integer idAutomotriz, String metodoPago) {

        // 1. Consulta datos de cada microservicio
        UsuarioDto usuario = restTemplate.getForObject(
            URL_USUARIO + idUsuario, UsuarioDto.class);

        vehiculoDto vehiculo = restTemplate.getForObject(
            URL_VEHICULO + idVehiculo, vehiculoDto.class);

        automotrizDto automotriz = restTemplate.getForObject(
            URL_AUTOMOTRIZ + idAutomotriz, automotrizDto.class);

        // 2. Arma la factura
        facturaModel compra = new facturaModel();

        compra.setSubtotal_factura(vehiculo.getPrecioVehiculo());
        compra.setMetodo_pago_factura(metodoPago);
        compra.setFecha_emision_factura(new Date());

        // Datos del usuario
        compra.setIdUser(usuario.getIdUser());
        compra.setNameUser(usuario.getNameUser());
        compra.setEmailUser(usuario.getEmailUser());
        compra.setPhoneUser(usuario.getPhoneUser());

        // Datos del vehiculo
        compra.setMarcaVehiculo(vehiculo.getMarcaVehiculo());
        compra.setModeloVehiculo(vehiculo.getModeloVehiculo());
        compra.setAnioVehiculo(vehiculo.getAnioVehiculo());
        compra.setTipoBencinaVehiculo(vehiculo.getTipoBencinaVehiculo());
        compra.setTipoTransmisionVehiculo(vehiculo.getTipoTransmisionVehiculo());
        compra.setTipoPropulsionVehiculo(vehiculo.getTipoPropulsionVehiculo());

        // Datos de la automotriz
        compra.setNombreAutomotriz(automotriz.getNombreAutomotriz());
        compra.setDireccionAutomotriz(automotriz.getDireccionAutomotriz());

        // 3. Guarda en DB
        return compraRepo.save(compra);
    }

    public List<facturaModel> obtenerCompras() {
        return compraRepo.findAll();
    }

    public Optional<facturaModel> obtenerCompraPorId(Integer id) {
        return compraRepo.findById(id);
    }

    public List<facturaModel> obtenerComprasPorUsuario(Integer idUsuario) {
        return compraRepo.findByIdUser(idUsuario);
    }
}
