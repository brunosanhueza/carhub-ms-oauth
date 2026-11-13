package com.zacky.reclamos.services;

import com.zacky.reclamos.model.dtos.UsuarioDto;
import com.zacky.reclamos.model.entity.ReclamoModel;
import com.zacky.reclamos.repository.ReclamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReclamoService {

    @Autowired
    private ReclamoRepository reclamoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private com.zacky.reclamos.publisher.ReclamoEventPublisher reclamoEventPublisher;

    @org.springframework.beans.factory.annotation.Value("${services.usuario.url:http://localhost:8066/api/v1/usuarios/}")
    private String URL_USUARIO;

    public ReclamoModel crearReclamo(Integer idUsuario, String tipoReclamo, String descripcionReclamo) {

        UsuarioDto usuario = null;
        try {
            usuario = restTemplate.getForObject(URL_USUARIO + idUsuario, UsuarioDto.class);
        } catch (Exception e) {
            System.err.println("[WARN] No se pudo obtener información del usuario ID " + idUsuario + ": " + e.getMessage());
        }

        ReclamoModel reclamo = new ReclamoModel();
        reclamo.setTipoReclamo(tipoReclamo);
        reclamo.setDescripcionReclamo(descripcionReclamo);
        reclamo.setEstadoReclamo("PENDIENTE");
        reclamo.setFechaReclamo(new Date());

        if (usuario != null) {
            reclamo.setIdUsuario(usuario.getIdUser() != null ? usuario.getIdUser() : idUsuario);
            reclamo.setNameUser(usuario.getNameUser() != null ? usuario.getNameUser() : "Usuario #" + idUsuario);
            reclamo.setEmailUser(usuario.getEmailUser() != null ? usuario.getEmailUser() : "cliente" + idUsuario + "@carhub.cl");
            reclamo.setPhoneUser(usuario.getPhoneUser() != null ? usuario.getPhoneUser() : "+56900000000");
        } else {
            reclamo.setIdUsuario(idUsuario);
            reclamo.setNameUser("Usuario #" + idUsuario);
            reclamo.setEmailUser("cliente" + idUsuario + "@carhub.cl");
            reclamo.setPhoneUser("+56900000000");
        }

        ReclamoModel reclamoGuardado = reclamoRepository.save(reclamo);

        // Publicación asíncrona a RabbitMQ para generar ticket de soporte
        try {
            reclamoEventPublisher.publicarTicketReclamo(reclamoGuardado);
        } catch (Exception e) {
            System.err.println("[WARN] No se pudo publicar evento a RabbitMQ: " + e.getMessage());
        }

        return reclamoGuardado;
    }

    public List<ReclamoModel> obtenerReclamos() {
        return reclamoRepository.findAll();
    }

    public Optional<ReclamoModel> obtenerReclamoPorId(Integer id) {
        return reclamoRepository.findById(id);
    }

    public List<ReclamoModel> obtenerReclamosPorUsuario(Integer idUsuario) {
        return reclamoRepository.findByIdUsuario(idUsuario);
    }
}
