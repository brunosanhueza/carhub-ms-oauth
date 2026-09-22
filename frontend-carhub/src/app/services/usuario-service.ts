import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario/usuario';
import { environment } from '../environments/environments';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  constructor(private http: HttpClient) {}

  crearUsuario(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(environment.urlUsuarios, usuario);
  }


  loginUsuario(email: string, password: string): Observable<Usuario>{
    return this.http.post<Usuario>(`${environment.urlUsuarios}/login`,{
      emailUser: email,
      passwordUser: password
    });
  }

  syncOAuthUsuario(usuario: Partial<Usuario>): Observable<Usuario> {
    return this.http.post<Usuario>(`${environment.urlUsuarios}/sync-oauth`, usuario);
  }
}