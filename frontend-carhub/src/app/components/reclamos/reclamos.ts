import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environments';

@Component({
  selector: 'app-reclamos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reclamos.html',
  styleUrls: ['./reclamos.css']
})
export class Reclamos {
  mostrarModal: boolean = false;
  enviando: boolean = false;
  enviado: boolean = false;

  tipoReclamo: string = '';
  descripcionReclamo: string = '';
  idUsuario: number = 0;

  tiposReclamo: string[] = [
    'Factura no recibida',
    'Pago incompleto',
    'Despacho tardío',
    'Vehículo en mal estado',
    'Otro'
  ];

  private readonly API = environment.urlReclamos;

  constructor(private http: HttpClient) {}

  abrirModal(): void {
    const userStr = localStorage.getItem('userLogged');
    if (userStr) {
      this.idUsuario = JSON.parse(userStr).idUser;
    }
    this.mostrarModal = true;
    this.enviado = false;
    this.tipoReclamo = '';
    this.descripcionReclamo = '';
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  enviarReclamo(): void {
    if (!this.tipoReclamo || !this.descripcionReclamo) return;

    this.enviando = true;
    const params = `?idUsuario=${this.idUsuario}&tipoReclamo=${encodeURIComponent(this.tipoReclamo)}&descripcionReclamo=${encodeURIComponent(this.descripcionReclamo)}`;

    this.http.post(this.API + params, {}).subscribe({
      next: () => {
        this.enviando = false;
        this.enviado = true;
      },
      error: () => {
        this.enviando = false;
        alert('Error al enviar el reclamo. Intenta de nuevo.');
      }
    });
  }
}
