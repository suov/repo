import { useEffect, useState } from "react";
import { mensajeDeError, solicitarEndpoint } from "../servicios/api.js";
import { GRUPOS_ADMINISTRATIVOS } from "../endpoints/grupos.js";
import FormularioDatos from "./FormularioDatos.jsx";

function valoresIniciales(parametros = []) {
  return Object.fromEntries(parametros.map(({ nombre, valorInicial }) => [nombre, valorInicial || ""]));
}

function formularioVacio(objeto) {
  if (Array.isArray(objeto)) {
    return objeto.map(formularioVacio);
  }

  if (objeto && typeof objeto === "object") {
    return Object.fromEntries(
      Object.entries(objeto).map(([nombre, valor]) => [nombre, formularioVacio(valor)]),
    );
  }

  return "";
}

export default function FormularioSolicitud({
  endpoint,
  sesion,
  precarga,
  alRecibirRespuesta,
  alActualizarApiKey,
  alCancelarEdicion,
}) {

  const [valoresRuta, setValoresRuta] = useState({});
  const [valoresConsulta, setValoresConsulta] = useState({});
  const [valoresCuerpo, setValoresCuerpo] = useState({});
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState("");
  const [aviso, setAviso] = useState("");

  useEffect(() => {
    const precargaActiva = precarga?.endpointId === endpoint.id ? precarga : null;
    setValoresRuta({
      ...valoresIniciales(endpoint.parametrosRuta),
      ...precargaActiva?.valoresRuta,
    });
    setValoresConsulta(valoresIniciales(endpoint.parametrosConsulta));
    setValoresCuerpo(precargaActiva?.cuerpo || formularioVacio(endpoint.ejemploCuerpo));
    setError("");
    setAviso("");
    alRecibirRespuesta(null);
  }, [endpoint, precarga, alRecibirRespuesta]);

  const estaEditando = precarga?.endpointId === endpoint.id;

  async function enviarSolicitud(event) {
    event.preventDefault();

    if (cargando) {
      return;
    }

    if (endpoint.parametrosRuta?.some(({ nombre }) => !valoresRuta[nombre]?.trim())) {
      setError("ERROR: Complete los datos de la ruta antes de enviar la consulta.");
      return;
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
        cuerpo: valoresCuerpo,
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
          <p className="eyebrow">{estaEditando ? "EDITAR REGISTRO" : "FORMULARIO"}</p>
          <h2>{estaEditando ? "Editar información" : endpoint.titulo}</h2>
          <p>{endpoint.descripcion}</p>
        </div>

        <span className={`method-badge method-${endpoint.metodo.toLowerCase()}`}>
          {endpoint.metodo === "GET" ? "CONSULTA" : endpoint.metodo === "POST" ? "CREAR" : endpoint.metodo === "PUT" ? "EDITAR" : "ELIMINAR"}
        </span>
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
          <div className="data-form">
            <p className="form-section-title">
              {estaEditando ? "Datos del registro" : "Complete los datos"}
            </p>
            <FormularioDatos valores={valoresCuerpo} alCambiar={setValoresCuerpo} />
          </div>
        )}

        {endpoint.acceso === "privado" && endpoint.grupo === "rutas" && (
          <p className="permission-note">
            Estas acciones requieren iniciar sesión.
          </p>
        )}

        {endpoint.acceso === "privado" && GRUPOS_ADMINISTRATIVOS.has(endpoint.grupo) && (
          <p className="permission-note">
            Para administrar estos datos, el usuario debe tener tipo de persona A.
          </p>
        )}

        <div className="form-actions">
          <button className="search-button request-button" type="submit" disabled={cargando}>
            {cargando ? <><span className="spinner" /> Procesando</> : endpoint.metodo === "GET" ? "Consultar" : endpoint.metodo === "POST" ? "Guardar" : endpoint.metodo === "PUT" ? "Guardar cambios" : "Eliminar"}
            {!cargando && <span aria-hidden="true">→</span>}
          </button>
          {estaEditando && (
            <button className="secondary-button" type="button" onClick={alCancelarEdicion}>
              Cancelar edición
            </button>
          )}
        </div>
      </form>

      {error && <div className="error-message" role="alert"><span aria-hidden="true">!</span>{error}</div>}
      {aviso && <div className="success-message" role="status"><span aria-hidden="true">✓</span>{aviso}</div>}

    </section>
  );
}
