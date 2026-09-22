import { Routes } from '@angular/router';
import { Register } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard';
import { Login } from './components/login/login';
import { About } from './components/about/about';
import { VehiculoComponent } from './components/vehiculo-component/vehiculo-component';
import { Catalogo } from './components/catalogo/catalogo';
import { Detalle } from './components/detalle/detalle';
import { Compra } from './components/compra/compra';
import { Reparaciones } from './components/reparaciones/reparaciones';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
    { path: 'register', component: Register },
    { path: 'about',    component: About },
    { path: 'login',    component: Login },

    // Rutas para cualquier usuario logueado
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'catalogo',  component: Catalogo,           canActivate: [authGuard] },
    { path: 'detalle/:id', component: Detalle,          canActivate: [authGuard] },
    { path: 'compra',    component: Compra,             canActivate: [authGuard] },

    // Rutas solo para SUBADMIN y SUPERADMIN
    { path: 'vehiculos',     component: VehiculoComponent, canActivate: [roleGuard(['SUBADMIN', 'SUPERADMIN'])] },
    { path: 'reparaciones',  component: Reparaciones,      canActivate: [roleGuard(['SUBADMIN', 'SUPERADMIN'])] },

    { path: '', redirectTo: 'register', pathMatch: 'full' },
    { path: '**', redirectTo: 'register' },
];
