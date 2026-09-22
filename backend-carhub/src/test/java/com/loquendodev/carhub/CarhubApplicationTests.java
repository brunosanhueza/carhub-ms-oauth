package com.loquendodev.carhub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loquendodev.carhub.controllers.usuarioController;
import com.loquendodev.carhub.models.entity.RolUsuario;
import com.loquendodev.carhub.models.entity.Usuario;
import com.loquendodev.carhub.repository.UsuarioRepository;
import com.loquendodev.carhub.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(usuarioController.class)
class CarhubApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioEjemplo() {
        Usuario u = new Usuario();
        u.setIdUser(1);
        u.setNameUser("Juan Pérez");
        u.setEmailUser("juan@gmail.com");
        u.setPasswordUser("pass123");
        u.setPhoneUser("912345678");
        u.setRolUsuario(RolUsuario.USUARIO);
        return u;
    }

    @Test
    void listarUsuarios_retornaOk() throws Exception {
        when(usuarioService.obtenerUsuarios()).thenReturn(List.of(usuarioEjemplo()));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nameUser").value("Juan Pérez"));
    }

    @Test
    void obtenerUsuarioPorId_encontrado_retorna200() throws Exception {
        when(usuarioService.obtenerUsuarioPorId(1)).thenReturn(Optional.of(usuarioEjemplo()));

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailUser").value("juan@gmail.com"));
    }

    @Test
    void obtenerUsuarioPorId_noEncontrado_retorna404() throws Exception {
        when(usuarioService.obtenerUsuarioPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearUsuario_retorna201() throws Exception {
        when(usuarioService.guardarUsuario(any())).thenReturn(usuarioEjemplo());

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUser").value(1));
    }

    @Test
    void login_credencialesCorrectas_retorna200() throws Exception {
        when(usuarioService.login("juan@gmail.com", "pass123"))
                .thenReturn(Optional.of(usuarioEjemplo()));

        Map<String, String> creds = Map.of("emailUser", "juan@gmail.com", "passwordUser", "pass123");

        mockMvc.perform(post("/api/v1/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creds)))
                .andExpect(status().isOk());
    }

    @Test
    void login_credencialesIncorrectas_retorna401() throws Exception {
        when(usuarioService.login(any(), any())).thenReturn(Optional.empty());

        Map<String, String> creds = Map.of("emailUser", "x@gmail.com", "passwordUser", "mal");

        mockMvc.perform(post("/api/v1/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creds)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminarUsuario_encontrado_retorna200() throws Exception {
        when(usuarioService.eliminarUsuarioPorId(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarUsuario_noEncontrado_retorna404() throws Exception {
        when(usuarioService.eliminarUsuarioPorId(99)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarUsuario_encontrado_retorna200() throws Exception {
        Usuario actualizado = usuarioEjemplo();
        actualizado.setNameUser("Pedro García");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioEjemplo()));
        when(usuarioRepository.save(any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameUser").value("Pedro García"));
    }

    @Test
    void actualizarUsuario_noEncontrado_retorna404() throws Exception {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/usuarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioEjemplo())))
                .andExpect(status().isNotFound());
    }
}
