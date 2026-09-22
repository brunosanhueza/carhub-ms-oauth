package com.zacky.reclamos;

import com.zacky.reclamos.controller.ReclamoController;
import com.zacky.reclamos.model.entity.ReclamoModel;
import com.zacky.reclamos.services.ReclamoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReclamoController.class)
class ReclamosApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReclamoService reclamoService;

    private ReclamoModel reclamoEjemplo() {
        ReclamoModel r = new ReclamoModel();
        r.setIdReclamo(1);
        r.setIdUsuario(5);
        r.setNameUser("Juan Pérez");
        r.setTipoReclamo("Garantía");
        r.setDescripcionReclamo("El motor falla al arrancar");
        return r;
    }

    @Test
    void listarReclamos_retornaOk() throws Exception {
        when(reclamoService.obtenerReclamos()).thenReturn(List.of(reclamoEjemplo()));

        mockMvc.perform(get("/reclamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoReclamo").value("Garantía"));
    }

    @Test
    void obtenerPorId_encontrado_retorna200() throws Exception {
        when(reclamoService.obtenerReclamoPorId(1)).thenReturn(Optional.of(reclamoEjemplo()));

        mockMvc.perform(get("/reclamos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameUser").value("Juan Pérez"));
    }

    @Test
    void obtenerPorId_noEncontrado_retorna404() throws Exception {
        when(reclamoService.obtenerReclamoPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reclamos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerReclamosPorUsuario_retornaLista() throws Exception {
        when(reclamoService.obtenerReclamosPorUsuario(5)).thenReturn(List.of(reclamoEjemplo()));

        mockMvc.perform(get("/reclamos/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(5));
    }

    @Test
    void crearReclamo_retornaOk() throws Exception {
        when(reclamoService.crearReclamo(5, "Garantía", "El motor falla al arrancar"))
                .thenReturn(reclamoEjemplo());

        mockMvc.perform(post("/reclamos")
                        .param("idUsuario", "5")
                        .param("tipoReclamo", "Garantía")
                        .param("descripcionReclamo", "El motor falla al arrancar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReclamo").value(1));
    }
}
