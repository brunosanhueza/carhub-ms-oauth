package com.zacky.reclamos.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDto {
    private Integer idUser;
    private String nameUser;
    private String emailUser;
    private String phoneUser;
}
