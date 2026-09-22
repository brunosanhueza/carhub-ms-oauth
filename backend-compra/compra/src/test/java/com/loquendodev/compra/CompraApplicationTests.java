package com.loquendodev.compra;

import com.loquendodev.compra.controller.compraController;
import com.loquendodev.compra.model.entity.facturaModel;
import com.loquendodev.compra.services.compraService;
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

@WebMvcTest(compraController.class)
class CompraApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private compraService compraServ;

    private facturaModel facturaEjemplo() {
        facturaModel f = new facturaModel();
        f.setNro_factura(1);
        f.setIdUser(5);
        f.setNameUser("Juan Pérez");
        f.setMarcaVehiculo("Toyota");
        f.setModeloVehiculo("Corolla");
        f.setMetodo_pago_factura("Crédito");
        f.setSubtotal_factura(15000000f);
        return f;
    }

    @Test
    void listarCompras_retornaOk() throws Exception {
        when(compraServ.obtenerCompras()).thenReturn(List.of(facturaEjemplo()));

        mockMvc.perform(get("/api/v4/compra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nameUser").value("Juan Pérez"));
    }

    @Test
    void obtenerCompraPorId_encontrado_retorna200() throws Exception {
        when(compraServ.obtenerCompraPorId(1)).thenReturn(Optional.of(facturaEjemplo()));

        mockMvc.perform(get("/api/v4/compra/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marcaVehiculo").value("Toyota"));
    }

    @Test
    void obtenerCompraPorId_noEncontrado_retorna404() throws Exception {
        when(compraServ.obtenerCompraPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v4/compra/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerComprasPorUsuario_retornaLista() throws Exception {
        when(compraServ.obtenerComprasPorUsuario(5)).thenReturn(List.of(facturaEjemplo()));

        mockMvc.perform(get("/api/v4/compra/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUser").value(5));
    }

    @Test
    void emitirCompra_retorna201() throws Exception {
        when(compraServ.emitirCompra(5, 1, 2, "Crédito")).thenReturn(facturaEjemplo());

        mockMvc.perform(post("/api/v4/compra")
                        .param("idUsuario", "5")
                        .param("idVehiculo", "1")
                        .param("idAutomotriz", "2")
                        .param("metodoPago", "Crédito"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.modeloVehiculo").value("Corolla"));
    }
}
