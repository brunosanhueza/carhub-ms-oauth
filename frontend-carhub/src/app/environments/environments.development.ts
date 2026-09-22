export const environment = {
  production: false,
  urlUsuarios:  `https://mv6xudxbi7.execute-api.us-east-1.amazonaws.com/api/v1/usuarios`,
  urlVehiculos: `https://mv6xudxbi7.execute-api.us-east-1.amazonaws.com/api/v2/vehiculos`,
  urlAutomotriz:`https://mv6xudxbi7.execute-api.us-east-1.amazonaws.com/api/v3/automotriz`,
  urlCompra:    `https://mv6xudxbi7.execute-api.us-east-1.amazonaws.com/api/v4/compra`,
  urlReclamos:  `https://mv6xudxbi7.execute-api.us-east-1.amazonaws.com/reclamos`,
  urlPiezas:    `https://mv6xudxbi7.execute-api.us-east-1.amazonaws.com/api/v7/piezas`,

  // Configuración de Microsoft Entra ID (Azure AD) - OAuth2.0
  azure: {
    clientId: '7a535d86-829f-4c52-a4f7-ddecbeadae2b',
    tenantId: 'common',
    redirectUri: typeof window !== 'undefined' ? window.location.origin : 'http://localhost:4200'
  }
};
