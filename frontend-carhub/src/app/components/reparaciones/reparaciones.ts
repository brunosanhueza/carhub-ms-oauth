import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Pieza } from '../../models/pieza/pieza';
import { Vehiculo } from '../../models/vehiculo/vehiculo';
import { PiezaService } from '../../services/pieza-service';
import { VehiculoService } from '../../services/vehiculo-service';

@Component({
  selector: 'app-reparaciones',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DecimalPipe],
  templateUrl: './reparaciones.html',
  styleUrls: ['./reparaciones.css']
})
export class Reparaciones implements OnInit {

  vistaActiva: 'catalogo' | 'agregar' = 'catalogo';

  piezas: Pieza[] = [];
  piezasFiltradas: Pieza[] = [];
  vehiculos: Vehiculo[] = [];

  filtroBusqueda: string = '';
  piezaForm!: FormGroup;
  enviando: boolean = false;
  idUsuario: number = 0;

  tiposPieza: string[] = [
    'Parachoques', 'Espejo retrovisor', 'Motor', 'Frenos',
    'Amortiguador', 'Radiador', 'Alternador', 'Batería',
    'Neumático', 'Llanta', 'Filtro de aceite', 'Otro'
  ];

  materiales: string[] = [
    'Plástico ABS', 'Fibra de carbono', 'Acero', 'Aluminio',
    'Caucho', 'Vidrio', 'Polipropileno', 'Otro'
  ];

  constructor(
    private piezaService: PiezaService,
    private vehiculoService: VehiculoService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    const userStr = localStorage.getItem('userLogged');
    if (userStr) this.idUsuario = JSON.parse(userStr).idUser;

    this.cargarPiezas();
    this.cargarVehiculos();
    this.initForm();
  }

  initForm(): void {
    this.piezaForm = this.fb.group({
      idVehiculo:     ['', Validators.required],
      tipoPieza:      ['', Validators.required],
      nombrePieza:    ['', Validators.required],
      materialPieza:  ['', Validators.required],
      precioPieza:    ['', [Validators.required, Validators.min(0)]],
      tipoMarcaPieza: ['ORIGINAL', Validators.required],
      stockPieza:     ['', [Validators.required, Validators.min(1)]],
      descripcionPieza: ['']
    });
  }

  cargarPiezas(): void {
    this.piezaService.listarPiezas().subscribe({
      next: (data) => {
        this.piezas = data;
        this.piezasFiltradas = data;
      },
      error: (err) => console.error('Error al cargar piezas:', err)
    });
  }

  cargarVehiculos(): void {
    this.vehiculoService.obtenerVehiculos().subscribe({
      next: (data) => this.vehiculos = data,
      error: (err) => console.error('Error al cargar vehículos:', err)
    });
  }

  filtrar(event: Event): void {
    const texto = (event.target as HTMLInputElement).value.toLowerCase();
    this.piezasFiltradas = this.piezas.filter(p =>
      p.marcaVehiculo.toLowerCase().includes(texto) ||
      p.modeloVehiculo.toLowerCase().includes(texto) ||
      p.tipoPieza.toLowerCase().includes(texto) ||
      p.nombrePieza.toLowerCase().includes(texto)
    );
  }

  registrar(): void {
    if (this.piezaForm.invalid) return;

    this.enviando = true;
    const v = this.piezaForm.value;
    const params = [
      `idVehiculo=${v.idVehiculo}`,
      `idUsuario=${this.idUsuario}`,
      `tipoPieza=${encodeURIComponent(v.tipoPieza)}`,
      `nombrePieza=${encodeURIComponent(v.nombrePieza)}`,
      `materialPieza=${encodeURIComponent(v.materialPieza)}`,
      `precioPieza=${v.precioPieza}`,
      `tipoMarcaPieza=${encodeURIComponent(v.tipoMarcaPieza)}`,
      `stockPieza=${v.stockPieza}`,
      `descripcionPieza=${encodeURIComponent(v.descripcionPieza || '')}`
    ].join('&');

    this.piezaService.registrarPieza(params).subscribe({
      next: () => {
        this.enviando = false;
        alert('¡Pieza registrada correctamente!');
        this.piezaForm.reset({ tipoMarcaPieza: 'ORIGINAL' });
        this.vistaActiva = 'catalogo';
        this.cargarPiezas();
      },
      error: () => {
        this.enviando = false;
        alert('Error al registrar la pieza. Verifica los datos.');
      }
    });
  }

  volver(): void {
    this.router.navigate(['/dashboard']);
  }
}
