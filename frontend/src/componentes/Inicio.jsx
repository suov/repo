import EncabezadoPortal from "./EncabezadoPortal.jsx";

export default function Inicio({ alElegir }) {
  return (
    <main className="welcome-shell">

      <EncabezadoPortal mostrarInicio />

      <section className="welcome-content">

        <p className="eyebrow">
          BIENVENIDO
        </p>

        <h1>
          ¿Cómo desea continuar?
        </h1>

        <p className="page-subtitle">
          Elija una opción para acceder a los servicios de la aplicación.
        </p>

        <div className="access-options">

          <button
            className="access-card"
            type="button"
            onClick={() => alElegir("login")}
          >
            <span className="access-icon" aria-hidden="true">
              ↪
            </span>

            <span className="eyebrow">
              ACCESO ADMINISTRADOR
            </span>

            <strong>
              Iniciar sesión
            </strong>

            <span>
              Guarde sus credenciales y consulte las rutas y los demás servicios autorizados.
            </span>

            <span className="access-link">
              Ingresar con mi cuenta <b aria-hidden="true">→</b>
            </span>
          </button>

          <button
            className="access-card"
            type="button"
            onClick={() => alElegir("publico")}
          >
            <span className="access-icon access-icon-light" aria-hidden="true">
              ◎
            </span>

            <span className="eyebrow">
              ACCESO LIBRE
            </span>

            <strong>
              Acceder a consultas públicas
            </strong>

            <span>
              Consulte datos disponibles públicamente de personas, vehículos y conductores.
            </span>

            <span className="access-link">
              Ver consultas disponibles <b aria-hidden="true">→</b>
            </span>
          </button>

        </div>
      </section>

      <footer className="portal-footer">
        El mejor Cipas+ · Inicio
      </footer>

    </main>
  );
}