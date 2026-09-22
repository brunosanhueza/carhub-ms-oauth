package com.loquendodev.vehiculos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.loquendodev.vehiculos.models.entity.VehiculoModel;
import com.loquendodev.vehiculos.repository.VehiculoRepository;
import com.loquendodev.vehiculos.services.VehiculoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v2")

//localhost se mantiene pese a estar dentro de una instancia con Nginx :)
//ya no se mantiene xd
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})  
public class VehiculoController {

    private final VehiculoRepository vehiculoRepository;

    VehiculoController(VehiculoRepository vehiculoRepository){
        this.vehiculoRepository = vehiculoRepository;
    }

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/vehiculos")
    public List<VehiculoModel> listarVehiculos(){
        return vehiculoService.obtenerVehiculos();
    }

    @GetMapping("/vehiculos/{id}")
    public ResponseEntity<VehiculoModel> obtenerVehiculoPorId(@PathVariable("id") Integer id){
        return vehiculoService.obtenerVehiculoPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/vehiculos/{id}")
    public ResponseEntity<String> eliminarVehiculoPorId(@PathVariable("id") Integer id){
        boolean vehiculoEliminado = vehiculoService.eliminarVehiculoPorId(id);
        if (vehiculoEliminado){
            return ResponseEntity.ok("Vehiculo eliminado");

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("el vehiculo que se desea eliminar\ncon su id: "+id+" no ha sido encontrado");
        }
    }

    @PutMapping("/vehiculos/{id}")
    public ResponseEntity<VehiculoModel> actualizarVehiculo(@PathVariable("id") Integer id, @RequestBody VehiculoModel veh)
    {
        return vehiculoRepository.findById(id)
        .map(vehiculoCreado -> {
            vehiculoCreado.setMarcaVehiculo(veh.getMarcaVehiculo());
            vehiculoCreado.setModeloVehiculo(veh.getModeloVehiculo());
            vehiculoCreado.setAnioVehiculo(veh.getAnioVehiculo());
            vehiculoCreado.setTipoBencinaVehiculo(veh.getTipoBencinaVehiculo());
            vehiculoCreado.setTipoPropulsionVehiculo(veh.getTipoPropulsionVehiculo());

            VehiculoModel vehiculoActualizado = vehiculoRepository.save(vehiculoCreado);
            return ResponseEntity.ok(vehiculoActualizado);

        }).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PostMapping("/vehiculos")
    public ResponseEntity<VehiculoModel> crearVehiculo(@RequestBody VehiculoModel veh){
        VehiculoModel vehiculoCreado = vehiculoService.guardarVehiculoModel(veh);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoCreado);
    }

    @GetMapping("/vehiculos/marca/{marca}")
    public List<VehiculoModel> obtenerVehiculoPorMarca(@PathVariable String marca){
        return vehiculoService.obtenerVehiculoPorMarca(marca);
    }
    }


