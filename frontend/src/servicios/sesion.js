export const CLAVE_SESION = "visor-rutas.sesion";

export function leerSesion() {

  const guardada = sessionStorage.getItem(CLAVE_SESION);

  if (!guardada) {
    return null;
  }

  try {
    const sesion = JSON.parse(guardada);

    if (sesion.login && sesion.token && sesion.apiKey) {
      return sesion;
    }

  } catch {
    sessionStorage.removeItem(CLAVE_SESION);
    return null;
  }

  sessionStorage.removeItem(CLAVE_SESION);
  return null;
}

export function guardarSesion(sesion) {
  sessionStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
}

export function eliminarSesion() {
  sessionStorage.removeItem(CLAVE_SESION);
}
