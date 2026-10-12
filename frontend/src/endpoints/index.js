import { ENDPOINTES_PRIVADOS } from "./privados.js";
import { ENDPOINTES_PUBLICOS } from "./publicos.js";

export { GRUPOS_ADMINISTRATIVOS, GRUPOS_ENDPOINTES } from "./grupos.js";
export { ENDPOINTES_PRIVADOS, ENDPOINTES_PUBLICOS };
export const ENDPOINTES = [...ENDPOINTES_PRIVADOS, ...ENDPOINTES_PUBLICOS];

const idsEndpointes = ENDPOINTES.map((endpoint) => endpoint.id);

if (new Set(idsEndpointes).size !== idsEndpointes.length) {
  throw new Error("ERROR: El catálogo de servicios contiene identificadores repetidos.");
}
