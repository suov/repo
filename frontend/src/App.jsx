import { useEffect, useMemo, useRef, useState } from "react";

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || "").replace(/\/+$/, "");
const GOOGLE_MAPS_API_KEY = import.meta.env.VITE_GOOGLE_MAPS_API_KEY?.trim();

let googleMapsPromise;

function cargarMapas() {
  if (window.google?.maps?.Map) {
    return Promise.resolve(window.google.maps);
  }

  if (googleMapsPromise) {
    return googleMapsPromise;
  }

  googleMapsPromise = new Promise((resolve, reject) => {
    const script = document.createElement("script");
    const url = new URL("https://maps.googleapis.com/maps/api/js");
    url.searchParams.set("key", GOOGLE_MAPS_API_KEY);
    url.searchParams.set("v", "weekly");

    script.src = url.toString();
    script.async = true;
    script.onload = () => {
      if (window.google?.maps?.Map) {
        resolve(window.google.maps);
      } else {
        googleMapsPromise = undefined;
        reject(new Error("ERROR: Google Maps no terminó de inicializar."));
      }
    };
    script.onerror = () => {
      googleMapsPromise = undefined;
      reject(
        new Error(
          "ERROR: No se pudo cargar Google Maps. Revisa la clave y sus restricciones.",
        ),
      );
    };
    document.head.appendChild(script);
  });

  return googleMapsPromise;
}

function obtenerCoordenadas(trayecto) {
  const lat = Number(trayecto.latitud);
  const lng = Number(trayecto.longitud);
  if (
    trayecto.latitud == null ||
    trayecto.longitud == null ||
    !Number.isFinite(lat) ||
    !Number.isFinite(lng) ||
    lat < -90 ||
    lat > 90 ||
    lng < -180 ||
    lng > 180
  ) {
    return null;
  }
  return { lat, lng };
}

function TrazarRutaEnMapa({ paradas }) {
  const mapaElemento = useRef(null);
  const mapaInstancia = useRef(null);
  const marcadores = useRef([]);
  const ruta = useRef(null);
  const [errorMapa, setErrorMapa] = useState("");

  useEffect(() => {
    let cancelado = false;
    const paradasConCoordenadas = paradas
      .map((parada) => ({ parada, posicion: obtenerCoordenadas(parada) }))
      .filter((item) => item.posicion);

    if (paradasConCoordenadas.length === 0) {
      setErrorMapa("ERROR: Esta ruta todavía no tiene coordenadas para mostrar.");
      return () => {
        cancelado = true;
      };
    }

    if (!GOOGLE_MAPS_API_KEY) {
      setErrorMapa(
        "ERROR: se debe configurar apikey para habilitar el mapa.",
      );
      return () => {
        cancelado = true;
      };
    }

    setErrorMapa("");
    cargarMapas()
      .then((maps) => {
        if (cancelado || !mapaElemento.current) {
          return;
        }

        if (!mapaInstancia.current) {
          mapaInstancia.current = new maps.Map(mapaElemento.current, {
            center: paradasConCoordenadas[0].posicion,
            zoom: 13,
            mapTypeControl: false,
            streetViewControl: false,
            fullscreenControl: true,
          });
        }

        marcadores.current.forEach((marcador) => marcador.setMap(null));
        marcadores.current = [];
        if (ruta.current) {
          ruta.current.setMap(null);
          ruta.current = null;
        }

        const bordes = new maps.LatLngBounds();
        const path = [];

        paradasConCoordenadas.forEach(({ parada, posicion }) => {
          const marker = new maps.Marker({
            map: mapaInstancia.current,
            position: posicion,
            label: String(parada.ordenParada ?? ""),
            title: parada.ubicacion || "Parada",
          });
          marcadores.current.push(marker);
          bordes.extend(posicion);
          path.push(posicion);
        });

        if (path.length > 1) {
          ruta.current = new maps.Polyline({
            map: mapaInstancia.current,
            path,
            geodesic: true,
            strokeColor: "#276ef1",
            strokeOpacity: 0.78,
            strokeWeight: 4,
          });
          mapaInstancia.current.fitBounds(bordes, 56);
        } else {
          mapaInstancia.current.setCenter(path[0]);
          mapaInstancia.current.setZoom(15);
        }
      })
      .catch((error) => {
        if (!cancelado) {
          setErrorMapa(error.message || "ERROR: No se pudo mostrar el mapa.");
        }
      });

    return () => {
      cancelado = true;
    };
  }, [paradas]);

  const mapaListo = paradas.filter(obtenerCoordenadas).length;

  return (
    <section className="map-panel" aria-label="Mapa de la ruta">
      <div className="map-toolbar">
        <div>
          <p className="eyebrow">VISTA GEOGRÁFICA</p>
          <h2>Paradas en el mapa</h2>
        </div>
        <span className="map-count">
          <span className="live-dot" />
          {mapaListo} {mapaListo === 1 ? "ubicación" : "ubicaciones"}
        </span>
      </div>
      <div className="map-frame">
        <div ref={mapaElemento} className="map-canvas" />
        {errorMapa && (
          <div className="map-notice" role="status">
            <span className="notice-icon" aria-hidden="true">
              i
            </span>
            <p>{errorMapa}</p>
          </div>
        )}
      </div>
      <div className="map-legend">
        <span>
          <i className="legend-start" /> Inicio
        </span>
        <span>
          <i className="legend-stop" /> Parada
        </span>
        <span>
          <i className="legend-end" /> Destino
        </span>
      </div>
    </section>
  );
}

function ListaParadas({ paradas }) {
  if (paradas.length === 0) {
    return (
      <div className="empty-stops">
        <span className="empty-route-icon" aria-hidden="true">
          ↖
        </span>
        <p>Paradas de la ruta</p>
      </div>
    );
  }

  return (
    <ol className="stop-list">
      {paradas.map((parada, index) => {
        const hasCoordinates = Boolean(obtenerCoordenadas(parada));
        const primera = index === 0;
        const ultima = index === paradas.length - 1;
        const nombreParada = primera ? "Punto de partida" : ultima ? "Destino" : "Parada";

        return (
          <li className="stop-item" key={parada.id ?? `${parada.codigoRuta}-${parada.ordenParada}`}>
            <div className="stop-track">
              <span
                className={`stop-marker ${primera ? "is-start" : ""} ${ultima ? "is-end" : ""}`}
              >
                {parada.ordenParada ?? index}
              </span>
              {index < paradas.length - 1 && <span className="stop-connector" />}
            </div>
            <div className="stop-copy">
              <div className="stop-heading">
                <span>{nombreParada}</span>
                <span className={`coordinate-status ${hasCoordinates ? "is-ready" : ""}`}>
                  <i />
                  {hasCoordinates ? "Geolocalizada" : "Sin coordenadas"}
                </span>
              </div>
              <strong>{parada.ubicacion || "Ubicación sin nombre"}</strong>
              {hasCoordinates && (
                <small>
                  {Number(parada.latitud).toFixed(5)}, {Number(parada.longitud).toFixed(5)}
                </small>
              )}
            </div>
          </li>
        );
      })}
    </ol>
  );
}

/* Aplicación */
export default function App() {

  /* Estados */
  const [codigoRuta, setCodigoRuta] = useState("");
  const [token, setToken] = useState("");
  const [apiKey, setApiKey] = useState("");
  const [paradas, setParadas] = useState([]);
  const [rutaCargada, setRutaCargada] = useState("");
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState("");

  /* Ordenamos trayectos por orden de parada */
  const paradasOrdenadas = useMemo(
    () => [...paradas].sort((a, b) => (a.ordenParada ?? 0) - (b.ordenParada ?? 0)),
    [paradas],
  );

  /* Leer espacios y validar */
  async function handleSubmit(event) {

    event.preventDefault();
    const codigoNormalizado = codigoRuta.trim();
    const tokenNormalizado = token.trim();
    const apiKeyNormalizada = apiKey.trim();

    if (!codigoNormalizado || !tokenNormalizado || !apiKeyNormalizada) {
      setError("ERROR: Completa el código de ruta, el token JWT y la API key de usuario.");
      return;
    }

    setCargando(true);
    setError("");
    setParadas([]);
    setRutaCargada("");

    const autorizacion = tokenNormalizado.startsWith("Bearer ")
      ? tokenNormalizado
      : `Bearer ${tokenNormalizado}`;

    /* Construcción de la solicitud HTTP y fetch*/
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/trayectos/ruta/${encodeURIComponent(codigoNormalizado)}`,
        {
          method: "GET",
          headers: {
            Authorization: autorizacion,
            "X-API-KEY": apiKeyNormalizada,
          },
        },
      );

      const contentType = response.headers.get("content-type") || "";
      const payload = contentType.includes("application/json")
        ? await response.json()
        : null;

      if (!response.ok) {
        throw new Error(
          payload?.message ||
            payload?.reason ||
            `ERROR: Verifica ruta y credenciales. ${response.status}.`,
        );
      }

      if (!Array.isArray(payload)) {
        throw new Error("La API respondió, pero el formato de la ruta no es válido.");
      }

      if (payload.length === 0) {
        throw new Error("La ruta no contiene paradas.");
      }

      setParadas(payload);
      setRutaCargada.toUpperCase();
    } catch (requestError) {
      setError(
        requestError instanceof TypeError
          ? "No se pudo conectar con la API. Verifica que el backend esté activo y permita este origen."
          : requestError.message,
      );
    } finally {
      setCargando(false);
    }
  }

  return (
    <main className="app-shell">

      {/* Header */}
      <header className="topbar">
        <a className="brand" href="/" aria-label="Rutas, inicio">
          <span className="brand-mark" aria-hidden="true">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-users-group">
              <path stroke="none" d="M0 0h24v24H0z" fill="none" />
              <path d="M10 13a2 2 0 1 0 4 0a2 2 0 0 0 -4 0" />
              <path d="M8 21v-1a2 2 0 0 1 2 -2h4a2 2 0 0 1 2 2v1" />
              <path d="M15 5a2 2 0 1 0 4 0a2 2 0 0 0 -4 0" />
              <path d="M17 10h2a2 2 0 0 1 2 2v1" />
              <path d="M5 5a2 2 0 1 0 4 0a2 2 0 0 0 -4 0" />
              <path d="M3 13v-1a2 2 0 0 1 2 -2h2" />
            </svg>
          </span>
          <span className="brand-name">Rutas del mejor Cipas<span>+</span></span>
        </a>
        <div className="topbar-context">
          <span className="context-dot" />
          Tutoría 4
        </div>
      </header>

      {/* Titulo y descripción */}
      <section className="page-heading">
        <div>
          <p className="eyebrow">VISUALIZACIÓN DE RUTAS</p>
          <h1>Explore sus rutas</h1>
          <p className="page-subtitle">
            Consulte las paradas y su ubicación en el mapa.
          </p>
        </div>
        <div className="secure-note">
          <svg viewBox="0 0 20 20" fill="none" aria-hidden="true">
            <path
              d="M10 2.5 4 5v4.8c0 3.7 2.6 6.4 6 7.7 3.4-1.3 6-4 6-7.7V5l-6-2.5Z"
              stroke="currentColor"
              strokeWidth="1.5"
              strokeLinejoin="round"
            />
            <path d="m7.5 10 1.7 1.7 3.5-3.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
          Endpoint protegido
        </div>
      </section>

      {/* Card de consulta */}
      <section className="search-card" aria-label="Buscar ruta">

        {/* Instrucciones */}
        <div className="search-card-heading">
          <div className="search-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="none">
              <circle cx="10.8" cy="10.8" r="6.8" stroke="currentColor" strokeWidth="1.8" />
              <path d="m16 16 4.2 4.2" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
            </svg>
          </div>
          <div>
            <h2>Buscar un trayecto</h2>
            <p>Ingrese el código de ruta y sus credenciales para continuar</p>
          </div>
        </div>

        {/* Formulario */}
        <form className="search-form" onSubmit={handleSubmit}>
          <label className="field route-field">
            <span>Código de ruta</span>
            <input
              autoComplete="off"
              placeholder="Ej. RUTA-UT"
              value={codigoRuta}
              onChange={(event) => setCodigoRuta(event.target.value)}
            />
          </label>

          <label className="field credential-field">
            <span>Token JWT</span>
            <input
              autoComplete="off"
              placeholder="Pegue su token SIN el 'Bearer'"
              type="password"
              value={token}
              onChange={(event) => setToken(event.target.value)}
            />
          </label>

          <label className="field credential-field">
            <span>API key de usuario</span>
            <input
              autoComplete="off"
              placeholder="X-API-KEY"
              type="password"
              value={apiKey}
              onChange={(event) => setApiKey(event.target.value)}
            />
          </label>

          <button className="search-button" type="submit" disabled={cargando}>
            {cargando ? (
              <>
                <span className="spinner" /> Consultando
              </>
            ) : (
              <>
                Consultar la ruta
                <span aria-hidden="true">→</span>
              </>
            )}
          </button>
        </form>

        {/* Si hay un error se muestra. De lo contrario no */}
        {error && (
          <div className="error-message" role="alert">
            <span aria-hidden="true">!</span>
            {error}
          </div>
        )}
      </section>

      {/* Card de visualización */}
      <section className="results-heading">

        {/* Titulo */}
        <div>
          <p className="eyebrow">VISUALIZACIÓN</p>
          <h2>{rutaCargada ? `Ruta ${rutaCargada}` : "Vista de trayecto"}</h2>
        </div>

        {/* Si ya hay paradas cargadas se muestran. Si no, no */}
        {rutaCargada && (
          <span className="stop-total">
            {paradasOrdenadas.length} {paradasOrdenadas.length === 1 ? "parada" : "paradas"}
          </span>
        )}
      </section>

      <section className="results-grid">

        {/* Panel de paradas */}
        <div className="stops-panel">
          <div className="panel-header">
            <div>
              <p className="eyebrow">TRAYECTO</p>
              <h2>Paradas del trayecto</h2>
            </div>
            {rutaCargada && <span className="route-chip">{rutaCargada}</span>}
          </div>
          <div className="stops-content">
            {cargando ? (
              <div className="loading-state">
                <span className="spinner spinner-dark" />
                <span>Cargando recorrido…</span>
              </div>
            ) : (
              <ListaParadas paradas={paradasOrdenadas} />
            )}
          </div>
          {paradasOrdenadas.length > 0 && (
            <div className="panel-footer">
              <span className="footer-check" aria-hidden="true">✓</span>
              Recorrido organizado por orden de parada
            </div>
          )}
        </div>

        <TrazarRutaEnMapa paradas={paradasOrdenadas} />
      </section>

      {/* Footer */}
      <footer className="page-footer">
        <span>El mejor cipas+ <span className="footer-separator">·</span> Visor de trayectos</span>
        <span>Los marcadores muestran las coordenadas registradas para cada parada.</span>
      </footer>
    </main>
  );
}
