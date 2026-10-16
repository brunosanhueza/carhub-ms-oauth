package com.zacky.reclamos.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReclamoTicketMensajeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idReclamo;
    private Integer idUsuario;
    private String nameUser;
    private String emailUser;
    private String phoneUser;
    private String tipoReclamo;
    private String descripcionReclamo;
    private String estadoReclamo;
    private Date fechaReclamo;
}
