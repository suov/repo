function etiqueta(nombre) {
  return nombre
    .replace(/([A-Z])/g, " $1")
    .replace(/^./, (letra) => letra.toUpperCase());
}

function mostrarValor(nombre, valor, mostrarApiKey) {
  if (/password/i.test(nombre) || (!mostrarApiKey && /apikey|api_key/i.test(nombre))) {
    return "Dato privado";
  }

  if (valor === null || valor === undefined || valor === "") {
    return "Sin información";
  }

  if (Array.isArray(valor)) {
    return `${valor.length} elemento${valor.length === 1 ? "" : "s"}`;
  }

  if (typeof valor === "object") {
    return Object.entries(valor)
      .map(([clave, dato]) => `${etiqueta(clave)}: ${mostrarValor(clave, dato, mostrarApiKey)}`)
      .join(" · ");
  }

  if (/base64|archivo/i.test(nombre) && String(valor).length > 80) {
    return "Archivo adjunto";
  }

  return String(valor);
}

function tituloDe(registro, esUsuario, esConductorHabilitado) {
  if (esUsuario && registro.login) {
    return registro.login;
  }

  if (esConductorHabilitado && registro.nombreConductor) {
    return registro.nombreConductor;
  }

  return registro.placa
    || registro.nombreDocumento
    || registro.codigoRuta
    || registro.codigoDocumentoParametrizado
    || [registro.nombre, registro.apellido].filter(Boolean).join(" ")
    || `Registro ${registro.id ?? ""}`;
}

export default function TarjetaResultado({
  registro,
  alEditar,
  mostrarApiKey = false,
  esConductorHabilitado = false,
}) {
  const campos = Object.entries(registro).filter(([nombre]) =>
    !/password/i.test(nombre)
      && !(mostrarApiKey && nombre === "login")
      && !(esConductorHabilitado && nombre === "nombreConductor")
      && (mostrarApiKey || !/apikey|api_key/i.test(nombre)),
  );

  return (
    <article className="record-card">
      <div className="record-card-heading">
        <h3>{tituloDe(registro, mostrarApiKey, esConductorHabilitado)}</h3>
        {registro.id !== undefined && <span>#{registro.id}</span>}
      </div>
      <dl className="record-details">
        {campos.map(([nombre, valor]) => (
          <div key={nombre}>
            <dt>{etiqueta(nombre)}</dt>
            <dd>{mostrarValor(nombre, valor, mostrarApiKey)}</dd>
          </div>
        ))}
      </dl>
      {alEditar && (
        <button className="secondary-button" type="button" onClick={() => alEditar(registro)}>
          Editar este registro
        </button>
      )}
    </article>
  );
}
