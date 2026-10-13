const OPCIONES = {
  tipoPersona: ["C", "A"],
  tipoDocumento: ["CC"],
  tipoVehiculo: ["AUTOMOVIL", "MOTOCICLETA"],
  tipoServicio: ["PU", "PR"],
  tipoCombustible: ["GASOLINA", "DIESEL", "ELECTRICO", "GAS"],
  estadoConductor: ["PO", "EA", "RO"],
  estado: ["HABILITADO", "VENCIDO", "EN_VERIFICACION"],
  tipoVehiculoAlQueAplica: ["A", "M", "AM"],
  requisitoSegunTipoVehiculo: ["RA", "RM", "RR"],
};

function etiqueta(nombre) {
  const nombres = {
    tipoPersona: "Tipo de persona (C: conductor, A: administrador)",
    tipoDocumento: "Tipo de documento",
    tipoVehiculo: "Tipo de vehículo",
    tipoServicio: "Tipo de servicio",
    tipoCombustible: "Tipo de combustible",
    tipoVehiculoAlQueAplica: "Tipo de vehículo al que aplica",
    requisitoSegunTipoVehiculo: "Requisito según tipo de vehículo",
    licenciaConduccionBase64: "Licencia de conducción (Base64)",
    fechaVigenciaLicencia: "Vigencia de la licencia",
    capacidadPasajeros: "Capacidad de pasajeros",
    colorHexadecimal: "Color (código hexadecimal)",
    codigoDocumentoParametrizado: "Código del documento",
    nombreDocumento: "Nombre del documento",
    fechaExpedicion: "Fecha de expedición",
    fechaVencimiento: "Fecha de vencimiento",
    archivoBase64: "Archivo (Base64)",
    archivoPdfBase64: "Archivo PDF (Base64)",
  };

  return nombres[nombre] || nombre
    .replace(/([A-Z])/g, " $1")
    .replace(/^./, (letraInicial) => letraInicial.toUpperCase());
}

function tipoDeCampo(nombre, valor) {
  if (nombre.toLowerCase().startsWith("fecha")) {
    return "date";
  }

  if (typeof valor === "number") {
    return "number";
  }

  if (nombre.toLowerCase().includes("email")) {
    return "email";
  }

  return "text";
}

function cambiarEnRuta(objeto, ruta, valor) {
  if (ruta.length === 0) {
    return valor;
  }

  const [parte, ...resto] = ruta;
  const copia = Array.isArray(objeto) ? [...objeto] : { ...objeto };
  copia[parte] = cambiarEnRuta(copia[parte], resto, valor);
  return copia;
}

function crearCampos(datos, ruta, alCambiar) {
  if (!datos || typeof datos !== "object") {
    return null;
  }

  if (Array.isArray(datos)) {
    return (
      <div className="array-list">
        {datos.map((elemento, indice) => (
          <fieldset className="data-group" key={`${ruta.join(".")}-${indice}`}>
            <legend>Elemento {indice + 1}</legend>
            {elemento && typeof elemento === "object"
              ? crearCampos(elemento, [...ruta, indice], alCambiar)
              : (
                <label className="field">
                  <span>Valor</span>
                  <input
                    value={elemento ?? ""}
                    onChange={(evento) => alCambiar(
                      [...ruta, indice],
                      evento.target.value,
                    )}
                  />
                </label>
              )}
            <button
              className="secondary-button"
              type="button"
              onClick={() => alCambiar(
                ruta,
                datos.filter((_, posicion) => posicion !== indice),
              )}
            >
              Quitar elemento
            </button>
          </fieldset>
        ))}
        {datos.length > 0 && (
          <button
            className="secondary-button"
            type="button"
            onClick={() => alCambiar(
              ruta,
              [...datos, JSON.parse(JSON.stringify(datos[0]))],
            )}
          >
            Agregar elemento
          </button>
        )}
      </div>
    );
  }

  return Object.entries(datos).map(([nombre, valor]) => {
    const rutaActual = [...ruta, nombre];

    if (Array.isArray(valor)) {
      const ejemplo = valor[0];

      return (
        <fieldset className="data-group" key={rutaActual.join(".")}>
          <legend>{etiqueta(nombre)}</legend>
          {valor.length === 0
            ? <p className="form-help">No hay elementos para enviar en este campo.</p>
            : valor.map((elemento, indice) => (
              <div className="array-item" key={`${rutaActual.join(".")}-${indice}`}>
                {typeof elemento === "object" && elemento !== null
                  ? crearCampos(elemento, [...rutaActual, indice], alCambiar)
                  : (
                    <label className="field">
                      <span>{etiqueta(nombre)} {indice + 1}</span>
                      <input
                        value={elemento ?? ""}
                        onChange={(evento) => alCambiar(
                          [...rutaActual, indice],
                          evento.target.value,
                        )}
                      />
                    </label>
                  )}
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => alCambiar(
                    rutaActual,
                    valor.filter((_, posicion) => posicion !== indice),
                  )}
                >
                  Quitar
                </button>
              </div>
            ))}
          {ejemplo && (
            <button
              className="secondary-button"
              type="button"
              onClick={() => alCambiar(rutaActual, [...valor, ejemplo])}
            >
              Agregar elemento
            </button>
          )}
        </fieldset>
      );
    }

    if (valor && typeof valor === "object") {
      return (
        <fieldset className="data-group" key={rutaActual.join(".")}>
          <legend>{etiqueta(nombre)}</legend>
          {crearCampos(valor, rutaActual, alCambiar)}
        </fieldset>
      );
    }

    const opciones = OPCIONES[nombre];
    const tipo = tipoDeCampo(nombre, valor);

    return (
      <label className="field" key={rutaActual.join(".")}>
        <span>{etiqueta(nombre)}</span>
        {opciones ? (
          <select
            value={valor ?? ""}
            onChange={(evento) => alCambiar(rutaActual, evento.target.value)}
          >
            <option value="">Seleccione una opción</option>
            {opciones.map((opcion) => (
              <option key={opcion} value={opcion}>{opcion}</option>
            ))}
          </select>
        ) : (
          <input
            type={tipo}
            step={tipo === "number" ? "any" : undefined}
            value={valor ?? ""}
            onChange={(evento) => alCambiar(
              rutaActual,
              tipo === "number" && evento.target.value !== ""
                ? Number(evento.target.value)
                : evento.target.value,
            )}
          />
        )}
      </label>
    );
  });
}

export default function FormularioDatos({ valores, alCambiar }) {
  return (
    <div className="data-form-grid">
      {crearCampos(valores, [], (ruta, valor) => alCambiar((actuales) => (
        cambiarEnRuta(actuales, ruta, valor)
      )))}
    </div>
  );
}
