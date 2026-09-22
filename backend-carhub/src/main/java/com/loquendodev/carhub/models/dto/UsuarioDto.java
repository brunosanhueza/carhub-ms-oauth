package com.loquendodev.carhub.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioDto {

    private Integer idUser;
    private String nameUser;
    private String emailUser;
    private String phoneUser;

}
