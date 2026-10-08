<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consulta de rutas</title>

    <link rel="stylesheet" href="/rutas-frontend/css/styles.css">

    <link
        rel="stylesheet"
        href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
    >
</head>
<body>

    <header>
        <h1>Consulta de rutas</h1>
        <p>Visualiza las paradas de una ruta registrada.</p>
    </header>

    <main>
        <section class="panel-consulta">
            <label for="codigoRuta">Código de ruta</label>

            <div class="fila-consulta">
                <input
                    type="text"
                    id="codigoRuta"
                    placeholder="Ejemplo: RUTA-001"
                >

                <button id="btnConsultar">
                    Consultar ruta
                </button>
            </div>

            <p id="mensaje"></p>
        </section>

        <section class="contenedor-ruta">
            <div class="panel-paradas">
                <h2>Paradas</h2>

                <ol id="listaParadas">
                    <li>Aún no has consultado una ruta.</li>
                </ol>
            </div>

            <div id="mapa"></div>
        </section>
    </main>

    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script src="/rutas-frontend/js/rutas.js"></script>

</body>
</html>