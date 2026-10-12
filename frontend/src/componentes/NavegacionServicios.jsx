import { GRUPOS_ADMINISTRATIVOS, GRUPOS_ENDPOINTES } from "../endpoints/grupos.js";

export default function NavegacionServicios({
  endpoints,
  grupoActivo,
  endpointActivo,
  alSeleccionarGrupo,
  alSeleccionarEndpoint,
}) {

  const gruposVisibles = GRUPOS_ENDPOINTES.filter((grupo) =>
    endpoints.some((endpoint) => endpoint.grupo === grupo.id),
  );

  const endpointsDelGrupo = endpoints.filter((endpoint) => endpoint.grupo === grupoActivo);

  return (
    <>
      <nav className="service-tabs" aria-label="Servicios de la API">
        {gruposVisibles.map((grupo) => (
          <button
            className={`service-tab ${grupoActivo === grupo.id ? "is-active" : ""}`}
            key={grupo.id}
            type="button"
            onClick={() => alSeleccionarGrupo(grupo)}
          >
            <span aria-hidden="true">{grupo.icono}</span>
            {grupo.nombre}
            <small>{endpoints.filter((endpoint) => endpoint.grupo === grupo.id).length}</small>
          </button>
        ))}
      </nav>

      <aside className="endpoint-sidebar" aria-label="Operaciones disponibles">
        <div className="sidebar-heading">
          <p className="eyebrow">SERVICIOS</p>
          <span>{endpointsDelGrupo.length} operaciones</span>
        </div>

        <div className="endpoint-list">
          {endpointsDelGrupo.map((endpoint) => (
            <button
              className={`endpoint-link ${endpointActivo?.id === endpoint.id ? "is-active" : ""}`}
              key={endpoint.id}
              type="button"
              onClick={() => alSeleccionarEndpoint(endpoint)}
            >
              <span className={`method-badge method-${endpoint.metodo.toLowerCase()}`}>{endpoint.metodo}</span>
              <span className="endpoint-link-copy">
                <strong>{endpoint.titulo}</strong>
                <small>
                  {endpoint.acceso === "publico"
                    ? "Público"
                    : GRUPOS_ADMINISTRATIVOS.has(endpoint.grupo)
                      ? "Requiere administrador"
                      : "Requiere sesión"}
                </small>
              </span>
            </button>
          ))}
        </div>
      </aside>
    </>
  );
}
