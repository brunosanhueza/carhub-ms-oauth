import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Pieza } from '../models/pieza/pieza';
import { environment } from '../environments/environments';

@Injectable({ providedIn: 'root' })
export class PiezaService {

  private readonly API = environment.urlPiezas;

  constructor(private http: HttpClient) {}

  listarPiezas(): Observable<Pieza[]> {
    return this.http.get<Pieza[]>(this.API);
  }

  listarPorMarca(marca: string): Observable<Pieza[]> {
    return this.http.get<Pieza[]>(`${this.API}/marca/${marca}`);
  }

  registrarPieza(params: string): Observable<Pieza> {
    return this.http.post<Pieza>(`${this.API}?${params}`, {});
  }

  eliminarPieza(id: number): Observable<string> {
    return this.http.delete(`${this.API}/${id}`, { responseType: 'text' });
  }
}
