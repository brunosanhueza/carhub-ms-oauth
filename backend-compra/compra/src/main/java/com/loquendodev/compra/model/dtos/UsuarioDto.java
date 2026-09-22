package com.loquendodev.compra.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//atributos extraidos directamente desde el modelo de usuario, obviando atributos de caracter sensible.
public class UsuarioDto {
    private Integer idUser;

    private String nameUser;

    private String emailUser;

    private String phoneUser;
}
