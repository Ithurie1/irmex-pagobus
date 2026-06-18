// static/js/estaciones.js
// Lógica de gestión de estaciones - estaciones.html

// Cargar estaciones al iniciar
async function cargarEstaciones() {
    const tbody = document.getElementById('tablaEstaciones');
    tbody.innerHTML = '';

    try {
        const response = await fetch('http://127.0.0.1:8000/api/estaciones');
        const estaciones = await response.json();

        estaciones.forEach(est => {
            const claseBadge = est.estatus === 'Operativa' ? 'badge-operativa' : 'badge-mantenimiento';
            const textoBoton = est.estatus === 'Operativa' ? 'Suspender' : 'Reactivar';
            const colorBoton = est.estatus === 'Operativa' ? '#e74c3c' : '#2ecc71';

            tbody.innerHTML += `
                <tr>
                    <td>#${est.id}</td>
                    <td><strong>${est.nombre}</strong></td>
                    <td>${est.tipo}</td>
                    <td><span class="${claseBadge}">${est.estatus}</span></td>
                    <td>
                        <button onclick="cambiarEstatus(${est.id})" style="background-color: ${colorBoton}; color: white; border: none; padding: 5px 10px; cursor: pointer; border-radius: 4px;">
                            ${textoBoton}
                        </button>
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.error("Error al cargar estaciones", error);
    }
}

// Registrar una nueva estación
document.getElementById('formEstacion').addEventListener('submit', async (e) => {
    e.preventDefault();
    const nombre = document.getElementById('nombreEstacion').value;
    const tipo = document.getElementById('tipoEstacion').value;

    try {
        const res = await fetch('http://127.0.0.1:8000/api/estaciones', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre, tipo })
        });

        if (res.ok) {
            document.getElementById('nombreEstacion').value = '';
            cargarEstaciones(); // Recargar la tabla
        } else {
            const error = await res.json();
            alert(error.detail);
        }
    } catch (error) {
        console.error("Error", error);
    }
});

// Cambiar estatus
async function cambiarEstatus(id) {
    try {
        await fetch(`http://127.0.0.1:8000/api/estaciones/${id}`, { method: 'PUT' });
        cargarEstaciones(); // Recargar la tabla para ver el cambio
    } catch (error) {
        console.error("Error al actualizar", error);
    }
}

cargarEstaciones();
