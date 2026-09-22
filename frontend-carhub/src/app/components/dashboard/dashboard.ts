import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule, DecimalPipe, DatePipe } from '@angular/common';
import { FooterComponent } from "../footer-component/footer-component";
import { VehiculoService } from '../../services/vehiculo-service';
import { Vehiculo } from '../../models/vehiculo/vehiculo';
import { FilterData } from "../filter-data/filter-data";
import { FiltroVehiculos } from '../../models/filters/FiltroVehiculos';
import { automotriz } from '../../models/automotriz/automotriz';
import { AutomotrizService } from '../../services/automotriz-service';
import { Usuario } from '../../models/usuario/usuario';
import { Factura } from '../../models/factura/factura';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environments';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FooterComponent, FilterData, DecimalPipe, DatePipe],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
})
export class DashboardComponent implements OnInit {

  listarAutosFiltro: Vehiculo[] = [];
  vehiculos: Vehiculo[] = [];
  listarAutomotrizFiltro: automotriz[] = [];
  automotrices: automotriz[] = [];

  // Perfil
  mostrarDropdown: boolean = false;
  mostrarModalCuenta: boolean = false;
  mostrarModalFacturas: boolean = false;
  usuarioLogueado: Usuario | null = null;
  facturas: Factura[] = [];
  cargandoFacturas: boolean = false;
  inicialesUsuario: string = '';

  private readonly API_COMPRA = environment.urlCompra;

  constructor(
    private router: Router,
    private vehiculoService: VehiculoService,
    private automotrizService: AutomotrizService,
    private http: HttpClient
  ) {}

  @HostListener('document:click', ['$event'])
  cerrarDropdownFuera(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.perfil-container')) {
      this.mostrarDropdown = false;
    }
  }

  ngOnInit(): void {
    const userStr = localStorage.getItem('userLogged');
    if (userStr) {
      this.usuarioLogueado = JSON.parse(userStr);
      const nombre = this.usuarioLogueado?.nameUser ?? '';
      this.inicialesUsuario = nombre.split(' ').map((n: string) => n[0]).slice(0, 2).join('').toUpperCase();
    }

    this.automotrizService.obtenerAutomotrices().subscribe({
      next: (data) => {
        this.automotrices = data;
        this.listarAutomotrizFiltro = data;
      },
      error: (err) => console.error("error al crear la automotriz", err)
    });
  }
      


  aplicarFiltro(marca: string): void{
    if (!marca){
      this.automotrices = this.listarAutomotrizFiltro;

    } else {
      this.automotrices = this.listarAutomotrizFiltro.filter(
        a => a.nombreAutomotriz.toLowerCase() === marca.toLowerCase()
      );
    }
  }
  

  verCatalogo(idAutomotriz: number, nombreAutomotriz: string): void {
    this.router.navigate(['/catalogo'], {queryParams: {marca: nombreAutomotriz, idAutomotriz: idAutomotriz}});
  }

  irAVehiculos(): void {
    this.router.navigate(['/vehiculos']);
  }

  irAReparaciones(): void {
    this.router.navigate(['/reparaciones']);
  }

  toggleDropdown(): void {
    this.mostrarDropdown = !this.mostrarDropdown;
  }

  verMiCuenta(): void {
    this.mostrarDropdown = false;
    this.mostrarModalCuenta = true;
  }

  verMisFacturas(): void {
    this.mostrarDropdown = false;
    this.mostrarModalFacturas = true;
    if (this.facturas.length === 0 && this.usuarioLogueado?.idUser) {
      this.cargandoFacturas = true;
      this.http.get<Factura[]>(`${this.API_COMPRA}/usuario/${this.usuarioLogueado.idUser}`).subscribe({
        next: (data) => { this.facturas = data; this.cargandoFacturas = false; },
        error: () => { this.cargandoFacturas = false; }
      });
    }
  }

  logout(): void {
    localStorage.removeItem('userLogged');
    this.router.navigate(['/register']);
  }
}