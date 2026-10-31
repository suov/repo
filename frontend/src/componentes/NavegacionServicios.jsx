import {
  GRUPOS_ADMINISTRATIVOS,
  GRUPOS_ENDPOINTES,
} from "../endpoints/grupos.js";

export default function NavegacionServicios({
  endpoints,
  grupoActivo,
  endpointActivo,
  alSeleccionarGrupo,
  alSeleccionarEndpoint,
}) {
  const gruposVisibles = GRUPOS_ENDPOINTES.filter((grupo) =>
    endpoints.some((endpoint) => endpoint.grupo === grupo.id)
  );

  const endpointsDelGrupo = endpoints.filter(
    (endpoint) => endpoint.grupo === grupoActivo
  );

  function nombreAccion(metodo) {
    if (metodo === "GET") return "Consultar";
    if (metodo === "POST") return "Crear";
    if (metodo === "PUT") return "Actualizar";

    return "Eliminar";
  }

  return (
    <>
      <nav
        className="service-tabs"
        aria-label="Secciones del portal"
      >
        {gruposVisibles.map((grupo) => (
          <button
            className={`service-tab ${
              grupoActivo === grupo.id ? "is-active" : ""
            }`}
            key={grupo.id}
            type="button"
            onClick={() => alSeleccionarGrupo(grupo)}
          >
            <span aria-hidden="true">
              {grupo.icono}
            </span>

            {grupo.nombre}

            <small>
              {
                endpoints.filter(
                  (endpoint) => endpoint.grupo === grupo.id
                ).length
              }
            </small>
          </button>
        ))}
      </nav>

      <aside
        className="endpoint-sidebar"
        aria-label="Acciones disponibles"
      >
        <div className="sidebar-heading">
          <p className="eyebrow">
            ACCIONES
          </p>

          <span>
            {endpointsDelGrupo.length} disponibles
          </span>
        </div>

        <div className="endpoint-list">
          {endpointsDelGrupo.map((endpoint) => (
            <button
              className={`endpoint-link ${
                endpointActivo?.id === endpoint.id ? "is-active" : ""
              }`}
              key={endpoint.id}
              type="button"
              onClick={() => alSeleccionarEndpoint(endpoint)}
            >
              <span
                className={`method-badge method-${endpoint.metodo.toLowerCase()}`}
              >
                {nombreAccion(endpoint.metodo)}
              </span>

              <span className="endpoint-link-copy">
                <strong>
                  {endpoint.titulo}
                </strong>

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