import { ListaParadas, TrazarRutaEnMapa } from "../RouteVisualization.jsx";

function ResultadoRuta({ paradas }) {
  const ordenadas = [...paradas].sort((a, b) => (a.ordenParada ?? 0) - (b.ordenParada ?? 0));

  return (
    <section className="results-grid route-results" aria-label="Visualización de la ruta">
      <div className="stops-panel">
        <div className="panel-header">
          <div><p className="eyebrow">TRAYECTO</p><h2>Paradas de la ruta</h2></div>
          <span className="route-chip">{ordenadas[0]?.codigoRuta || "Ruta"}</span>
        </div>
        <div className="stops-content"><ListaParadas paradas={ordenadas} /></div>
      </div>
      <TrazarRutaEnMapa paradas={ordenadas} />
    </section>
  );
}

export default function ResultadoSolicitud({ endpoint, respuesta }) {
  if (!respuesta) {
    return null;
  }

  const esRuta = endpoint.id === "ruta-por-codigo"
    && respuesta.correcto
    && Array.isArray(respuesta.contenido)
    && respuesta.contenido.length > 0;

  return (
    <section className="api-result" aria-live="polite">
      <div className="result-heading">
        <div><p className="eyebrow">RESPUESTA DEL SERVIDOR</p><h2>Resultado</h2></div>

        <span className={`http-status ${respuesta.correcto ? "is-success" : "is-error"}`}>
          {respuesta.correcto ? "Correcta" : "Error"} · {respuesta.estado}
        </span>
      </div>
      
      {esRuta && <ResultadoRuta paradas={respuesta.contenido} />}
      <details className="json-result" open>
        <summary>Respuesta en formato JSON</summary>
        <pre>{typeof respuesta.contenido === "string"
          ? respuesta.contenido
          : JSON.stringify(respuesta.contenido, null, 2)}</pre>
      </details>
    </section>
  );
}
