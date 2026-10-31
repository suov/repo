import { ListaParadas, TrazarRutaEnMapa } from "../RouteVisualization.jsx";
import TarjetaResultado from "./TarjetaResultado.jsx";

function fechaEnDias(fecha) {
  if (typeof fecha !== "string" || !/^\d{4}-\d{2}-\d{2}$/.test(fecha)) {
    return null;
  }

  const [anio, mes, dia] = fecha.split("-").map(Number);
  const fechaDocumento = Date.UTC(anio, mes - 1, dia);
  const hoy = new Date();
  const hoyUtc = Date.UTC(hoy.getFullYear(), hoy.getMonth(), hoy.getDate());
  return Math.round((fechaDocumento - hoyUtc) / 86_400_000);
}

function mostrarFecha(fecha) {
  const partes = fecha.split("-").map(Number);
  return new Intl.DateTimeFormat("es-CO", {
    day: "numeric",
    month: "long",
    year: "numeric",
    timeZone: "UTC",
  }).format(new Date(Date.UTC(partes[0], partes[1] - 1, partes[2])));
}

function ResultadoDocumentosPorVencer({
  registros,
  diasConsulta,
}) {
  const dias = Number.isFinite(diasConsulta) ? diasConsulta : 30;
  const documentos = registros.flatMap((vehiculo) =>
    (Array.isArray(vehiculo.documentos) ? vehiculo.documentos : [])
      .filter((documento) => {
        const faltanDias = fechaEnDias(documento.fechaVencimiento);
        return faltanDias !== null && faltanDias < dias;
      })
      .map((documento) => ({
        ...documento,
        placa: vehiculo.placa,
        marca: vehiculo.marca,
        linea: vehiculo.linea,
        faltanDias: fechaEnDias(documento.fechaVencimiento),
      })),
  ).sort((a, b) => a.faltanDias - b.faltanDias);

  return (
    <section className="expiring-documents" aria-label="Documentos próximos a vencer">
      <header className="expiring-documents-summary">
        <div>
          <p className="eyebrow">REVISIÓN DOCUMENTAL</p>
          <h3>{documentos.length
            ? `${documentos.length} documento${documentos.length === 1 ? "" : "s"} por revisar`
            : "No se encontraron documentos dentro del plazo"}</h3>
          <p>Plazo consultado: {dias} día{dias === 1 ? "" : "s"}</p>
        </div>
      </header>
      {documentos.length > 0 ? (
        <div className="expiring-documents-list">
          {documentos.map((documento, indice) => {
            const vencido = documento.faltanDias < 0;
            const venceHoy = documento.faltanDias === 0;
            const estado = vencido
              ? `Vencido hace ${Math.abs(documento.faltanDias)} día${documento.faltanDias === -1 ? "" : "s"}`
              : venceHoy
                ? "Vence hoy"
                : `Vence en ${documento.faltanDias} día${documento.faltanDias === 1 ? "" : "s"}`;
            const nombreArchivo = documento.nombreArchivo?.trim();

            return (
              <article
                className={`expiring-document-card${vencido ? " is-expired" : ""}`}
                key={`${documento.placa}-${documento.id ?? indice}`}
              >
                <div className="expiring-document-heading">
                  <div>
                    <p className="eyebrow">{vencido ? "VENCIDO" : "PRÓXIMO A VENCER"}</p>
                    <h4>{documento.placa || "Vehículo sin placa"}</h4>
                    {(documento.marca || documento.linea) && (
                      <p>{[documento.marca, documento.linea].filter(Boolean).join(" · ")}</p>
                    )}
                  </div>
                  <span className="expiring-document-badge">{estado}</span>
                </div>
                <dl className="expiring-document-details">
                  {nombreArchivo && (
                    <div>
                      <dt>Documento</dt>
                      <dd>{nombreArchivo}</dd>
                    </div>
                  )}
                  <div>
                    <dt>Fecha de vencimiento</dt>
                    <dd>{mostrarFecha(documento.fechaVencimiento)}</dd>
                  </div>
                </dl>
              </article>
            );
          })}
        </div>
      ) : (
        <p className="expiring-documents-empty">
          La respuesta no contiene documentos con una fecha de vencimiento dentro del plazo consultado.
        </p>
      )}
    </section>
  );
}

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
  const esConsultaPorVencer = endpoint.id === "vehiculos-por-vencer";

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
      {respuesta.correcto && esConsultaPorVencer && (
        <ResultadoDocumentosPorVencer
          registros={registros}
          diasConsulta={respuesta.diasConsulta}
        />
      )}
      {respuesta.correcto && !esConsultaPorVencer && registros.length > 0 && (
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
