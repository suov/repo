import { useState } from "react";
import FormularioInicioSesion from "./componentes/FormularioInicioSesion.jsx";
import Inicio from "./componentes/Inicio.jsx";
import PanelServicios from "./componentes/PanelServicios.jsx";
import { eliminarSesion, leerSesion } from "./servicios/sesion.js";

export default function App() {

  const [sesion, setSesion] = useState(leerSesion);
  const [vista, setVista] = useState("inicio");

  function cerrarSesion() {
    eliminarSesion();
    setSesion(null);
    setVista("inicio");
  }

  if (sesion) {
    return (
      <PanelServicios
        sesion={sesion}
        alCerrarSesion={cerrarSesion}
        alActualizarSesion={setSesion}
      />
    );
  }

  if (vista === "publico") {
    return <PanelServicios publico volverAlInicio={() => setVista("inicio")} />;
  }

  if (vista === "login") {
    return (
      <FormularioInicioSesion
        alVolver={() => setVista("inicio")}
        alIniciarSesion={setSesion}
      />
    );
  }

  return <Inicio alElegir={setVista} />;
}
