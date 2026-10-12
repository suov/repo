import { useState } from "react";
import EncabezadoPortal from "./EncabezadoPortal.jsx";
import { autenticar } from "../servicios/api.js";
import { guardarSesion } from "../servicios/sesion.js";

export default function FormularioInicioSesion({ alVolver, alIniciarSesion }) {

  const [usuario, setUsuario] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [apiKey, setApiKey] = useState("");
  const [mostrarApiKey, setMostrarApiKey] = useState(false);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState("");

  async function enviarFormulario(event) {

    event.preventDefault();

    if (!apiKey.trim()) {
      setError("ERROR: Ingrese la API key asociada a su usuario.");
      return;
    }

    setCargando(true);
    setError("");

    try {
      const token = await autenticar({ usuario, contrasena });
      const sesion = { login: usuario.trim(), token, apiKey: apiKey.trim() };
      guardarSesion(sesion);
      alIniciarSesion(sesion);

    } catch (errorDeSolicitud) {

      setError(errorDeSolicitud instanceof TypeError
        ? "ERROR: No fue posible conectar con la API. Compruebe que el servidor esté activo."
        : errorDeSolicitud.message);

    } finally {
      setCargando(false);
    }
  }

  return (
    <main className="welcome-shell login-shell">

      <header className="portal-topbar">
        <a className="brand" href="/" aria-label="Ir al inicio">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-users-group">
            <path stroke="none" d="M0 0h24v24H0z" fill="none" />
            <path d="M10 13a2 2 0 1 0 4 0a2 2 0 0 0 -4 0" />
            <path d="M8 21v-1a2 2 0 0 1 2 -2h4a2 2 0 0 1 2 2v1" />
            <path d="M15 5a2 2 0 1 0 4 0a2 2 0 0 0 -4 0" />
            <path d="M17 10h2a2 2 0 0 1 2 2v1" />
            <path d="M5 5a2 2 0 1 0 4 0a2 2 0 0 0 -4 0" />
            <path d="M3 13v-1a2 2 0 0 1 2 -2h2" />
          </svg>
          <span className="brand-name">Rutas del mejor Cipas<span>+</span></span>
        </a>
        <button className="text-button" type="button" onClick={alVolver}>Volver</button>
      </header>

      <section className="login-card">
        <span className="access-icon" aria-hidden="true">🛅</span>
        <p className="eyebrow">ACCESO COMPLETO</p>
        <h1>Iniciar sesión</h1>
        <p className="page-subtitle">Ingrese sus credenciales para consultar las rutas y los demás servicios.</p>

        <form className="login-form" onSubmit={enviarFormulario}>
          <label className="field">
            <span>Usuario</span>
            <input required autoComplete="username" value={usuario} onChange={(event) => setUsuario(event.target.value)} />
          </label>

          <label className="field">
            <span>Contraseña</span>
            <input required type="password" autoComplete="current-password" value={contrasena} onChange={(event) => setContrasena(event.target.value)} />
          </label>

          <label className="field">
            <span>API key del usuario</span>
            <div className="password-control">
              <input
                required
                type={mostrarApiKey ? "text" : "password"}
                autoComplete="off"
                value={apiKey}
                onChange={(event) => setApiKey(event.target.value)}
              />
              <button type="button" onClick={() => setMostrarApiKey((visible) => !visible)}>
                {mostrarApiKey ? "Ocultar" : "Mostrar"}
              </button>
            </div>
          </label>

          {error && <div className="error-message" role="alert"><span aria-hidden="true">!</span>{error}</div>}

          <button className="search-button request-button" type="submit" disabled={cargando}>
            {cargando ? <><span className="spinner" /> Validando acceso</> : "Iniciar sesión"}
          </button>

        </form>
      </section>

      <footer className="portal-footer">El mejor Cipas+ · Autenticación</footer>

    </main>
  );
}
