document.addEventListener('DOMContentLoaded', () => {
    cargarUsuarios();

    // Filtro de búsqueda en tiempo real
    const buscador = document.getElementById('buscador');
    buscador.addEventListener('input', function() {
        const query = this.value.toLowerCase();
        const filas = document.querySelectorAll('#tablaPrivilegios tbody tr');
        
        filas.forEach(fila => {
            const nombre = fila.children[0].innerText.toLowerCase();
            const correo = fila.children[1].innerText.toLowerCase();
            
            if (nombre.includes(query) || correo.includes(query)) {
                fila.style.display = '';
            } else {
                fila.style.display = 'none';
            }
        });
    });
});

async function cargarUsuarios() {
    const tbody = document.querySelector('#tablaPrivilegios tbody');
    tbody.innerHTML = "<tr><td colspan='4' style='text-align:center;'>Cargando usuarios...</td></tr>";

    try {
        const response = await fetch('http://127.0.0.1:8000/api/usuarios_completos');
        const usuarios = await response.json();
        
        tbody.innerHTML = "";

        if (usuarios.length === 0) {
            tbody.innerHTML = "<tr><td colspan='4' style='text-align:center;'>No hay usuarios registrados</td></tr>";
            return;
        }

        usuarios.forEach(u => {
            const userRol = u.rol || 'estudiante';
            const rolActual = userRol.toUpperCase();

            // Celda de asignación interactiva (el usuario ya está verificado como superadmin en el HTML)
            const selectRol = `
                <select class="form-select select-rol form-select-sm" data-usuario-id="${u.id}" style="width: 130px;">
                    <option value="estudiante" ${userRol === 'estudiante' ? 'selected' : ''}>Estudiante</option>
                    <option value="admin" ${userRol === 'admin' ? 'selected' : ''}>Administrador</option>
                    <option value="superadmin" ${userRol === 'superadmin' ? 'selected' : ''}>Super Admin</option>
                </select>
            `;

            const fila = `
                <tr style="border-bottom: 1px solid #eee;">
                    <td style="padding: 12px 15px;">${u.nombre}</td>
                    <td style="padding: 12px 15px;">${u.correo}</td>
                    <td style="padding: 12px 15px; font-weight: bold; color: #34495e;">${rolActual}</td>
                    <td style="padding: 12px 15px;">${selectRol}</td>
                </tr>
            `;
            tbody.innerHTML += fila;
        });

        // Adjuntar evento change a los selects recién inyectados
        document.querySelectorAll('.select-rol').forEach(select => {
            // Guardar el valor original como data-attribute en el elemento en caso de revertir
            select.dataset.originalRol = select.value;
            select.addEventListener('change', function() {
                cambiarRol(this.dataset.usuarioId, this, this.dataset.originalRol);
            });
        });

    } catch (error) {
        tbody.innerHTML = "<tr><td colspan='4' style='color:red; text-align:center;'>Error al cargar datos del servidor</td></tr>";
    }
}

async function cambiarRol(usuarioId, selectElement, currentRol) {
    const nuevoRol = selectElement.value;
    const token = localStorage.getItem('token');
    
    let mensajePrompt = "Ingrese el Código Maestro para autorizar este cambio de privilegios:";
    if (currentRol === 'superadmin' || nuevoRol === 'superadmin') {
        mensajePrompt = "⚠️ ALERTA DE SEGURIDAD MÁXIMA ⚠️\nEstá a punto de alterar privilegios de Super Administrador.\nIngrese el SUPER NIP RAIZ:";
    }

    const nipProporcionado = prompt(mensajePrompt);

    if (!nipProporcionado) {
        selectElement.value = currentRol; // revert
        return;
    }

    try {
        const response = await fetch(`http://127.0.0.1:8000/api/usuarios/${usuarioId}/rol`, {
            method: 'PUT',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                nuevo_rol: nuevoRol,
                nip_proporcionado: nipProporcionado
            })
        });

        if (response.ok) {
            alert(`✅ Privilegios actualizados correctamente a: ${nuevoRol.toUpperCase()}`);
            cargarUsuarios(); // refresh list to get latest roles
        } else {
            const error = await response.json();
            alert(`🚨 ERROR: ${error.detail}`);
            selectElement.value = currentRol; // revert on error
        }
    } catch (error) {
        console.error("Error al cambiar rol:", error);
        alert("🚨 ERROR: No se pudo conectar con el servidor.");
        selectElement.value = currentRol;
    }
}
