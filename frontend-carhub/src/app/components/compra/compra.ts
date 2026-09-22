import { Component, OnInit } from '@angular/core';
import { Usuario } from '../../models/usuario/usuario';
import { Vehiculo } from '../../models/vehiculo/vehiculo';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { VehiculoService } from '../../services/vehiculo-service';
import { environment } from '../../environments/environments';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DecimalPipe, DatePipe } from '@angular/common';

@Component({
  selector: 'app-compra',
  imports: [CommonModule, FormsModule, DecimalPipe, DatePipe],
  templateUrl: './compra.html',
  styleUrl: './compra.css',
})
export class Compra implements OnInit{
  usuarioLogueado: Usuario | null = null;
  vehiculo: Vehiculo | null = null;
  metodoPago: string = 'Credito';
  facturaGenerada: any = null;
  cargando: boolean = false;
  idAutomotriz: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private vehiculoService: VehiculoService
  ) {}

  ngOnInit(): void {
    const usuarioGuardado = localStorage.getItem('userLogged');
    if (usuarioGuardado) {
      this.usuarioLogueado = JSON.parse(usuarioGuardado);
    }

    this.route.queryParams.subscribe(params => {
      this.idAutomotriz = params['idAutomotriz'] ? Number(params['idAutomotriz']) : 0;
      const idVehiculo = params['idVehiculo'];
      if (idVehiculo) {
        this.vehiculoService.obtenerVehiculoPorId(Number(idVehiculo)).subscribe({
          next: (data) => this.vehiculo = data,
          error: (err) => console.error('Error al cargar vehiculo:', err)
        });
      }
    });
  }

  confirmarCompra(): void {
    if (!this.usuarioLogueado || !this.vehiculo) return;

    this.cargando = true;

    const url = `${environment.urlCompra}?idUsuario=${
      this.usuarioLogueado.idUser}&idVehiculo=${
        this.vehiculo.idVehiculo}&idAutomotriz=${
          this.idAutomotriz}&metodoPago=${this.metodoPago}`;

    this.http.post(url, {}).subscribe({
      next: (factura) => {
        this.facturaGenerada = factura;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al generar factura:', err);
        this.cargando = false;
      }
    });
  }

  volver(): void {
    this.router.navigate(['/dashboard']);
  }
}
