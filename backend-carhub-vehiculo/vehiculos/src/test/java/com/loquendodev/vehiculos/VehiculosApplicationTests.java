package com.loquendodev.vehiculos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loquendodev.vehiculos.controller.VehiculoController;
import com.loquendodev.vehiculos.models.entity.VehiculoModel;
import com.loquendodev.vehiculos.repository.VehiculoRepository;
import com.loquendodev.vehiculos.services.VehiculoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiculoController.class)
class VehiculosApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehiculoService vehiculoService;

    @MockitoBean
    private VehiculoRepository vehiculoRepository;

    private VehiculoModel vehiculoEjemplo() {
        VehiculoModel v = new VehiculoModel();
        v.setIdVehiculo(1);
        v.setMarcaVehiculo("Toyota");
        v.setModeloVehiculo("Corolla");
        v.setAnioVehiculo("2023");
        v.setTipoBencinaVehiculo("Gasolina");
        v.setTipoTransmisionVehiculo("Automático");
        v.setTipoPropulsionVehiculo("Tracción delantera");
        v.setPrecioVehiculo(15000000f);
        return v;
    }

    @Test
    void listarVehiculos_retornaOk() throws Exception {
        when(vehiculoService.obtenerVehiculos()).thenReturn(List.of(vehiculoEjemplo()));

        mockMvc.perform(get("/api/v2/vehiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marcaVehiculo").value("Toyota"));
    }

    @Test
    void obtenerPorId_encontrado_retorna200() throws Exception {
        when(vehiculoService.obtenerVehiculoPorId(1)).thenReturn(Optional.of(vehiculoEjemplo()));

        mockMvc.perform(get("/api/v2/vehiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modeloVehiculo").value("Corolla"));
    }

    @Test
    void obtenerPorId_noEncontrado_retorna404() throws Exception {
        when(vehiculoService.obtenerVehiculoPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v2/vehiculos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearVehiculo_retorna201() throws Exception {
        when(vehiculoService.guardarVehiculoModel(any())).thenReturn(vehiculoEjemplo());

        mockMvc.perform(post("/api/v2/vehiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehiculoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marcaVehiculo").value("Toyota"));
    }

    @Test
    void listarPorMarca_retornaLista() throws Exception {
        when(vehiculoService.obtenerVehiculoPorMarca("Toyota")).thenReturn(List.of(vehiculoEjemplo()));

        mockMvc.perform(get("/api/v2/vehiculos/marca/Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marcaVehiculo").value("Toyota"));
    }

    @Test
    void eliminarVehiculo_encontrado_retorna200() throws Exception {
        when(vehiculoService.eliminarVehiculoPorId(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v2/vehiculos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarVehiculo_noEncontrado_retorna404() throws Exception {
        when(vehiculoService.eliminarVehiculoPorId(99)).thenReturn(false);

        mockMvc.perform(delete("/api/v2/vehiculos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarVehiculo_encontrado_retorna200() throws Exception {
        VehiculoModel actualizado = vehiculoEjemplo();
        actualizado.setModeloVehiculo("Yaris");

        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(vehiculoEjemplo()));
        when(vehiculoRepository.save(any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/v2/vehiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modeloVehiculo").value("Yaris"));
    }

    @Test
    void actualizarVehiculo_noEncontrado_retorna404() throws Exception {
        when(vehiculoRepository.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v2/vehiculos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehiculoEjemplo())))
                .andExpect(status().isNotFound());
    }
}
