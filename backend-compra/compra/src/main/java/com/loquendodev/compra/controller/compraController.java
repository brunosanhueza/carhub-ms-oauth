package com.loquendodev.compra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.loquendodev.compra.model.entity.facturaModel;
import com.loquendodev.compra.services.compraService;
import java.util.List;

@RestController
@RequestMapping("/api/v4/compra")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
public class compraController {

    @Autowired
    private compraService compraServ;

    @GetMapping
    public List<facturaModel> listarCompras() {
        return compraServ.obtenerCompras();
    }

    @GetMapping("/{id}")
    public ResponseEntity<facturaModel> obtenerCompraPorId(@PathVariable Integer id) {
        return compraServ.obtenerCompraPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<facturaModel>> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(compraServ.obtenerComprasPorUsuario(idUsuario));
    }

    @PostMapping
    public ResponseEntity<facturaModel> emitirCompra(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idVehiculo,
            @RequestParam Integer idAutomotriz,
            @RequestParam String metodoPago) {

        facturaModel compra = compraServ.emitirCompra(
            idUsuario, idVehiculo, idAutomotriz, metodoPago);
        return ResponseEntity.status(HttpStatus.CREATED).body(compra);
    }
}