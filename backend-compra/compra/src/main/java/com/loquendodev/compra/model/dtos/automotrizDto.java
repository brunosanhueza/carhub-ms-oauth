package com.loquendodev.compra.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class automotrizDto {
    private Integer idAutomotriz;
    private String nombreAutomotriz;
    private String direccionAutomotriz;

}
