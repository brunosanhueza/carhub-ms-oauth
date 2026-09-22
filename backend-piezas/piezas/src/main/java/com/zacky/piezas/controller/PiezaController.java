package com.zacky.piezas.controller;

import com.zacky.piezas.model.entity.PiezaModel;
import com.zacky.piezas.services.PiezaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
@RequestMapping("/api/v7/piezas")
public class PiezaController {

    @Autowired
    private PiezaService piezaService;

    @PostMapping
    public ResponseEntity<PiezaModel> registrarPieza(
            @RequestParam Integer idVehiculo,
            @RequestParam Integer idUsuario,
            @RequestParam String tipoPieza,
            @RequestParam String nombrePieza,
            @RequestParam String materialPieza,
            @RequestParam Float precioPieza,
            @RequestParam String tipoMarcaPieza,
            @RequestParam Integer stockPieza,
            @RequestParam(required = false) String descripcionPieza) {

        PiezaModel pieza = piezaService.registrarPieza(
                idVehiculo, idUsuario, tipoPieza, nombrePieza,
                materialPieza, precioPieza, tipoMarcaPieza, stockPieza, descripcionPieza);
        return ResponseEntity.ok(pieza);
    }

    @GetMapping
    public ResponseEntity<List<PiezaModel>> listarPiezas() {
        return ResponseEntity.ok(piezaService.listarPiezas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PiezaModel> obtenerPorId(@PathVariable Integer id) {
        return piezaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<PiezaModel>> listarPorMarca(@PathVariable String marca) {
        return ResponseEntity.ok(piezaService.listarPorMarca(marca));
    }

    @GetMapping("/modelo/{modelo}")
    public ResponseEntity<List<PiezaModel>> listarPorModelo(@PathVariable String modelo) {
        return ResponseEntity.ok(piezaService.listarPorModelo(modelo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPieza(@PathVariable Integer id) {
        boolean eliminada = piezaService.eliminarPieza(id);
        if (eliminada) return ResponseEntity.ok("Pieza eliminada");
        return ResponseEntity.notFound().build();
    }
}
