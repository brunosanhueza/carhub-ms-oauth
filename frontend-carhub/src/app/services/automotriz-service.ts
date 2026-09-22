import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { automotriz } from '../models/automotriz/automotriz';
import { environment } from '../environments/environments';

@Injectable({
  providedIn: 'root',
})
export class AutomotrizService {
  constructor(private http: HttpClient){}


  obtenerAutomotrices(): Observable<automotriz[]>{
    return this.http.get<automotriz[]>(environment.urlAutomotriz);

  }
}
