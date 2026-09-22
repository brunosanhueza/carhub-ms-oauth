package com.zacky.reclamos.controller;

import com.zacky.reclamos.model.entity.ReclamoModel;
import com.zacky.reclamos.services.ReclamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
@RequestMapping("/reclamos")
public class ReclamoController {

    @Autowired
    private ReclamoService reclamoService;

    @PostMapping
    public ResponseEntity<ReclamoModel> crearReclamo(
            @RequestParam Integer idUsuario,
            @RequestParam String tipoReclamo,
            @RequestParam String descripcionReclamo) {

        ReclamoModel reclamo = reclamoService.crearReclamo(idUsuario, tipoReclamo, descripcionReclamo);
        return ResponseEntity.ok(reclamo);
    }

    @GetMapping
    public ResponseEntity<List<ReclamoModel>> obtenerReclamos() {
        return ResponseEntity.ok(reclamoService.obtenerReclamos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReclamoModel> obtenerPorId(@PathVariable Integer id) {
        return reclamoService.obtenerReclamoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReclamoModel>> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(reclamoService.obtenerReclamosPorUsuario(idUsuario));
    }
}
