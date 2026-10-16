package com.loquendodev.carhub.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.loquendodev.carhub.models.dto.UsuarioDto;
import com.loquendodev.carhub.models.entity.Usuario;
import com.loquendodev.carhub.repository.UsuarioRepository;

@Service

public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired(required = false)
    private com.loquendodev.carhub.publisher.UsuarioEventPublisher usuarioEventPublisher;

//metodo para obtener a todos los clientes 
public List<Usuario> obtenerUsuarios(){ 
    return usuarioRepository.findAll(); 
}

//metodo para guardar a un cliente 
public Usuario guardarUsuario(Usuario usr){ 
    Usuario usuarioGuardado = usuarioRepository.save(usr); 
    if (usuarioEventPublisher != null) {
        usuarioEventPublisher.publicarEventoUsuario(usuarioGuardado, "REGISTRO", "Usuario registrado en Carhub");
    }
    return usuarioGuardado;
} 
//metodo para encontrar a un cliente mediante su id 
public Optional<Usuario> obtenerUsuarioPorId(Integer id){ 
return usuarioRepository.findById(id); 
}

//metodo para eliminar por Id 

public boolean eliminarUsuarioPorId (Integer id){ 
if (usuarioRepository.existsById(id)){ 
usuarioRepository.deleteById(id); 
return true; 
} else {return false;} 

}

//actualizar usuario
public Optional<Usuario> actualizarUsuario(Integer id, Usuario usuarioActualizado) {
    return usuarioRepository.findById(id).map(usuarioExistente -> {
        usuarioExistente.setNameUser(usuarioActualizado.getNameUser());
        usuarioExistente.setEmailUser(usuarioActualizado.getEmailUser());
        usuarioExistente.setPasswordUser(usuarioActualizado.getPasswordUser());
        usuarioExistente.setPhoneUser(usuarioActualizado.getPhoneUser());
        return usuarioRepository.save(usuarioExistente);
    });
}


@GetMapping("/dto/usuario/{id}")
public ResponseEntity<UsuarioDto> obtenerUsuarioDto(@PathVariable Integer id) {
    
    Usuario usuario = usuarioRepository.findById(id).orElseThrow();
    
    UsuarioDto udto = new UsuarioDto();
    
    udto.setIdUser(usuario.getIdUser()); 
    
    udto.setNameUser(usuario.getNameUser()); 
    udto.setEmailUser(usuario.getEmailUser());
    udto.setPhoneUser(usuario.getPhoneUser());
    udto.setIdUser(usuario.getIdUser());
    
    return ResponseEntity.ok(udto);
}

//eto pal login
public Optional<Usuario> login(String email, String password) {
    return usuarioRepository.findByEmailUserAndPasswordUser(email, password);
}

// Sincronizacion Just-In-Time (JIT) de usuarios autenticados con Microsoft Entra ID
public Usuario syncUsuarioOAuth(Usuario usr) {
    String email = usr.getEmailUser();
    if (email == null || email.trim().isEmpty()) {
        throw new IllegalArgumentException("El correo del usuario no puede estar vacio");
    }

    Optional<Usuario> existente = usuarioRepository.findByEmailUser(email.trim());
    if (existente.isPresent()) {
        Usuario usuarioEncontrado = existente.get();
        // Actualizar nombre y rol si vinieron informados
        if (usr.getNameUser() != null && !usr.getNameUser().trim().isEmpty()) {
            usuarioEncontrado.setNameUser(usr.getNameUser().trim());
        }
        if (usr.getRolUsuario() != null) {
            usuarioEncontrado.setRolUsuario(usr.getRolUsuario());
        }
        return usuarioRepository.save(usuarioEncontrado);
    } else {
        // Usuario nuevo: generar placeholders unicos para campos con restriccion UNIQUE
        String safeHash = Integer.toHexString(email.trim().toLowerCase().hashCode());
        usr.setEmailUser(email.trim());
        usr.setPasswordUser("AZURE_OAUTH2_" + safeHash);
        if (usr.getPhoneUser() == null || usr.getPhoneUser().trim().isEmpty() || usr.getPhoneUser().equals("N/A")) {
            usr.setPhoneUser("NA_" + safeHash);
        }
        if (usr.getRolUsuario() == null) {
            usr.setRolUsuario(com.loquendodev.carhub.models.entity.RolUsuario.USUARIO);
        }
            Usuario usuarioGuardado = usuarioRepository.save(usr);
            if (usuarioEventPublisher != null) {
                usuarioEventPublisher.publicarEventoUsuario(usuarioGuardado, "OAUTH_SYNC", "Usuario sincronizado mediante Microsoft Entra ID");
            }
            return usuarioGuardado;
        }
    }
}
