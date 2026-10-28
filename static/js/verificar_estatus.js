// static/js/verificar_estatus.js

document.addEventListener('DOMContentLoaded', () => {
    cargarUsuarios();

    // Filtro de búsqueda en tiempo real
    const buscador = document.getElementById('buscador');
    buscador.addEventListener('input', function() {
        const query = this.value.toLowerCase();
        const filas = document.querySelectorAll('#tablaUsuarios tr');
        
        filas.forEach(fila => {
            const nombre = fila.children[1].innerText.toLowerCase();
            const correo = fila.children[2].innerText.toLowerCase();
            
            if (nombre.includes(query) || correo.includes(query)) {
                fila.style.display = '';
            } else {
                fila.style.display = 'none';
            }
        });
    });
});

async function cargarUsuarios() {
    const tbody = document.getElementById('tablaUsuarios');
    tbody.innerHTML = "<tr><td colspan='5' style='text-align:center;'>Cargando estudiantes...</td></tr>";

    try {
        const response = await fetch('http://127.0.0.1:8000/api/usuarios_completos');
        const usuarios = await response.json();
        
        tbody.innerHTML = "";

        if (usuarios.length === 0) {
            tbody.innerHTML = "<tr><td colspan='6' style='text-align:center;'>No hay estudiantes registrados</td></tr>";
            return;
        }

        usuarios.forEach(usuario => {
            let estatusClass = '';
            if (usuario.estatus_tarjeta === 'Activa') estatusClass = 'badge-activa';
            else if (usuario.estatus_tarjeta === 'Bloqueada') estatusClass = 'badge-inactiva';

            const badgeHtml = estatusClass ? `<span class="${estatusClass}">${usuario.estatus_tarjeta}</span>` : usuario.estatus_tarjeta;
            
            const userRol = usuario.rol || 'estudiante';
            const rolHtml = userRol.toUpperCase();

            const fila = `
                <tr>
                    <td>${usuario.id}</td>
                    <td>${usuario.nombre}</td>
                    <td>${usuario.correo}</td>
                    <td>${badgeHtml}</td>
                    <td>${rolHtml}</td>
                    <td>
                        <button class="btn-eliminar" onclick="darDeBaja(${usuario.id})">Dar de baja</button>
                    </td>
                </tr>
            `;
            tbody.innerHTML += fila;
        });
    } catch (error) {
        console.error("Error fetching usuarios:", error);
        tbody.innerHTML = "<tr><td colspan='6' style='color:red; text-align:center;'>Error al cargar datos del servidor</td></tr>";
    }
}

async function darDeBaja(id) {
    const confirmacion = confirm("⚠️ ATENCIÓN: Estás a punto de eliminar a este estudiante y todas sus tarjetas, recargas e historial de manera permanente.\n\n¿Estás seguro de que deseas continuar?");
    if (!confirmacion) return;

    try {
        const response = await fetch(`http://127.0.0.1:8000/api/usuarios/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert("✅ Estudiante dado de baja y eliminado de la base de datos con éxito.");
            cargarUsuarios();
        } else {
            const error = await response.json();
            alert("🚨 ERROR: " + error.detail);
        }
    } catch (error) {
        console.error("Error al eliminar usuario:", error);
        alert("🚨 ERROR: No se pudo conectar con el servidor.");
    }
}

