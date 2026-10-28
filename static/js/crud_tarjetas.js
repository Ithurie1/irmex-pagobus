// static/js/crud_tarjetas.js
// Lógica de gestión de tarjetas (Admin) - crud_tarjetas.html

// Función para cargar las tarjetas
async function cargarTarjetas() {
    const tbody = document.getElementById('tablaTarjetas');
    tbody.innerHTML = "<tr><td colspan='5'>Cargando tarjetas...</td></tr>";

    try {
        const response = await fetch('http://127.0.0.1:8000/api/tarjetas');
        const tarjetas = await response.json();
        tbody.innerHTML = "";

        if (tarjetas.length === 0) {
            tbody.innerHTML = "<tr><td colspan='5' style='text-align:center;'>No hay tarjetas registradas</td></tr>";
            return;
        }

        tarjetas.forEach(t => {
            const estatusClass = t.estatus === 'Activa' ? 'badge-activa' : 'badge-inactiva';

            const propietarioStr = t.usuario ? t.usuario.nombre : 'Sin asignar';
            const fila = `
                <tr>
                    <td>${t.numero_tarjeta}</td>
                    <td>${propietarioStr}</td>
                    <td>$ ${t.saldo}.00</td>
                    <td class="${estatusClass}">${t.estatus}</td>
                    <td style="font-weight: bold; color: #2c3e50;">${t.fecha_expiracion}</td>
                    <td>
                        <button class="btn-modificar" onclick="cambiarEstatus(${t.tarjeta_id}, '${t.estatus}')">Bloquear</button>
                        <button style="background-color: #3498db; color: white; border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer;" onclick="ajustarVigencia(${t.tarjeta_id})">🗓️ Ajustar Vigencia</button>
                        <button class="btn-eliminar" onclick="borrarTarjeta(${t.tarjeta_id})">Borrar</button>
                        <button style="background-color: #2ecc71; color: white; border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer; margin-left: 5px;" onclick="recargaEmergencia(${t.tarjeta_id})">💰 Recarga Manual</button>
                    </td>
                </tr>
            `;
            tbody.innerHTML += fila;
        });
    } catch (error) {
        tbody.innerHTML = "<tr><td colspan='5' style='color:red;'>Error al conectar con el servidor</td></tr>";
    }
}

async function ajustarVigencia(id) {
    const diasInput = prompt("¿Cuántos días deseas sumar a la vigencia actual?\n(Escribe un número negativo si quieres restarle días):", "30");

    if (diasInput !== null && diasInput.trim() !== "") {
        const diasInt = parseInt(diasInput);

        if (!isNaN(diasInt)) {
            await fetch(`http://127.0.0.1:8000/api/tarjetas/${id}/vigencia`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ dias: diasInt })
            });
            cargarTarjetas(); // Recarga la tabla para ver la nueva fecha
        } else {
            alert("Por favor, ingresa un número válido.");
        }
    }
}

// LÓGICA DE SUDO MODE: Borrar Tarjeta
async function borrarTarjeta(id) {
    const confirmacion = confirm("⚠️ ATENCIÓN: Estás a punto de eliminar esta tarjeta de la base de datos de forma permanente. ¿Deseas continuar?");
    if (!confirmacion) return;

    const pin = prompt("🔒 ACCIÓN DESTRUCTIVA DETECTADA\n\nIngresa el NIP Maestro de Administrador para borrar el registro:");
    if (!pin) {
        alert("Operación cancelada por seguridad.");
        return;
    }

    try {
        const response = await fetch(`http://127.0.0.1:8000/api/tarjetas/${id}/borrar-seguro`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ pin_seguridad: pin })
        });

        if (response.ok) {
            alert("🗑️ Tarjeta eliminada de la base de datos.");
            cargarTarjetas();
        } else {
            const error = await response.json();
            if (response.status === 400) {
                alert(error.detail);
            } else {
                alert("🚨 ALERTA DE SEGURIDAD: " + error.detail);
            }
        }
    } catch (error) {
        console.error(error);
    }
}

// LÓGICA DE SUDO MODE: Recarga de Emergencia
async function recargaEmergencia(id) {
    // 1. Pedimos el monto
    const montoInput = prompt("💰 Ingresa el monto exacto a recargar a esta tarjeta:");
    if (!montoInput || isNaN(montoInput) || montoInput <= 0) return;

    // 2. EL BLINDAJE: Pedimos el NIP de Autorización
    const pin = prompt("🔒 ACCIÓN SENSIBLE DETECTADA\n\nPara autorizar la inyección de fondos, ingresa el NIP Maestro de Administrador:");
    if (!pin) {
        alert("Operación cancelada por seguridad.");
        return;
    }

    try {
        // 3. Enviamos la petición protegida
        const response = await fetch(`http://127.0.0.1:8000/api/tarjetas/${id}/recarga-manual`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                monto: parseFloat(montoInput),
                pin_seguridad: pin
            })
        });

        if (response.ok) {
            alert("✅ NIP Aceptado. Los fondos han sido inyectados correctamente.");
            cargarTarjetas(); // Recargamos la tabla para ver el nuevo saldo
        } else {
            const error = await response.json();
            alert("🚨 ALERTA DE SEGURIDAD: " + error.detail);
        }
    } catch (error) {
        alert("Error de conexión con el servidor principal.");
    }
}

// LÓGICA DE SUDO MODE: Cambiar Estatus
async function cambiarEstatus(id, estatusActual) {
    const accion = estatusActual === 'Activa' ? 'BLOQUEAR' : 'ACTIVAR';
    const pin = prompt(`🔒 ACCIÓN SENSIBLE\n\nEstás a punto de ${accion} esta tarjeta.\nIngresa el NIP Maestro para autorizar:`);

    if (!pin) {
        alert("Operación cancelada por seguridad.");
        return;
    }

    try {
        const response = await fetch(`http://127.0.0.1:8000/api/tarjetas/${id}/estatus-seguro`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ pin_seguridad: pin })
        });

        if (response.ok) {
            cargarTarjetas(); // Recarga la tabla
        } else {
            const error = await response.json();
            alert("🚨 ALERTA DE SEGURIDAD: " + error.detail);
        }
    } catch (error) {
        console.error(error);
    }
}

// Función para guardar una nueva tarjeta (CREATE)
document.getElementById('formCrearTarjeta').addEventListener('submit', async (e) => {
    e.preventDefault();
    const num = document.getElementById('numTarjeta').value;
    const saldo = document.getElementById('saldoInicial').value;
    const estatus = document.getElementById('estatusTarjeta').value;
    const usuarioId = document.getElementById('selectUsuario').value;
    const msg = document.getElementById('msgFormulario');

    msg.style.color = "blue";
    msg.innerText = "Guardando...";

    try {
        const response = await fetch('http://127.0.0.1:8000/api/tarjetas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                numero_tarjeta: num, 
                saldo: parseInt(saldo), 
                estatus: estatus,
                usuario_id: usuarioId ? parseInt(usuarioId) : null
            })
        });

        if (response.ok) {
            msg.style.color = "green";
            msg.innerText = "Tarjeta guardada con éxito";
            document.getElementById('formCrearTarjeta').reset();
            cargarTarjetas();
        } else {
            const errorData = await response.json();
            msg.innerText = "";
            if (response.status === 400) {
                alert(errorData.detail);
            } else {
                alert("Error: " + errorData.detail);
            }
        }
    } catch (error) {
        msg.style.color = "red";
        msg.innerText = "Error en el servidor";
    }
    setTimeout(() => { msg.innerText = ""; }, 3000);
});

// Cargar select de estudiantes
async function cargarEstudiantes() {
    try {
        const response = await fetch('http://127.0.0.1:8000/api/estudiantes');
        const estudiantes = await response.json();
        const select = document.getElementById('selectUsuario');
        select.innerHTML = '<option value="">-- Sin asignar --</option>';
        estudiantes.forEach(est => {
            select.innerHTML += `<option value="${est.id}">${est.nombre}</option>`;
        });
    } catch (error) {
        console.error("Error cargando estudiantes", error);
    }
}

// Ejecutar la carga al abrir la página
document.addEventListener('DOMContentLoaded', () => {
    cargarEstudiantes();
    cargarTarjetas();
});
