package com.zacky.piezas;

import com.zacky.piezas.controller.PiezaController;
import com.zacky.piezas.model.entity.PiezaModel;
import com.zacky.piezas.services.PiezaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PiezasApplicationTests {

    @Mock
    private PiezaService piezaService;

    @InjectMocks
    private PiezaController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private PiezaModel piezaEjemplo() {
        PiezaModel p = new PiezaModel();
        p.setIdPieza(1);
        p.setNombrePieza("Filtro de aceite");
        p.setTipoPieza("Filtro");
        p.setMaterialPieza("Metal");
        p.setPrecioPieza(25000f);
        p.setTipoMarcaPieza("Toyota");
        p.setStockPieza(10);
        p.setMarcaVehiculo("Toyota");
        p.setModeloVehiculo("Corolla");
        p.setIdVehiculo(1);
        return p;
    }

    @Test
    void listarPiezas_retornaOk() throws Exception {
        when(piezaService.listarPiezas()).thenReturn(List.of(piezaEjemplo()));

        mockMvc.perform(get("/api/v7/piezas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombrePieza").value("Filtro de aceite"));
    }

    @Test
    void obtenerPorId_encontrado_retorna200() throws Exception {
        when(piezaService.obtenerPorId(1)).thenReturn(Optional.of(piezaEjemplo()));

        mockMvc.perform(get("/api/v7/piezas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoPieza").value("Filtro"));
    }

    @Test
    void obtenerPorId_noEncontrado_retorna404() throws Exception {
        when(piezaService.obtenerPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v7/piezas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarPorMarca_retornaLista() throws Exception {
        when(piezaService.listarPorMarca("Toyota")).thenReturn(List.of(piezaEjemplo()));

        mockMvc.perform(get("/api/v7/piezas/marca/Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marcaVehiculo").value("Toyota"));
    }

    @Test
    void eliminarPieza_encontrada_retorna200() throws Exception {
        when(piezaService.eliminarPieza(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v7/piezas/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarPieza_noEncontrada_retorna404() throws Exception {
        when(piezaService.eliminarPieza(99)).thenReturn(false);

        mockMvc.perform(delete("/api/v7/piezas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarPorModelo_retornaLista() throws Exception {
        when(piezaService.listarPorModelo("Corolla")).thenReturn(List.of(piezaEjemplo()));

        mockMvc.perform(get("/api/v7/piezas/modelo/Corolla"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modeloVehiculo").value("Corolla"));
    }

    @Test
    void registrarPieza_retornaOk() throws Exception {
        when(piezaService.registrarPieza(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(piezaEjemplo());

        mockMvc.perform(post("/api/v7/piezas")
                        .param("idVehiculo", "1")
                        .param("idUsuario", "5")
                        .param("tipoPieza", "Filtro")
                        .param("nombrePieza", "Filtro de aceite")
                        .param("materialPieza", "Metal")
                        .param("precioPieza", "25000")
                        .param("tipoMarcaPieza", "Toyota")
                        .param("stockPieza", "10")
                        .param("descripcionPieza", "Filtro original"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombrePieza").value("Filtro de aceite"));
    }
}
