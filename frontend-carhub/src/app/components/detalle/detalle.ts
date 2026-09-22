import { Component, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Vehiculo } from '../../models/vehiculo/vehiculo';
import { ActivatedRoute, Router } from '@angular/router';
import { VehiculoService } from '../../services/vehiculo-service';
import { Usuario } from '../../models/usuario/usuario';

@Component({
  selector: 'app-detalle',
  imports: [DecimalPipe],
  templateUrl: './detalle.html',
  styleUrl: './detalle.css',
})
export class Detalle implements OnInit{
  vehiculo: Vehiculo | null = null;
  usuarioLogueado: Usuario | null = null;
  idAutomotriz: number = 0;
  marcaActual: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private vehiculoService: VehiculoService
  ) {}

  ngOnInit(): void {
    const usuarioGuardado = localStorage.getItem('userLogged');
    if (usuarioGuardado) {
      this.usuarioLogueado = JSON.parse(usuarioGuardado);
    }

    this.idAutomotriz = Number(this.route.snapshot.queryParamMap.get('idAutomotriz')) || 0;
    this.marcaActual = this.route.snapshot.queryParamMap.get('marca') || '';

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.vehiculoService.obtenerVehiculoPorId(Number(id)).subscribe({
        next: (data) => this.vehiculo = data,
        error: (err) => console.error('Error al cargar vehiculo:', err)
      });
    }
  }

  comprar(): void {
    if (!this.usuarioLogueado) {
      alert('Debes iniciar sesión para comprar');
      this.router.navigate(['/login']);
      return;
    }
    this.router.navigate(['/compra'], {
      queryParams: {
        idUsuario: this.usuarioLogueado.idUser,
        idVehiculo: this.vehiculo?.idVehiculo,
        idAutomotriz: this.idAutomotriz
      }
    });
  }

  volver(): void {
    this.router.navigate(['/catalogo'], {
      queryParams: { marca: this.marcaActual, idAutomotriz: this.idAutomotriz }
    });
  }
}
