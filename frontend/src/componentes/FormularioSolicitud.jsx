import { useEffect, useState } from "react";
import { mensajeDeError, solicitarEndpoint } from "../servicios/api.js";
import { ENDPOINTES_PRIVADOS } from "../endpoints/privados.js";
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

function opcionesParametro(endpoint, nombre) {
  if (endpoint.id === "vehiculos-por-estado-documento" &&
      nombre === "estadoDocumento") {
    return [
      { valor: "HABILITADO", etiqueta: "Habilitado" },
      { valor: "VENCIDO", etiqueta: "Vencido" },
      { valor: "EN_VERIFICACION", etiqueta: "En verificación" },
    ];
  }

  if (nombre !== "tipoVehiculo") {
    return null;
  }

  if (endpoint.id === "documentos-por-tipo-vehiculo") {
    return [
      { valor: "A", etiqueta: "A - Automóvil" },
      { valor: "M", etiqueta: "M - Motocicleta" },
      { valor: "AM", etiqueta: "AM - Ambos" },
    ];
  }

  return [
    { valor: "AUTOMOVIL", etiqueta: "Automóvil" },
    { valor: "MOTOCICLETA", etiqueta: "Motocicleta" },
  ];
}

function fechaLocalActual() {
  const hoy = new Date();
  const mes = String(hoy.getMonth() + 1).padStart(2, "0");
  const dia = String(hoy.getDate()).padStart(2, "0");
  return `${hoy.getFullYear()}-${mes}-${dia}`;
}

function registrosDe(respuesta) {
  if (Array.isArray(respuesta.contenido)) {
    return respuesta.contenido;
  }

  if (Array.isArray(respuesta.contenido?.data)) {
    return respuesta.contenido.data;
  }

  throw new Error("La API no devolvió una lista válida.");
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
  const [documentosDisponibles, setDocumentosDisponibles] = useState([]);
  const [cargandoDocumentos, setCargandoDocumentos] = useState(false);
  const [errorDocumentos, setErrorDocumentos] = useState("");
  const [vehiculosDisponibles, setVehiculosDisponibles] = useState([]);
  const [conductoresDisponibles, setConductoresDisponibles] = useState([]);
  const [cargandoOpcionesAsociacion, setCargandoOpcionesAsociacion] = useState(false);
  const [errorOpcionesAsociacion, setErrorOpcionesAsociacion] = useState("");

  /* Precarga de información */
  useEffect(() => {
    const precargaActiva = precarga?.endpointId === endpoint.id ? precarga : null;
    setValoresRuta({
      ...valoresIniciales(endpoint.parametrosRuta),
      ...precargaActiva?.valoresRuta,
    });
    setValoresConsulta(valoresIniciales(endpoint.parametrosConsulta));
    const cuerpoInicial = precargaActiva?.cuerpo
      ? { ...precargaActiva.cuerpo }
      : formularioVacio(endpoint.ejemploCuerpo);
    if (endpoint.id === "asociaciones-crear" && !precargaActiva?.cuerpo?.fechaAsociacion) {
      cuerpoInicial.fechaAsociacion = fechaLocalActual();
    }
    setValoresCuerpo(cuerpoInicial);
    setError("");
    setAviso("");
    alRecibirRespuesta(null);
  }, [endpoint, precarga, alRecibirRespuesta]);

  useEffect(() => {
    let vigente = true;
    setVehiculosDisponibles([]);
    setConductoresDisponibles([]);
    setErrorOpcionesAsociacion("");

    if (endpoint.id !== "asociaciones-crear") {
      setCargandoOpcionesAsociacion(false);
      return () => {
        vigente = false;
      };
    }

    if (!sesion) {
      setCargandoOpcionesAsociacion(false);
      setErrorOpcionesAsociacion("Inicie sesión para cargar vehículos y conductores registrados.");
      return () => {
        vigente = false;
      };
    }

    const endpointVehiculos = ENDPOINTES_PRIVADOS.find(({ id }) => id === "vehiculos-listar");
    const endpointPersonas = ENDPOINTES_PRIVADOS.find(({ id }) => id === "personas-listar");
    if (!endpointVehiculos || !endpointPersonas) {
      setCargandoOpcionesAsociacion(false);
      setErrorOpcionesAsociacion("No se encontraron los servicios para cargar vehículos y personas.");
      return () => {
        vigente = false;
      };
    }

    setCargandoOpcionesAsociacion(true);
    Promise.all([
      solicitarEndpoint(endpointVehiculos, {
        valoresRuta: {},
        valoresConsulta: {},
        cuerpo: {},
        sesion,
      }),
      solicitarEndpoint(endpointPersonas, {
        valoresRuta: {},
        valoresConsulta: {},
        cuerpo: {},
        sesion,
      }),
    ])
      .then(([respuestaVehiculos, respuestaPersonas]) => {
        if (!respuestaVehiculos.correcto) {
          throw new Error(`No fue posible cargar los vehículos: ${
            mensajeDeError(respuestaVehiculos.contenido, respuestaVehiculos.estado)
          }`);
        }
        if (!respuestaPersonas.correcto) {
          throw new Error(`No fue posible cargar las personas: ${
            mensajeDeError(respuestaPersonas.contenido, respuestaPersonas.estado)
          }`);
        }

        const vehiculos = registrosDe(respuestaVehiculos);
        const personas = registrosDe(respuestaPersonas);

        if (vigente) {
          setVehiculosDisponibles(vehiculos
            .filter((vehiculo) => vehiculo.id !== undefined && vehiculo.placa)
            .map((vehiculo) => ({ id: vehiculo.id, etiqueta: vehiculo.placa })));
          setConductoresDisponibles(personas
            .filter((persona) => persona.id !== undefined && persona.tipoPersona === "C")
            .map((persona) => ({
              id: persona.id,
              etiqueta: [persona.nombre, persona.apellido].filter(Boolean).join(" "),
            })));
        }
      })
      .catch((errorDeSolicitud) => {
        if (vigente) {
          setErrorOpcionesAsociacion(errorDeSolicitud instanceof TypeError
            ? "No fue posible conectar con la API para cargar vehículos y conductores."
            : errorDeSolicitud.message);
        }
      })
      .finally(() => {
        if (vigente) {
          setCargandoOpcionesAsociacion(false);
        }
      });

    return () => {
      vigente = false;
    };
  }, [endpoint.id, sesion]);

  /* Valida que la sesión no haya expirado */
  useEffect(() => {
    let vigente = true;

    setDocumentosDisponibles([]);
    setErrorDocumentos("");

    const esFormularioVehiculo = endpoint.id === "vehiculo-crear"
      || endpoint.id === "vehiculo-actualizar"
      || endpoint.id === "vehiculo-agregar-documentos";
    const esBusquedaVehiculoPorDocumento = endpoint.id === "vehiculos-por-documento";

    if (!esFormularioVehiculo && !esBusquedaVehiculoPorDocumento) {
      setCargandoDocumentos(false);
      return () => {
        vigente = false;
      };
    }

    if (!sesion) {
      setCargandoDocumentos(false);
      setErrorDocumentos("Inicie sesión para cargar los tipos de documento registrados.");
      return () => {
        vigente = false;
      };
    }

    const endpointDocumentos = ENDPOINTES_PRIVADOS.find(
      ({ id }) => id === "documentos-listar",
    );

    if (!endpointDocumentos) {
      setCargandoDocumentos(false);
      setErrorDocumentos("No se encontró el servicio para consultar los documentos registrados.");
      return () => {
        vigente = false;
      };
    }

    setCargandoDocumentos(true);

    solicitarEndpoint(endpointDocumentos, {
      valoresRuta: {},
      valoresConsulta: {},
      cuerpo: {},
      sesion,
    })
      .then((respuesta) => {
        if (!respuesta.correcto) {
          throw new Error(mensajeDeError(respuesta.contenido, respuesta.estado));
        }

        if (!Array.isArray(respuesta.contenido)) {
          throw new Error("La API no devolvió una lista válida de documentos.");
        }

        if (vigente) {
          setDocumentosDisponibles(respuesta.contenido);
        }
      })
      .catch((errorDeSolicitud) => {
        if (vigente) {
          setErrorDocumentos(errorDeSolicitud instanceof TypeError
            ? "No fue posible conectar con la API para consultar los documentos."
            : errorDeSolicitud.message);
        }
      })
      .finally(() => {
        if (vigente) {
          setCargandoDocumentos(false);
        }
      });

    return () => {
      vigente = false;
    };
  }, [endpoint.id, sesion]);

  const estaEditando = precarga?.endpointId === endpoint.id;

  async function enviarSolicitud(event) {
    event.preventDefault();

    if (cargando) {
      return;
    }

    if (endpoint.id === "vehiculos-por-documento" &&
        (cargandoDocumentos || errorDocumentos)) {
      setError(errorDocumentos || "Espere a que se carguen los documentos registrados.");
      return;
    }

    if (endpoint.id === "asociaciones-crear" &&
        (cargandoOpcionesAsociacion || errorOpcionesAsociacion)) {
      setError(errorOpcionesAsociacion || "Espere a que se carguen los vehículos y conductores.");
      return;
    }

    if (endpoint.parametrosRuta?.some(({ nombre }) => (
      !String(valoresRuta[nombre] ?? "").trim()
    ))) {
      setError("ERROR: Complete los datos de la ruta antes de enviar la consulta.");
      return;
    }

    if (endpoint.id === "vehiculo-crear" || endpoint.id === "vehiculo-actualizar") {
      if (cargandoDocumentos || errorDocumentos) {
        setError(errorDocumentos || "Espere a que se carguen los documentos registrados.");
        return;
      }

      const documentos = valoresCuerpo.documentos;
      if (endpoint.id === "vehiculo-crear" &&
          (!Array.isArray(documentos) || documentos.length === 0)) {
        setError("Agregue al menos un documento registrado para asociarlo al vehículo.");
        return;
      }

      if (Array.isArray(documentos) && documentos.some(({
        documentoId,
        archivoBase64,
        fechaExpedicion,
        fechaVencimiento,
      }) => (
        !documentoId || !archivoBase64 || !fechaExpedicion || !fechaVencimiento
      ))) {
        setError("Cada documento debe tener tipo registrado, archivo PDF, fecha de expedición y fecha de vencimiento.");
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
    let vehiculoActualizado = false;

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

      vehiculoActualizado = endpoint.id === "vehiculo-actualizar";

      if (endpoint.id === "vehiculo-actualizar" && valoresCuerpo.documentos?.length) {
        const endpointDocumentos = ENDPOINTES_PRIVADOS.find(
          ({ id }) => id === "vehiculo-agregar-documentos",
        );

        if (!endpointDocumentos) {
          setError("El vehículo se actualizó, pero no se encontró el servicio para guardar sus documentos.");
          return;
        }

        setAviso("Vehículo actualizado. Guardando documentos...");
        const respuestaDocumentos = await solicitarEndpoint(endpointDocumentos, {
          valoresRuta: { vehiculoId: valoresRuta.id },
          valoresConsulta: {},
          cuerpo: valoresCuerpo.documentos,
          sesion,
        });
        alRecibirRespuesta(respuestaDocumentos);

        if (!respuestaDocumentos.correcto) {
          setAviso("");
          setError(`El vehículo se actualizó, pero no fue posible guardar sus documentos: ${
            mensajeDeError(respuestaDocumentos.contenido, respuestaDocumentos.estado)
          }`);
          return;
        }

        setAviso("Vehículo y documentos guardados correctamente.");
        return;
      }

      setAviso(
        respuesta.contenido?.message
        || (endpoint.metodo === "PUT"
          ? "Cambios guardados correctamente."
          : "Solicitud completada correctamente."),
      );

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

      setAviso("");
      setError(vehiculoActualizado
        ? `El vehículo se actualizó, pero no fue posible confirmar el guardado de sus documentos. ${
          errorDeSolicitud instanceof TypeError
            ? "Compruebe que el servidor esté activo y vuelva a guardar los documentos."
            : errorDeSolicitud.message
        }`
        : errorDeSolicitud instanceof TypeError
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
            {endpoint.id === "vehiculos-por-documento" &&
            parametro.nombre === "codigoDocumento" ? (
              <>
                <select
                  required
                  disabled={cargandoDocumentos || Boolean(errorDocumentos) || documentosDisponibles.length === 0}
                  value={valoresRuta[parametro.nombre] || ""}
                  onChange={(event) => setValoresRuta((valores) => ({
                    ...valores,
                    [parametro.nombre]: event.target.value,
                  }))}
                >
                  <option value="">
                    {cargandoDocumentos
                      ? "Cargando documentos..."
                      : documentosDisponibles.length
                        ? "Seleccione un documento registrado"
                        : "No hay documentos registrados"}
                  </option>
                  {[...new Map(documentosDisponibles
                    .filter((documento) => documento.codigoDocumentoParametrizado)
                    .map((documento) => [
                      documento.codigoDocumentoParametrizado,
                      documento,
                    ])).values()]
                    .map((documento) => (
                      <option
                        key={documento.id}
                        value={documento.codigoDocumentoParametrizado}
                      >
                        {documento.codigoDocumentoParametrizado} - {documento.nombreDocumento}
                      </option>
                    ))}
                </select>
                {errorDocumentos && <small role="alert">{errorDocumentos}</small>}
              </>
            ) : opcionesParametro(endpoint, parametro.nombre) ? (
              <select
                required
                value={valoresRuta[parametro.nombre] || ""}
                onChange={(event) => setValoresRuta((valores) => ({
                  ...valores,
                  [parametro.nombre]: event.target.value,
                }))}
              >
                <option value="">Seleccione una opción</option>
                {opcionesParametro(endpoint, parametro.nombre).map(({ valor, etiqueta }) => (
                  <option key={valor} value={valor}>{etiqueta}</option>
                ))}
              </select>
            ) : (
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
            )}
          </label>
        ))}

        {endpoint.parametrosConsulta?.map((parametro) => (
          <label className="field" key={parametro.nombre}>
            <span>{parametro.etiqueta} <small>Parámetro de consulta</small></span>
            {opcionesParametro(endpoint, parametro.nombre) ? (
              <select
                value={valoresConsulta[parametro.nombre] || ""}
                onChange={(event) => setValoresConsulta((valores) => ({
                  ...valores,
                  [parametro.nombre]: event.target.value,
                }))}
              >
                <option value="">Seleccione una opción</option>
                {opcionesParametro(endpoint, parametro.nombre).map(({ valor, etiqueta }) => (
                  <option key={valor} value={valor}>{etiqueta}</option>
                ))}
              </select>
            ) : (
              <input
                autoComplete="off"
                placeholder={`Ej. ${parametro.ejemplo}`}
                value={valoresConsulta[parametro.nombre] || ""}
                onChange={(event) => setValoresConsulta((valores) => ({
                  ...valores,
                  [parametro.nombre]: event.target.value,
                }))}
              />
            )}
          </label>
        ))}

        {endpoint.ejemploCuerpo && (
          <div className="data-form">
            <p className="form-section-title">
              {estaEditando ? "Datos del registro" : "Complete los datos"}
            </p>
            {(endpoint.id === "vehiculo-crear"
              || endpoint.id === "vehiculo-actualizar"
              || endpoint.id === "vehiculo-agregar-documentos") && (
              <p className="form-help">
                {cargandoDocumentos
                  ? "Cargando tipos de documento registrados..."
                  : errorDocumentos
                    ? errorDocumentos
                    : documentosDisponibles.length === 0
                      ? "No hay tipos de documento registrados. Créelos primero desde Documentos."
                      : endpoint.id === "vehiculo-agregar-documentos"
                        ? "Seleccione un tipo documental ya creado (por ejemplo, SOAT); el identificador del vehículo se ingresa arriba."
                      : estaEditando
                        ? "Edite las fechas de vencimiento o reemplace el PDF. Los documentos se guardan después de actualizar el vehículo."
                        : "Seleccione documentos ya registrados y adjunte el PDF de cada uno."}
              </p>
            )}
            {endpoint.id === "asociaciones-crear" && (
              <p className="form-help">
                {cargandoOpcionesAsociacion
                  ? "Cargando vehículos y conductores..."
                  : errorOpcionesAsociacion
                    ? errorOpcionesAsociacion
                    : vehiculosDisponibles.length === 0
                        ? "No hay vehículos registrados disponibles."
                        : conductoresDisponibles.length === 0
                          ? "No hay personas de tipo conductor registradas."
                          : "Seleccione la placa y el conductor. La fecha de asociación inicia en hoy, pero puede editarla."}
              </p>
            )}
            <FormularioDatos
              valores={valoresCuerpo}
              alCambiar={setValoresCuerpo}
              opcionesDocumentos={documentosDisponibles}
              opcionesVehiculos={vehiculosDisponibles}
              opcionesConductores={conductoresDisponibles}
              esAsociacionConductorVehiculo={endpoint.id === "asociaciones-crear"}
            />
          </div>
        )}

        {endpoint.acceso === "privado" && endpoint.grupo === "rutas" && (
          <p className="permission-note">
            Estas acciones requieren iniciar sesión.
          </p>
        )}

        {endpoint.acceso === "privado" && GRUPOS_ADMINISTRATIVOS.has(endpoint.grupo) && (
          <p className="permission-note">
            Está en modo administrador
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
