import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Vehiculo } from '../../models/vehiculo/vehiculo';
import { VehiculoService } from '../../services/vehiculo-service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-vehiculo-component',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './vehiculo-component.html',
  styleUrl: './vehiculo-component.css',
})

export class VehiculoComponent implements OnInit {

  VehiculoForm!: FormGroup;
  vehiculos: Vehiculo[] = [];
  filtroMarca: string = "";

  msgOK = "";
  msgERR = "";

  constructor(
    private fb: FormBuilder,
    private vehiculoService: VehiculoService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  volver(): void {
    this.router.navigate(['/dashboard']);
  }

  ngOnInit(): void {
      this.VehiculoForm = this.fb.group({
        marcaVehiculo: ['', Validators.required],
        modeloVehiculo: ['', Validators.required],
        anioVehiculo: ['', Validators.required],
        tipoBencinaVehiculo: ['', Validators.required],
        tipoTransmisionVehiculo: ['', Validators.required],
        tipoPropulsionVehiculo: ['', Validators.required],
        precioVehiculo: ['', Validators.required]
      });
    
      this.route.queryParams.subscribe(params => {
        this.filtroMarca = params['marca'] || '';
        if (this.filtroMarca){
          this.listarVehiculosPorMarca(this.filtroMarca);
        } else {        
          this.listarVehiculos();


        }
      });
  }

  listarVehiculos(): void {
    this.vehiculoService.obtenerVehiculos().subscribe({
      next: (data) => this.vehiculos = data,
      error: (err) => console.error('Error al listar vehiculos: ',err)
    });
  }

  listarVehiculosPorMarca(marca: string): void {
    this.vehiculoService.obtenerVehiculosPorMarca(marca).subscribe({
      next: (data) => this.vehiculos = data,
      error: (err) => console.error('Error al filtrar vehículos:', err)
    });
  }


  onSubmit(): void {
    if (this.VehiculoForm.valid){
      const nuevoVehiculo: Vehiculo = this.VehiculoForm.value;
      this.vehiculoService.crearVehiculo(nuevoVehiculo).subscribe({
        next: (res) => {
          console.log('el vehiculo se ha creado uwu: ',res);
          this.msgOK= 'el vehiculo ha sido creado OK';
          this.msgERR= 'no hay ningun error yeei';
          this.VehiculoForm.reset();
          this.listarVehiculos();
        },
        error: (err) => {
          console.error('error al crear el vehiculo: ',err);
          this.msgERR= 'Error al crear el vehiculo pipipi';
          this.msgOK='';
        }
      });
    }
  }
}
