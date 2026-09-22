package com.zacky.automotriz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zacky.automotriz.models.entity.automotrizModel;
import com.zacky.automotriz.services.automotrizService;



@RestController
@RequestMapping("/api/v3/automotriz")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})

public class automotrizController {

    @Autowired
    private automotrizService automotrizSer;

    @GetMapping
    public List<automotrizModel> listarAutomotrices() {
        return automotrizSer.obtenerAutomotrices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<automotrizModel> obtenerAutomotrizPorId(@PathVariable Integer id) {
        return automotrizSer.obtenerAutomotrizPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<automotrizModel> crearAutomotriz(@RequestBody automotrizModel automotriz) {
        automotrizModel autoMotcreada = automotrizSer.guardarAutomotriz(automotriz);
        return ResponseEntity.status(HttpStatus.CREATED).body(autoMotcreada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<automotrizModel> modificarAutomotriz(@PathVariable Integer id, @RequestBody automotrizModel automotriz) {
        return automotrizSer.modificarAutomotriz(id, automotriz)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarAutomotriz(@PathVariable Integer id) {
        boolean eliminada = automotrizSer.eliminarAutomotrizPorId(id);
        if (eliminada) {
            return ResponseEntity.ok("La automotriz ha sido eliminada");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("la Automotriz con la id: " + id + " no ha sido encontrada");
        }
    }

}
