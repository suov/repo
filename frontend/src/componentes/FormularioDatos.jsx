import { useState } from "react";

const OPCIONES = {
  tipoPersona: ["C", "A"],
  tipoDocumento: ["CC"],
  tipoVehiculo: ["AUTOMOVIL", "MOTOCICLETA"],
  tipoServicio: ["PU", "PR"],
  tipoCombustible: ["GASOLINA", "DIESEL", "GAS"],
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
    documentoId: "Tipo de documento registrado",
    fechaExpedicion: "Fecha de expedición",
    fechaVencimiento: "Fecha de vencimiento",
    archivoBase64: "Archivo PDF",
    archivoPdfBase64: "Archivo PDF",
  };

  return nombres[nombre] || nombre
    .replace(/([A-Z])/g, " $1")
    .replace(/^./, (letraInicial) => letraInicial.toUpperCase());
}

function tipoDeCampo(nombre, valor) {
  if (nombre === "documentoId") {
    return "number";
  }

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

const PLANTILLAS_ARRAY = {
  documentos: {
    documentoId: "",
    nombreArchivo: "",
    archivoBase64: "",
    fechaExpedicion: "",
    fechaVencimiento: "",
  },
};

function crearCampos(
  datos,
  ruta,
  alCambiar,
  establecerError,
  opcionesDocumentos,
  documentosSeleccionados,
) {
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
              ? crearCampos(
                elemento,
                [...ruta, indice],
                alCambiar,
                establecerError,
                opcionesDocumentos,
                documentosSeleccionados,
              )
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
      const plantilla = ejemplo || PLANTILLAS_ARRAY[nombre];

      return (
        <fieldset className="data-group" key={rutaActual.join(".")}>
          <legend>{etiqueta(nombre)}</legend>
          {valor.length === 0
            ? <p className="form-help">Agregue documentos registrados para asociarlos a este vehículo.</p>
            : valor.map((elemento, indice) => (
              <div className="array-item" key={`${rutaActual.join(".")}-${indice}`}>
                {typeof elemento === "object" && elemento !== null
                  ? crearCampos(
                    elemento,
                    [...rutaActual, indice],
                    alCambiar,
                    establecerError,
                    opcionesDocumentos,
                    valor,
                  )
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
          {plantilla && (
            <button
              className="secondary-button"
              type="button"
              onClick={() => alCambiar(rutaActual, [
                ...valor,
                JSON.parse(JSON.stringify(plantilla)),
              ])}
            >
              Agregar documento
            </button>
          )}
        </fieldset>
      );
    }

    if (valor && typeof valor === "object") {
      return (
        <fieldset className="data-group" key={rutaActual.join(".")}>
          <legend>{etiqueta(nombre)}</legend>
          {crearCampos(
            valor,
            rutaActual,
            alCambiar,
            establecerError,
            opcionesDocumentos,
            documentosSeleccionados,
          )}
        </fieldset>
      );
    }

    const opciones = OPCIONES[nombre];
    const tipo = tipoDeCampo(nombre, valor);
    const esCampoArchivo = nombre === "archivoBase64" || nombre === "archivoPdfBase64";
    const esDocumento = rutaActual[0] === "documentos";
    const esSelectorDocumento = esDocumento && nombre === "documentoId";
    const indiceDocumento = Number(rutaActual[1]);
    const idsEnUso = new Set(documentosSeleccionados
      .filter((_, indice) => indice !== indiceDocumento)
      .map((documento) => String(documento.documentoId)));

    return (
      <label className="field" key={rutaActual.join(".")}>
        <span>{etiqueta(nombre)}</span>
        {opciones ? (
          <select
            value={valor ?? ""}
            onChange={(evento) => alCambiar(rutaActual, evento.target.value)}
          >
            <option value="">Seleccione una opción</option>
            {opciones.map((opcion) => {
              const separador = opcion.indexOf(" - ");
              const codigo = separador === -1 ? opcion : opcion.slice(0, separador);
              const descripcion = separador === -1 ? opcion : opcion.slice(separador + 3);

              return <option key={codigo} value={codigo}>{descripcion}</option>;
            })}
          </select>
        ) : esSelectorDocumento ? (
          <select
            required
            value={valor ?? ""}
            disabled={opcionesDocumentos.length === 0}
            onChange={(evento) => alCambiar(
              rutaActual,
              evento.target.value ? Number(evento.target.value) : "",
            )}
          >
            <option value="">
              {opcionesDocumentos.length
                ? "Seleccione un documento registrado"
                : "No hay documentos registrados disponibles"}
            </option>
            {opcionesDocumentos
              .filter((documento) => !idsEnUso.has(String(documento.id)) ||
                String(documento.id) === String(valor))
              .map((documento) => (
                <option key={documento.id} value={documento.id}>
                  {documento.codigoDocumentoParametrizado} - {documento.nombreDocumento}
                </option>
              ))}
          </select>
        ) : esCampoArchivo ? (
          <>
            <input
              type="file"
              accept="application/pdf,.pdf"
              required={esDocumento && !valor}
              onChange={(evento) => {
                const archivo = evento.target.files?.[0];
                if (!archivo) {
                  return;
                }

                if (archivo.type !== "application/pdf" && !archivo.name.toLowerCase().endsWith(".pdf")) {
                  establecerError("Seleccione un archivo PDF.");
                  evento.target.value = "";
                  return;
                }

                const lector = new FileReader();
                lector.onload = () => {
                  if (typeof lector.result !== "string") {
                    establecerError("No fue posible leer el archivo seleccionado.");
                    return;
                  }

                  const rutaDocumento = rutaActual.slice(0, -1);
                  alCambiar(rutaActual, lector.result);
                  alCambiar([...rutaDocumento, "nombreArchivo"], archivo.name);

                  if (nombre === "archivoPdfBase64") {
                    alCambiar([...rutaDocumento, "archivoBase64"], lector.result);
                  }

                  establecerError("");
                };
                lector.onerror = () => establecerError("No fue posible leer el archivo seleccionado.");
                lector.readAsDataURL(archivo);
              }}
            />
            {valor && (
              <small>
                {rutaActual[0] === "documentos" ? "Archivo actual guardado." : "Ya hay un archivo cargado."}
                {rutaActual[0] === "documentos" && rutaActual[1] !== undefined
                  ? ` ${documentosSeleccionados[Number(rutaActual[1])]?.nombreArchivo || ""}`
                  : ""}
                {" "}Seleccione otro para reemplazarlo.
              </small>
            )}
          </>
        ) : (
          <input
            type={tipo}
            required={esDocumento && !valor}
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

export default function FormularioDatos({
  valores,
  alCambiar,
  opcionesDocumentos = [],
}) {
  const [errorArchivo, establecerError] = useState("");
  const documentos = Array.isArray(valores?.documentos) ? valores.documentos : [];

  return (
    <>
      <div className="data-form-grid">
        {crearCampos(valores, [], (ruta, valor) => alCambiar((actuales) => (
          cambiarEnRuta(actuales, ruta, valor)
        )), establecerError, opcionesDocumentos, documentos)}
      </div>
      {errorArchivo && <p className="error-message" role="alert">{errorArchivo}</p>}
    </>
  );
}
