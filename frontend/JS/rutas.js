const mapa = L.map("mapa").setView([4.4389, -75.2322], 13);

L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "&copy; OpenStreetMap"
}).addTo(mapa);

const btnConsultar = document.getElementById("btnConsultar");
const inputCodigoRuta = document.getElementById("codigoRuta");
const mensaje = document.getElementById("mensaje");

btnConsultar.addEventListener("click", () => {
    const codigoRuta = inputCodigoRuta.value.trim();

    if (codigoRuta === "") {
        mensaje.textContent = "Ingresa un código de ruta.";
        return;
    }

    mensaje.textContent =
        "La conexión con Spring Boot se agregará en el siguiente paso.";
});