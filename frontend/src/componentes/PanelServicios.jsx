import { useState } from "react";
import EncabezadoPortal from "./EncabezadoPortal.jsx";
import FormularioSolicitud from "./FormularioSolicitud.jsx";
import NavegacionServicios from "./NavegacionServicios.jsx";
import ResultadoSolicitud from "./ResultadoSolicitud.jsx";
import { guardarSesion } from "../servicios/sesion.js";
import {
  ENDPOINTES,
  ENDPOINTES_PUBLICOS,
  GRUPOS_ENDPOINTES,
} from "../endpoints/index.js";

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

  const grupoActual = gruposVisibles.find((grupo) => grupo.id === grupoActivo) || gruposVisibles[0];
  const endpointActivo = endpointsVisibles.find((endpoint) => endpoint.id === endpointId)
    || endpointsVisibles.find((endpoint) => endpoint.grupo === grupoActual?.id);

  function seleccionarGrupo(grupo) {
    const primerEndpoint = endpointsVisibles.find((endpoint) => endpoint.grupo === grupo.id);

    if (primerEndpoint) {
      setGrupoActivo(grupo.id);
      setEndpointId(primerEndpoint.id);
      setRespuesta(null);
    }
  }

  function seleccionarEndpoint(endpoint) {
    setEndpointId(endpoint.id);
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
              : "Seleccione una operación para consultar o administrar este servicio."}
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
              key={endpointActivo.id}
              endpoint={endpointActivo}
              sesion={sesion}
              alRecibirRespuesta={setRespuesta}
              alActualizarApiKey={actualizarApiKey}
            />
            {respuesta && (
              <ResultadoSolicitud endpoint={endpointActivo} respuesta={respuesta} />
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
