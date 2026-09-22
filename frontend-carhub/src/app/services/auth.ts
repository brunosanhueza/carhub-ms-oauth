import { Injectable } from '@angular/core';
import { PublicClientApplication, Configuration, AuthenticationResult, AccountInfo } from '@azure/msal-browser';
import { environment } from '../environments/environments';
import { RolUsuario, Usuario } from '../models/usuario/usuario';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private msalInstance!: PublicClientApplication;
  private isInitialized = false;

  constructor() {
    this.initMsal();
  }

  private async initMsal(): Promise<void> {
    try {
      if (typeof window === 'undefined') return;

      if (!window.crypto || !window.crypto.subtle) {
        console.warn('Web Crypto (crypto.subtle) no está disponible porque la página no está en un contexto seguro (HTTPS o localhost). Para probar en HTTP por IP, activa chrome://flags/#unsafely-treat-insecure-origin-as-secure');
        return;
      }

      const msalConfig: Configuration = {
        auth: {
          clientId: environment.azure.clientId,
          authority: `https://login.microsoftonline.com/${environment.azure.tenantId}`,
          redirectUri: environment.azure.redirectUri,
        },
        cache: {
          cacheLocation: 'localStorage',
        },
      };

      this.msalInstance = new PublicClientApplication(msalConfig);
      await this.msalInstance.initialize();
      await this.msalInstance.handleRedirectPromise();
      this.isInitialized = true;
    } catch (err) {
      console.warn('MSAL requiere HTTPS o localhost para activar Web Crypto. Detalles:', err);
    }
  }

  public async ensureInitialized(): Promise<void> {
    if (!this.isInitialized) {
      await this.initMsal();
    }
  }

  /**
   * Inicia sesión con Microsoft Entra ID (Azure AD) mediante ventana emergente (Popup)
   */
  public async loginMicrosoft(): Promise<Usuario> {
    await this.ensureInitialized();
    if (!this.isInitialized || !this.msalInstance) {
      if (typeof window !== 'undefined' && (!window.crypto || !window.crypto.subtle)) {
        throw new Error('El navegador bloqueó la criptografía de Microsoft (crypto.subtle) porque estás navegando por HTTP con IP. Activa en Chrome/Edge el flag chrome://flags/#unsafely-treat-insecure-origin-as-secure para tu URL o usa localhost.');
      }
      throw new Error('MSAL no se pudo inicializar en este navegador.');
    }

    try {
      // Limpiar estados de interacción colgados de sesiones anteriores si existen
      try {
        Object.keys(sessionStorage).forEach(key => {
          if (key.toLowerCase().includes('interaction')) sessionStorage.removeItem(key);
        });
        Object.keys(localStorage).forEach(key => {
          if (key.toLowerCase().includes('interaction')) localStorage.removeItem(key);
        });
      } catch (e) {}

      const loginRequest = {
        scopes: ['user.read', 'openid', 'profile', 'email'],
        prompt: 'select_account',
      };

      const result: AuthenticationResult = await this.msalInstance.loginPopup(loginRequest);

      // Extraer datos del usuario y claims
      const account: AccountInfo = result.account;
      const idTokenClaims = result.idTokenClaims as Record<string, any> | undefined;

      // Detectar roles desde los claims del token ('roles')
      let userRole: RolUsuario = 'USUARIO';
      if (idTokenClaims?.['roles'] && Array.isArray(idTokenClaims['roles'])) {
        const roles = idTokenClaims['roles'].map((r: string) => r.toUpperCase());
        if (roles.includes('SUPERADMIN') || roles.includes('ADMIN')) {
          userRole = 'SUPERADMIN';
        } else if (roles.includes('SUBADMIN')) {
          userRole = 'SUBADMIN';
        }
      }

      let usuario: Usuario = {
        idUser: 0,
        nameUser: account.name || account.username,
        emailUser: account.username,
        passwordUser: 'AZURE_AD_OAUTH2',
        phoneUser: 'N/A',
        rolUsuario: userRole,
      };

      const token = result.idToken;
      localStorage.setItem('azureToken', token);

      // Sincronizar Just-In-Time (JIT) con la base de datos MySQL
      try {
        const syncResponse = await fetch(`${environment.urlUsuarios}/sync-oauth`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(usuario)
        });

        if (syncResponse.ok) {
          const usuarioDb = await syncResponse.json();
          if (usuarioDb && usuarioDb.idUser) {
            usuario = usuarioDb;
            console.log('Usuario sincronizado con MySQL exitosamente con ID:', usuario.idUser);
          }
        }
      } catch (syncErr) {
        console.warn('Sincronización con backend diferida (backend aún no disponible):', syncErr);
      }

      localStorage.setItem('userLogged', JSON.stringify(usuario));
      return usuario;
    } catch (error) {
      console.error('Error al iniciar sesión con Microsoft Entra ID:', error);
      throw error;
    }
  }

  /**
   * Obtiene el token JWT actual de Azure AD para adjuntarlo a las peticiones
   */
  public async getAccessToken(): Promise<string | null> {
    const cachedToken = localStorage.getItem('azureToken');
    if (!cachedToken) return null;

    try {
      await this.ensureInitialized();
      const accounts = this.msalInstance.getAllAccounts();
      if (accounts.length > 0) {
        const silentRequest = {
          scopes: ['user.read', 'openid', 'profile'],
          account: accounts[0],
        };
        const response = await this.msalInstance.acquireTokenSilent(silentRequest);
        if (response.idToken) {
          localStorage.setItem('azureToken', response.idToken);
          return response.idToken;
        }
      }
    } catch (e) {
      console.warn('No se pudo renovar silenciosamente el token, usando token cacheado:', e);
    }

    return cachedToken;
  }

  /**
   * Cierre de sesión
   */
  public async logout(): Promise<void> {
    localStorage.removeItem('userLogged');
    localStorage.removeItem('azureToken');

    try {
      await this.ensureInitialized();
      const accounts = this.msalInstance.getAllAccounts();
      if (accounts.length > 0) {
        await this.msalInstance.logoutPopup({
          account: accounts[0],
        });
      }
    } catch (e) {
      console.warn('Error en logout de MSAL:', e);
    }
  }

  public isLoggedIn(): boolean {
    return localStorage.getItem('userLogged') !== null;
  }

  public getLoggedUser(): Usuario | null {
    const userStr = localStorage.getItem('userLogged');
    return userStr ? JSON.parse(userStr) : null;
  }
}
