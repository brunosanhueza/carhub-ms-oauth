export const environment = {
  production: false,
  urlUsuarios:  `/api/v1/usuarios`,
  urlVehiculos: `/api/v2/vehiculos`,
  urlAutomotriz:`/api/v3/automotriz`,
  urlCompra:    `/api/v4/compra`,
  urlReclamos:  `/reclamos`,
  urlPiezas:    `/api/v7/piezas`,

  // Configuración de Microsoft Entra ID (Azure AD) - OAuth2.0
  azure: {
    clientId: '7a535d86-829f-4c52-a4f7-ddecbeadae2b',
    tenantId: 'common',
    redirectUri: typeof window !== 'undefined' ? window.location.origin : 'http://localhost:4200'
  }
};
