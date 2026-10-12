import { useEffect, useState } from "react";
import { mensajeDeError, solicitarEndpoint } from "../servicios/api.js";
import { GRUPOS_ADMINISTRATIVOS } from "../endpoints/grupos.js";

function valoresIniciales(parametros = []) {
  return Object.fromEntries(parametros.map(({ nombre, valorInicial }) => [nombre, valorInicial || ""]));
}

export default function FormularioSolicitud({
  endpoint,
  sesion,
  alRecibirRespuesta,
  alActualizarApiKey,
}) {

  const [valoresRuta, setValoresRuta] = useState({});
  const [valoresConsulta, setValoresConsulta] = useState({});
  const [cuerpo, setCuerpo] = useState("");
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState("");
  const [aviso, setAviso] = useState("");

  useEffect(() => {
    setValoresRuta(valoresIniciales(endpoint.parametrosRuta));
    setValoresConsulta(valoresIniciales(endpoint.parametrosConsulta));
    setCuerpo(endpoint.ejemploCuerpo ? JSON.stringify(endpoint.ejemploCuerpo, null, 2) : "");
    setError("");
    setAviso("");
    alRecibirRespuesta(null);
  }, [endpoint, alRecibirRespuesta]);

  const rutaVisible = endpoint.ruta.replace(/:([A-Za-z0-9_]+)/g, (_, nombre) => {
    const valor = valoresRuta[nombre]?.trim();
    return valor ? encodeURIComponent(valor) : `:${nombre}`;
  });

  async function enviarSolicitud(event) {
    event.preventDefault();

    if (cargando) {
      return;
    }

    if (endpoint.parametrosRuta?.some(({ nombre }) => !valoresRuta[nombre]?.trim())) {
      setError("ERROR: Complete los datos de la ruta antes de enviar la consulta.");
      return;
    }

    let cuerpoProcesado;

    if (endpoint.ejemploCuerpo) {

      try {
        cuerpoProcesado = JSON.parse(cuerpo);

      } catch {
        setError("ERROR: El contenido de la solicitud no es un JSON válido.");
        return;
      }
    }

    if (endpoint.metodo === "DELETE" &&
        !window.confirm(`ADVERTENCIA: ¿Confirma que desea eliminar? ${endpoint.titulo}`)) {
      return;
    }

    setCargando(true);
    setError("");
    setAviso("");
    alRecibirRespuesta(null);

    try {
      const respuesta = await solicitarEndpoint(endpoint, {
        valoresRuta,
        valoresConsulta,
        cuerpo: cuerpoProcesado,
        sesion,
      });

      alRecibirRespuesta(respuesta);

      if (!respuesta.correcto) {
        setError(mensajeDeError(respuesta.contenido, respuesta.estado));
        return;
      }

      if (endpoint.actualizaApiKey) {

        const nuevaApiKey = respuesta.contenido?.data?.apikey
          || respuesta.contenido?.data?.apiKey
          || respuesta.contenido?.apikey
          || respuesta.contenido?.apiKey;

        if (nuevaApiKey) {
          alActualizarApiKey(nuevaApiKey);
          setAviso("EXITO: La nueva API key se guardó automáticamente para esta sesión.");
        }
      }

    } catch (errorDeSolicitud) {

      setError(errorDeSolicitud instanceof TypeError
        ? "ERROR: No fue posible conectar con la API. Compruebe que el servidor esté activo."
        : errorDeSolicitud.message);

    } finally {
      setCargando(false);
    }
  }

  return (
    <section className="request-card">

      <div className="request-heading">

        <div>
          <p className="eyebrow">{endpoint.acceso === "publico" ? "ENDPOINT PÚBLICO" : "ENDPOINT PROTEGIDO"}</p>
          <h2>{endpoint.titulo}</h2>
          <p>{endpoint.descripcion}</p>
        </div>

        <span className={`method-badge method-${endpoint.metodo.toLowerCase()}`}>{endpoint.metodo}</span>
      </div>

      <div className="request-url">
        <span>{endpoint.metodo}</span>
        <code>{rutaVisible}</code>
      </div>

      <form className="request-form" onSubmit={enviarSolicitud}>

        {endpoint.parametrosRuta?.map((parametro) => (
          <label className="field" key={parametro.nombre}>
            <span>{parametro.etiqueta}</span>
            <input
              required
              autoComplete="off"
              placeholder={`Ej. ${parametro.ejemplo}`}
              value={valoresRuta[parametro.nombre] || ""}
              onChange={(event) => setValoresRuta((valores) => ({
                ...valores,
                [parametro.nombre]: event.target.value,
              }))}
            />
          </label>
        ))}

        {endpoint.parametrosConsulta?.map((parametro) => (
          <label className="field" key={parametro.nombre}>
            <span>{parametro.etiqueta} <small>Parámetro de consulta</small></span>
            <input
              autoComplete="off"
              placeholder={`Ej. ${parametro.ejemplo}`}
              value={valoresConsulta[parametro.nombre] || ""}
              onChange={(event) => setValoresConsulta((valores) => ({
                ...valores,
                [parametro.nombre]: event.target.value,
              }))}
            />
          </label>
        ))}

        {endpoint.ejemploCuerpo && (
          <label className="field json-field">
            <span>Contenido de la solicitud <small>JSON</small></span>
            <textarea
              spellCheck="false"
              value={cuerpo}
              onChange={(event) => setCuerpo(event.target.value)}
              rows={Math.min(15, Math.max(6, cuerpo.split("\n").length))}
            />
          </label>
        )}

        {endpoint.acceso === "privado" && endpoint.grupo === "rutas" && (
          <p className="permission-note">
            Este servicio requiere iniciar sesión.
          </p>
        )}

        {endpoint.acceso === "privado" && GRUPOS_ADMINISTRATIVOS.has(endpoint.grupo) && (
          <p className="permission-note">
            Este servicio administrativo puede requerir un usuario administrador.
          </p>
        )}

        <button className="search-button request-button" type="submit" disabled={cargando}>
          {cargando ? <><span className="spinner" /> Enviando solicitud</> : "Enviar solicitud"}
          {!cargando && <span aria-hidden="true">→</span>}
        </button>
      </form>

      {error && <div className="error-message" role="alert"><span aria-hidden="true">!</span>{error}</div>}
      {aviso && <div className="success-message" role="status"><span aria-hidden="true">✓</span>{aviso}</div>}

    </section>
  );
}
