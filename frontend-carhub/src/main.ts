import 'zone.js';

import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

// Si esta ventana contiene la respuesta de autenticación de Microsoft (#code= o ?code=),
// no levantamos Angular para evitar duplicar la app y permitir que el script de index.html la cierre.
const hash = typeof window !== 'undefined' ? window.location.hash : '';
const search = typeof window !== 'undefined' ? window.location.search : '';
const isAuthPopup = hash.includes('code=') || search.includes('code=') || hash.includes('error=') || search.includes('error=');

if (!isAuthPopup) {
  bootstrapApplication(App, appConfig)
    .catch((err) => console.error(err));
}
