package com.loquendodev.compra.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaTicketMensajeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer nroFactura;
    private Integer idUser;
    private String nameUser;
    private String emailUser;
    private String phoneUser;
    private String marcaVehiculo;
    private String modeloVehiculo;
    private String anioVehiculo;
    private String nombreAutomotriz;
    private String metodoPago;
    private Float subtotal;
    private Date fechaEmision;
}
