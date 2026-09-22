import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RolUsuario } from '../models/usuario/usuario';

export const roleGuard = (rolesPermitidos: RolUsuario[]): CanActivateFn => () => {
  const router = inject(Router);
  const userStr = localStorage.getItem('userLogged');

  if (!userStr) {
    router.navigate(['/login']);
    return false;
  }

  const user = JSON.parse(userStr);
  if (rolesPermitidos.includes(user.rolUsuario)) return true;

  router.navigate(['/dashboard']);
  return false;
};
