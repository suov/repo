import { ListaParadas, TrazarRutaEnMapa } from "../RouteVisualization.jsx";
import TarjetaResultado from "./TarjetaResultado.jsx";

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

export default function ResultadoSolicitud({ endpoint, respuesta, alEditar }) {
  if (!respuesta) {
    return null;
  }

  const esRuta = endpoint.id === "ruta-por-codigo"
    && respuesta.correcto
    && Array.isArray(respuesta.contenido)
    && respuesta.contenido.length > 0;

  const contenido = respuesta.contenido;
  const registros = Array.isArray(contenido)
    ? contenido
    : Array.isArray(contenido?.data)
      ? contenido.data
      : contenido?.data && typeof contenido.data === "object"
        ? [contenido.data]
        : contenido && typeof contenido === "object"
          ? [contenido]
          : [];

  function ocultarDatosPrivados(valor) {
    if (Array.isArray(valor)) {
      return valor.map(ocultarDatosPrivados);
    }

    if (valor && typeof valor === "object") {
      return Object.fromEntries(Object.entries(valor).map(([nombre, dato]) => [
        nombre,
        /password|apikey|api_key/i.test(nombre)
          ? "Dato privado"
          : ocultarDatosPrivados(dato),
      ]));
    }

    return valor;
  }

  return (
    <section className="api-result" aria-live="polite">
      <div className="result-heading">
        <div><p className="eyebrow">RESPUESTA DEL SERVIDOR</p><h2>Resultado</h2></div>

        <span className={`http-status ${respuesta.correcto ? "is-success" : "is-error"}`}>
          {respuesta.correcto ? "Correcta" : "Error"} · {respuesta.estado}
        </span>
      </div>
      
      {esRuta && <ResultadoRuta paradas={respuesta.contenido} />}
      {respuesta.correcto && registros.length > 0 && (
        <div className="record-grid">
          {registros.map((registro, indice) => (
            <TarjetaResultado
              key={registro.id ?? indice}
              registro={registro}
              mostrarApiKey={endpoint.grupo === "usuarios"}
              esConductorHabilitado={endpoint.id === "conductores-habilitados"}
              alEditar={alEditar ? () => alEditar(registro) : null}
            />
          ))}
        </div>
      )}
      <details className="json-result">
        <summary>Ver respuesta técnica</summary>
        <pre>{typeof contenido === "string"
          ? contenido
          : JSON.stringify(ocultarDatosPrivados(contenido), null, 2)}</pre>
      </details>
    </section>
  );
}
