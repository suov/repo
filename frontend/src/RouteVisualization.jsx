import { useEffect, useRef, useState } from "react";

const GOOGLE_MAPS_API_KEY = import.meta.env.VITE_GOOGLE_MAPS_API_KEY?.trim();
let mapasPromise;

function cargarMapas() {

  if (window.google?.maps?.Map) {
    return Promise.resolve(window.google.maps);
  }

  if (!mapasPromise) {
    mapasPromise = new Promise((resolve, reject) => {

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
          mapasPromise = undefined;
          reject(new Error("ERROR: Google Maps no se terminó de inicializar."));
        }
      };

      script.onerror = () => {
        mapasPromise = undefined;
        script.remove();
        reject(new Error("ERROR: No se pudo cargar Google Maps. Revisa la clave y sus restricciones."));
      };

      document.head.appendChild(script);

    });
  }

  return mapasPromise;
}

function coordenadasDe(parada) {
  const lat = Number(parada.latitud);
  const lng = Number(parada.longitud);

  if (parada.latitud == null || parada.longitud == null 
      || !Number.isFinite(lat) || !Number.isFinite(lng) ||
      lat < -90 || lat > 90 || lng < -180 || lng > 180) {

    return null;
  }

  return { lat, lng };
}

export function ListaParadas({ paradas }) {

  return (
    <ol className="stop-list">
      {paradas.map((parada, index) => {
        const tieneCoordenadas = Boolean(coordenadasDe(parada));
        const esInicio = index === 0;
        const esFin = index === paradas.length - 1;
        const tipoParada = esInicio ? "Punto de partida" : esFin ? "Destino" : "Parada";

        return (
          <li className="stop-item" key={parada.id ?? `${parada.codigoRuta}-${parada.ordenParada}-${index}`}>
            <div className="stop-track">
              <span className={`stop-marker ${esInicio ? "is-start" : ""} ${esFin ? "is-end" : ""}`}>
                {parada.ordenParada ?? index + 1}
              </span>
              {index < paradas.length - 1 && <span className="stop-connector" />}
            </div>
            <div className="stop-copy">
              <div className="stop-heading">
                <span>{tipoParada}</span>
                <span className={`coordinate-status ${tieneCoordenadas ? "is-ready" : ""}`}>
                  <i />
                  {tieneCoordenadas ? "Geolocalizada" : "Sin coordenadas"}
                </span>
              </div>
              <strong>{parada.ubicacion || "Ubicación sin nombre"}</strong>
              {tieneCoordenadas && (
                <small>{Number(parada.latitud).toFixed(5)}, {Number(parada.longitud).toFixed(5)}</small>
              )}
            </div>
          </li>
        );

      })}
    </ol>
  );
}

export function TrazarRutaEnMapa({ paradas }) {
  const mapaElemento = useRef(null);
  const mapa = useRef(null);
  const marcadores = useRef([]);
  const linea = useRef(null);

  const [error, setError] = useState("");
  const [version, setVersion] = useState(0);

  const posiciones = paradas.map((parada) => ({ parada, posicion: coordenadasDe(parada) }))
    .filter(({ posicion }) => posicion);

  useEffect(() => {

    if (!posiciones.length) {
      setError("ERROR: Ruta sin coordenadas para mostrar.");
      return undefined;
    }

    if (!GOOGLE_MAPS_API_KEY) {
      setError("ERROR: Se debe configurar VITE_GOOGLE_MAPS_API_KEY para habilitar el mapa.");
      return undefined;
    }

    let cancelado = false;
    setError("");

    cargarMapas().then((maps) => {
      if (cancelado || !mapaElemento.current) {
        return;
      }

      if (!mapa.current) {
        mapa.current = new maps.Map(mapaElemento.current, {
          center: posiciones[0].posicion,
          zoom: 13,
          mapTypeControl: false,
          streetViewControl: false,
          fullscreenControl: true,
        });
      }

      marcadores.current.forEach((marcador) => marcador.setMap(null));
      marcadores.current = [];
      linea.current?.setMap(null);
      
      const limites = new maps.LatLngBounds();
      const trayecto = posiciones.map(({ parada, posicion }) => {
        const marcador = new maps.Marker({
          map: mapa.current,
          position: posicion,
          label: String(parada.ordenParada ?? ""),
          title: parada.ubicacion || "Parada",
        });

        marcadores.current.push(marcador);
        limites.extend(posicion);
        return posicion;
      });

      if (trayecto.length > 1) {
        linea.current = new maps.Polyline({
          map: mapa.current,
          path: trayecto,
          geodesic: true,
          strokeColor: "#276ef1",
          strokeOpacity: 0.78,
          strokeWeight: 4,
        });

        mapa.current.fitBounds(limites, 56);
      } else {
        mapa.current.setCenter(trayecto[0]);
        mapa.current.setZoom(15);
      }

      setVersion((actual) => actual + 1);

    }).catch((mapError) => {
      if (!cancelado) {
        setError(mapError.message || "No se pudo mostrar el mapa.");
      }
    });

    return () => {
      cancelado = true;
    };
    
  }, [paradas]);

  return (
    <section className="map-panel" aria-label="Mapa de la ruta">
      <div className="map-toolbar">
        <div><p className="eyebrow">VISTA GEOGRÁFICA</p><h2>Paradas en el mapa</h2></div>
        <span className="map-count"><span className="live-dot" />{posiciones.length} ubicaciones</span>
      </div>
      <div className="map-frame">
        <div ref={mapaElemento} className="map-canvas" data-map-version={version} />
        {error && <div className="map-notice" role="status"><span className="notice-icon">i</span><p>{error}</p></div>}
      </div>
      <div className="map-legend">
        <span><i className="legend-start" /> Inicio</span>
        <span><i className="legend-stop" /> Parada</span>
        <span><i className="legend-end" /> Destino</span>
      </div>
    </section>
  );
}
