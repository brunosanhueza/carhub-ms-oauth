import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Vehiculo } from "../models/vehiculo/vehiculo";
import { environment } from "../environments/environments";



@Injectable({providedIn: 'root'})
export class VehiculoService {
    constructor(private http: HttpClient){}


    obtenerVehiculos(): Observable<Vehiculo[]>{
        return this.http.get<Vehiculo[]>(environment.urlVehiculos);
    }

    crearVehiculo(vehiculo: Vehiculo): Observable<Vehiculo>{
        return this.http.post<Vehiculo>(environment.urlVehiculos, vehiculo);
    }

    obtenerVehiculosPorMarca(marca: string): Observable<Vehiculo[]> {
        const marcaEncoded = encodeURIComponent(encodeURIComponent(marca));
        return this.http.get<Vehiculo[]>(`${environment.urlVehiculos}/marca/${marcaEncoded}`);



}

    obtenerVehiculoPorId(id: number): Observable<Vehiculo> {
    return this.http.get<Vehiculo>(`${environment.urlVehiculos}/${id}`);
    
    }
}