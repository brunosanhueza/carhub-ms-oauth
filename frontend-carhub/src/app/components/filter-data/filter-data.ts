import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { automotriz } from '../../models/automotriz/automotriz';

@Component({
  selector: 'app-filter-data',
  imports: [CommonModule, FormsModule],
  templateUrl: './filter-data.html',
  styleUrl: './filter-data.css',
})
export class FilterData {

  @Input() set listaAutomotriz(autos: automotriz[]) {
    if (autos && autos.length > 0) {
      this.marcas = [...new Set(autos.map(a => a.nombreAutomotriz))].sort() as string[];
    }
  }

  @Output() cambioDeFiltro = new EventEmitter<string>();

  marcaSel: string = '';
  marcas: string[] = [];

  notificarFiltros(): void {
    this.cambioDeFiltro.emit(this.marcaSel);
  }

  limpiarFiltros(): void {
    this.marcaSel = '';
    this.notificarFiltros();
  }
}