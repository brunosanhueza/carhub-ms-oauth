package com.zacky.automotriz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacky.automotriz.controller.automotrizController;
import com.zacky.automotriz.models.entity.automotrizModel;
import com.zacky.automotriz.services.automotrizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AutomotrizApplicationTests {

    @Mock
    private automotrizService automotrizSer;

    @InjectMocks
    private automotrizController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private automotrizModel automotrizEjemplo() {
        automotrizModel a = new automotrizModel();
        a.setIdAutomotriz(1);
        a.setNombreAutomotriz("Toyota");
        a.setDireccionAutomotriz("Av. Providencia 1234, Santiago");
        return a;
    }

    @Test
    void listarAutomotrices_retornaOk() throws Exception {
        when(automotrizSer.obtenerAutomotrices()).thenReturn(List.of(automotrizEjemplo()));

        mockMvc.perform(get("/api/v3/automotriz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreAutomotriz").value("Toyota"));
    }

    @Test
    void obtenerPorId_encontrado_retorna200() throws Exception {
        when(automotrizSer.obtenerAutomotrizPorId(1)).thenReturn(Optional.of(automotrizEjemplo()));

        mockMvc.perform(get("/api/v3/automotriz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreAutomotriz").value("Toyota"));
    }

    @Test
    void obtenerPorId_noEncontrado_retorna404() throws Exception {
        when(automotrizSer.obtenerAutomotrizPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v3/automotriz/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearAutomotriz_retorna201() throws Exception {
        when(automotrizSer.guardarAutomotriz(any())).thenReturn(automotrizEjemplo());

        mockMvc.perform(post("/api/v3/automotriz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(automotrizEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAutomotriz").value(1));
    }

    @Test
    void eliminarAutomotriz_encontrado_retorna200() throws Exception {
        when(automotrizSer.eliminarAutomotrizPorId(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v3/automotriz/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarAutomotriz_noEncontrado_retorna404() throws Exception {
        when(automotrizSer.eliminarAutomotrizPorId(99)).thenReturn(false);

        mockMvc.perform(delete("/api/v3/automotriz/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void modificarAutomotriz_encontrado_retorna200() throws Exception {
        automotrizModel actualizada = automotrizEjemplo();
        actualizada.setNombreAutomotriz("Honda");

        when(automotrizSer.modificarAutomotriz(any(), any())).thenReturn(Optional.of(actualizada));

        mockMvc.perform(put("/api/v3/automotriz/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreAutomotriz").value("Honda"));
    }

    @Test
    void modificarAutomotriz_noEncontrado_retorna404() throws Exception {
        when(automotrizSer.modificarAutomotriz(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v3/automotriz/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(automotrizEjemplo())))
                .andExpect(status().isNotFound());
    }
}
