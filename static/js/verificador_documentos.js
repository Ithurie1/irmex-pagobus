// static/js/verificador_documentos.js
// Lógica para verificar documentos de renovación (Admin) - verificador_documentos.html

async function cargarSolicitudes() {
    const tbody = document.getElementById('tablaRenovaciones');
    tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Cargando solicitudes...</td></tr>';

    try {
        const response = await fetch('http://127.0.0.1:8000/api/renovaciones/pendientes');
        const datos = await response.json();

        if (datos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">No hay solicitudes pendientes.</td></tr>';
            return;
        }

        tbody.innerHTML = '';
        datos.forEach(req => {
            // Ruta a la carpeta estática de documentos seguros montada en FastAPI
            const urlDocumento = `http://127.0.0.1:8000/documentos_seguros/${req.nombre_archivo}`;

            tbody.innerHTML += `
                <tr>
                    <td>#${req.id_renovacion}</td>
                    <td><strong>${req.nombre_usuario}</strong></td>
                    <td>${req.correo}</td>
                    <td>${req.fecha}</td>
                    <td>
                        <a href="${urlDocumento}" target="_blank" class="btn-ver">👁️ Ver Archivo</a>
                    </td>
                    <td>
                        <button class="btn-aprobar" onclick="procesarSolicitud(${req.id_renovacion}, 'Aprobada')">Aprobar</button>
                        <button class="btn-rechazar" onclick="procesarSolicitud(${req.id_renovacion}, 'Rechazada')">Rechazar</button>
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color:red;">Error al cargar las solicitudes.</td></tr>';
    }
}

async function procesarSolicitud(id, estatus) {
    if (!confirm(`¿Estás seguro de marcar esta solicitud como ${estatus}?`)) return;

    try {
        const response = await fetch(`http://127.0.0.1:8000/api/renovaciones/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ estatus: estatus })
        });

        const data = await response.json();
        if (response.ok) {
            alert(data.mensaje);
            cargarSolicitudes(); // Recargar la tabla
        } else {
            alert("Error: " + data.detail);
        }
    } catch (error) {
        alert("Error de conexión al procesar.");
    }
}

// Cargar al iniciar la página
cargarSolicitudes();
