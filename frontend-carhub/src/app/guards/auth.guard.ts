import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = () => {
  const user = localStorage.getItem('userLogged');
  if (user) return true;
  inject(Router).navigate(['/login']);
  return false;
};
