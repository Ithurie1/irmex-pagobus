// static/js/verificar_estatus.js
// Lógica para verificar estatus de estudiante - verificar_estatus.html

let correoActualBuscado = ""; // Guardamos el correo para usarlo en el botón

document.getElementById('formVerificar').addEventListener('submit', async (e) => {
    e.preventDefault();
    const correo = document.getElementById('correoBusqueda').value.trim();
    correoActualBuscado = correo; // Lo guardamos
    const divResultado = document.getElementById('resultadoBusqueda');
    const btnAsignar = document.getElementById('btnAsignarNueva');

    try {
        const response = await fetch(`http://127.0.0.1:8000/api/verificar-estudiante/${correo}`);
        const data = await response.json();

        if (response.ok) {
            document.getElementById('resNombre').innerText = data.nombre;
            document.getElementById('resCorreo').innerText = data.correo;

            const spanEstatus = document.getElementById('resEstatus');
            spanEstatus.innerText = data.estatus_tarjeta;

            // Colores y LÓGICA DEL BOTÓN
            if (data.estatus_tarjeta === "Activa") {
                spanEstatus.style.backgroundColor = "#2ecc71";
                spanEstatus.style.color = "white";
                btnAsignar.style.display = 'none'; // Si está activa, no necesita reposición
            } else {
                spanEstatus.style.backgroundColor = "#e74c3c";
                spanEstatus.style.color = "white";
                btnAsignar.style.display = 'block'; // Mostrar botón si está Inactiva o sin tarjeta
            }

            divResultado.style.display = 'block';
        } else {
            divResultado.style.display = 'none';
            alert(data.detail);
        }
    } catch (error) {
        alert("Error de conexión al buscar.");
    }
});

// Evento para el botón de reposición
document.getElementById('btnAsignarNueva').addEventListener('click', async () => {
    if (confirm("¿Estás seguro de asignar una nueva tarjeta en blanco a este estudiante?")) {
        try {
            const res = await fetch(`http://127.0.0.1:8000/api/asignar-tarjeta/${correoActualBuscado}`, {
                method: 'POST'
            });
            const data = await res.json();

            if (res.ok) {
                alert(data.mensaje);
                // Volvemos a disparar la búsqueda para que se actualice la pantalla
                document.getElementById('formVerificar').dispatchEvent(new Event('submit'));
            } else {
                alert("Error: " + data.detail);
            }
        } catch (error) {
            alert("Error al procesar la reposición.");
        }
    }
});
