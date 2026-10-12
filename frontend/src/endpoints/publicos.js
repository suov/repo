export const ENDPOINTES_PUBLICOS = [

  /* Personas */
  {
    id: "personas-total",
    grupo: "personas",
    titulo: "Total de personas por tipo",
    descripcion: "Consulte cuántas personas hay de cada tipo.",
    metodo: "GET",
    ruta: "/persona/total-por-tipo",
    acceso: "publico",
  },

  /* Vehiculos */
  {
    id: "vehiculo-por-placa",
    grupo: "vehiculos",
    titulo: "Buscar vehículo por placa",
    descripcion: "Consulte un vehículo por su placa.",
    metodo: "GET",
    ruta: "/api/vehiculos/placa/:placa",
    parametrosRuta: [{ nombre: "placa", etiqueta: "Placa", ejemplo: "ABC123" }],
    acceso: "publico",
  },
  {
    id: "vehiculos-por-estado-documento",
    grupo: "vehiculos",
    titulo: "Buscar vehículos por estado documental",
    descripcion: "Estados de ejemplo: HABILITADO, VENCIDO o EN_VERIFICACION.",
    metodo: "GET",
    ruta: "/api/vehiculos/estadoDocumento/:estadoDocumento",
    parametrosRuta: [{ nombre: "estadoDocumento", etiqueta: "Estado del documento", ejemplo: "VENCIDO" }],
    acceso: "publico",
  },
  {
    id: "vehiculos-por-vencer",
    grupo: "vehiculos",
    titulo: "Consultar documentos próximos a vencer",
    descripcion: "Busque documentos que vencen dentro del número de días indicado.",
    metodo: "GET",
    ruta: "/api/vehiculos/por-vencer",
    parametrosConsulta: [{ nombre: "dias", etiqueta: "Días", ejemplo: "30", valorInicial: "30" }],
    acceso: "publico",
  },
  {
    id: "vehiculos-vencidos",
    grupo: "vehiculos",
    titulo: "Consultar vehículos con documentos vencidos",
    descripcion: "Busque vehículos con documentos vencidos.",
    metodo: "GET",
    ruta: "/api/vehiculos/vencidos",
    acceso: "publico",
  },

  /* Vehiculo Persona */
  {
    id: "conductores-habilitados",
    grupo: "asociaciones",
    titulo: "Conductores que pueden operar",
    descripcion: "Consulte los conductores habilitados para operar.",
    metodo: "GET",
    ruta: "/api/vehiculo-persona/conductores/pueden-operar",
    acceso: "publico",
  },
];
