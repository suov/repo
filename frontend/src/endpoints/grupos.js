export const GRUPOS_ENDPOINTES = [
  { id: "rutas", nombre: "Rutas", icono: "↗" },
  { id: "personas", nombre: "Personas", icono: "◎" },
  { id: "vehiculos", nombre: "Vehículos", icono: "▱" },
  { id: "documentos", nombre: "Documentos", icono: "▤" },
  { id: "asociaciones", nombre: "Asociaciones", icono: "⇄" },
  { id: "usuarios", nombre: "Usuarios", icono: "◉" },
];

export const GRUPOS_ADMINISTRATIVOS = new Set([
  "personas",
  "vehiculos",
  "usuarios",
  "asociaciones",
]);
