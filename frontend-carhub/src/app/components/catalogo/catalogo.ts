import { Component, OnInit } from '@angular/core';
import { Vehiculo } from '../../models/vehiculo/vehiculo';
import { ActivatedRoute, Router } from '@angular/router';
import { VehiculoService } from '../../services/vehiculo-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-catalogo',
  imports: [CommonModule],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css',
})
export class Catalogo implements OnInit{
  vehiculos: Vehiculo[] = [];
  marcaActual: string = '';
  idAutomotrizActual: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private vehiculoService: VehiculoService
  ){}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.marcaActual = params['marca'] || '';
      this.idAutomotrizActual = params['idAutomotriz'] ? Number(params['idAutomotriz']) : 0;
      if (this.marcaActual) {
        this.vehiculoService.obtenerVehiculosPorMarca(this.marcaActual).subscribe({
          next: (data) => this.vehiculos = data,
          error: (err) => console.error("error al cargar los autos: ", err)
        });
      }
    });
  }

  verDetalle(vehiculo: Vehiculo): void {
    this.router.navigate(['/detalle', vehiculo.idVehiculo], {
      queryParams: { idAutomotriz: this.idAutomotrizActual, marca: this.marcaActual }
    });
  }

  volver(): void {
    this.router.navigate(['/dashboard']);
  }

}


