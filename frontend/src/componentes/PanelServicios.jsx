import { useState } from "react";
import EncabezadoPortal from "./EncabezadoPortal.jsx";
import FormularioSolicitud from "./FormularioSolicitud.jsx";
import NavegacionServicios from "./NavegacionServicios.jsx";
import ResultadoSolicitud from "./ResultadoSolicitud.jsx";
import { guardarSesion } from "../servicios/sesion.js";
import {
  ENDPOINTES,
  ENDPOINTES_PUBLICOS,
  ENDPOINTES_PRIVADOS,
  GRUPOS_ENDPOINTES,
} from "../endpoints/index.js";

function datosParaEditar(ejemplo, registro) {
  if (Array.isArray(ejemplo)) {
    if (ejemplo.length === 0 || !Array.isArray(registro)) {
      return [];
    }

    return registro.map((item) => datosParaEditar(ejemplo[0], item));
  }

  if (ejemplo && typeof ejemplo === "object") {
    return Object.fromEntries(
      Object.entries(ejemplo).map(([nombre, valor]) => [
        nombre,
        registro?.[nombre] === undefined
          ? valor
          : datosParaEditar(valor, registro[nombre]),
      ]),
    );
  }

  return registro === undefined || registro === null ? ejemplo : registro;
}

export default function PanelServicios({
  sesion,
  publico = false,
  alCerrarSesion,
  alActualizarSesion,
  volverAlInicio,
}) {

  const endpointsVisibles = publico ? ENDPOINTES_PUBLICOS : ENDPOINTES;

  const gruposVisibles = GRUPOS_ENDPOINTES.filter((grupo) =>
    endpointsVisibles.some((endpoint) => endpoint.grupo === grupo.id),
  );

  const [grupoActivo, setGrupoActivo] = useState(publico ? "personas" : "rutas");
  const [endpointId, setEndpointId] = useState(publico ? "personas-total" : "ruta-por-codigo");
  const [respuesta, setRespuesta] = useState(null);
  const [precarga, setPrecarga] = useState(null);

  const grupoActual = gruposVisibles.find((grupo) => grupo.id === grupoActivo) || gruposVisibles[0];
  const endpointActivo = endpointsVisibles.find((endpoint) => endpoint.id === endpointId)
    || endpointsVisibles.find((endpoint) => endpoint.grupo === grupoActual?.id);

  function seleccionarGrupo(grupo) {
    const primerEndpoint = endpointsVisibles.find((endpoint) => endpoint.grupo === grupo.id);

    if (primerEndpoint) {
      setGrupoActivo(grupo.id);
      setEndpointId(primerEndpoint.id);
      setRespuesta(null);
      setPrecarga(null);
    }
  }

  function seleccionarEndpoint(endpoint) {
    setEndpointId(endpoint.id);
    setRespuesta(null);
    setPrecarga(null);
  }

  function editarRegistro(endpointLista, registro) {
    const endpointEditar = ENDPOINTES_PRIVADOS.find(
      (endpoint) => endpoint.id === endpointLista.editarEndpoint,
    );

    if (!endpointEditar) {
      return;
    }

    const valoresRuta = Object.fromEntries(
      (endpointEditar.parametrosRuta || []).map(({ nombre }) => [
        nombre,
        registro[nombre] ?? registro.id ?? "",
      ]),
    );

    setGrupoActivo(endpointEditar.grupo);
    setEndpointId(endpointEditar.id);
    const cuerpo = datosParaEditar(endpointEditar.ejemploCuerpo, registro);
    if (endpointEditar.id === "vehiculo-actualizar" && Array.isArray(registro.documentos)) {
      cuerpo.documentos = registro.documentos.map((documento) => ({
        documentoId: documento.documentoId,
        nombreArchivo: documento.nombreArchivo || "",
        archivoBase64: documento.archivoBase64 || documento.archivoPdfBase64 || "",
        fechaExpedicion: documento.fechaExpedicion || "",
        fechaVencimiento: documento.fechaVencimiento || "",
      }));
    }

    setPrecarga({
      endpointId: endpointEditar.id,
      valoresRuta,
      cuerpo,
    });
    setRespuesta(null);
  }

  function actualizarApiKey(apiKey) {
    const sesionActualizada = { ...sesion, apiKey };
    guardarSesion(sesionActualizada);
    alActualizarSesion(sesionActualizada);
  }

  return (
    <main className="portal-shell">
      <EncabezadoPortal
        sesion={sesion}
        publico={publico}
        alCerrarSesion={alCerrarSesion}
        volverAlInicio={volverAlInicio}
      />
      
      <section className="portal-heading">
        <p className="eyebrow">{publico ? "CONSULTAS SIN INICIAR SESIÓN" : "PANEL DE SERVICIOS"}</p>
        <h1>{grupoActual?.nombre}</h1>
        <p className="page-subtitle">
          {publico
            ? "Elija una consulta disponible."
            : grupoActivo === "rutas"
              ? "Comience por consultar una ruta; los demás servicios están organizados por secciones."
              : "Elija qué desea hacer en esta sección."}
        </p>
      </section>

      <section className="service-workspace">
        <NavegacionServicios
          endpoints={endpointsVisibles}
          grupoActivo={grupoActual?.id}
          endpointActivo={endpointActivo}
          alSeleccionarGrupo={seleccionarGrupo}
          alSeleccionarEndpoint={seleccionarEndpoint}
        />
        {endpointActivo && (
          <div className="endpoint-main">
            <FormularioSolicitud
              endpoint={endpointActivo}
              sesion={sesion}
              precarga={precarga}
              alRecibirRespuesta={setRespuesta}
              alActualizarApiKey={actualizarApiKey}
              alCancelarEdicion={() => setPrecarga(null)}
            />
            {respuesta && (
              <ResultadoSolicitud
                endpoint={endpointActivo}
                respuesta={respuesta}
                alEditar={endpointActivo.editarEndpoint
                  ? (registro) => editarRegistro(endpointActivo, registro)
                  : null}
              />
            )}
          </div>
        )}
      </section>

      <footer className="portal-footer">
        {publico
          ? "Estas consultas no requieren autenticación."
          : "Las credenciales se eliminan al cerrar sesión."}
      </footer>
    </main>
  );
}
