import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Interceptor HTTP para inyectar automáticamente el JWT (Bearer Token)
 * recibido desde Microsoft Entra ID (Azure AD) en todas las llamadas al backend.
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // No inyectar token en peticiones de login o registro
  if (req.url.includes('/api/v1/usuarios') && req.method === 'POST') {
    return next(req);
  }

  const token = localStorage.getItem('azureToken');

  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(authReq);
  }

  return next(req);
};
