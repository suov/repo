export const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || "").replace(/\/+$/, "");

export async function leerRespuesta(response) {

  const texto = await response.text();

  if (!texto) {
    return null;
  }

  try {
    return JSON.parse(texto);
  } catch {
    return texto;
  }
}

export function mensajeDeError(respuesta, estado) {

  if (estado === 401) {
    return "ERROR: La sesión no es válida o venció. Inicie sesión nuevamente.";
  }

  if (estado === 403) {
    const detalle = typeof respuesta === "string"
      ? respuesta.toLowerCase()
      : `${respuesta?.message || ""} ${respuesta?.error || ""}`.toLowerCase();

    if (detalle.includes("x-api-key") || detalle.includes("api key") || detalle.includes("apikey")) {
      return "La API key no coincide con la de este usuario. Revísela o vuelva a iniciar sesión con la clave actual.";
    }

    if (detalle.includes("access denied") || detalle.includes("acceso denegado") || detalle.includes("forbidden")) {
      return "La sesión fue reconocida, pero esta acción necesita un usuario administrador. En el sistema, su persona debe tener tipo A. Si ya lo tiene, cierre sesión y vuelva a entrar.";
    }

    return "El servidor rechazó la solicitud. Revise que la API key sea la de este usuario y que su persona tenga tipo A para acciones administrativas.";
  }

  return respuesta?.message || respuesta?.reason || respuesta?.error ||
    `ERROR: La solicitud no pudo completarse (código ${estado}).`;
}

export async function autenticar({ usuario, contrasena }) {

  const response = await fetch(`${API_BASE_URL}/authenticate`, {
    method: "POST",
    headers: { "Content-Type": "application/json", Accept: "application/json" },
    body: JSON.stringify({ username: usuario.trim(), password: contrasena }),
  });

  const respuesta = await leerRespuesta(response);

  if (!response.ok) {
    throw new Error(mensajeDeError(respuesta, response.status));
  }

  const token = respuesta?.token || respuesta?.jwttoken;

  if (typeof token !== "string" || !token) {
    throw new Error("ERROR: El servidor no devolvió un token de acceso válido.");
  }

  return token;
}

export async function solicitarEndpoint(endpoint, { valoresRuta, valoresConsulta, cuerpo, sesion }) {

  const ruta = endpoint.ruta.replace(/:([A-Za-z0-9_]+)/g, (_, nombre) => {
    const valor = String(valoresRuta[nombre] ?? "").trim();
    return valor ? encodeURIComponent(valor) : `:${nombre}`;
  });

  const consulta = new URLSearchParams();

  Object.entries(valoresConsulta).forEach(([nombre, valor]) => {
    if (valor.trim()) {
      consulta.set(nombre, valor.trim());
    }
  });

  const url = `${API_BASE_URL}${ruta}${consulta.size ? `?${consulta.toString()}` : ""}`;
  const headers = { Accept: "application/json" };

  if (endpoint.acceso === "privado" && sesion) {
    headers.Authorization = `Bearer ${sesion.token.replace(/^Bearer\s+/i, "")}`;
    headers["X-API-KEY"] = sesion.apiKey;
  }

  if (endpoint.ejemploCuerpo) {
    headers["Content-Type"] = "application/json";
  }

  const response = await fetch(url, {
    method: endpoint.metodo,
    headers,
    ...(endpoint.ejemploCuerpo ? { body: JSON.stringify(cuerpo) } : {}),
  });
  
  return {
    estado: response.status,
    correcto: response.ok,
    contenido: await leerRespuesta(response),
  };
}
