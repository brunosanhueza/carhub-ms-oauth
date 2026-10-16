package com.loquendodev.carhub.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO para la transmisión asíncrona de eventos de usuario (bienvenida, registro, alertas).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioNotificacionMensajeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idUser;
    private String nameUser;
    private String emailUser;
    private String phoneUser;
    private String tipoEvento; // REGISTRO, BIENVENIDA, ACTUALIZACION, OAUTH_SYNC
    private String mensaje;
    private Date fechaEvento;
}
