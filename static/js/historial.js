// static/js/historial.js
// Lógica del historial de actividad - historial.html

async function cargarHistorial() {
    const tbody = document.getElementById('tablaHistorial');
    tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; padding: 20px;">Cargando movimientos...</td></tr>';

    try {
        const token = localStorage.getItem('token');

        // 1. Primero averiguamos qué tarjeta tiene el estudiante
        const resTarjetas = await fetch('http://127.0.0.1:8000/api/mis-tarjetas', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const tarjetas = await resTarjetas.json();

        if (tarjetas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; padding: 20px;">No tienes tarjeta asignada.</td></tr>';
            return;
        }

        // Tomamos el ID de la primera tarjeta
        const tarjetaId = tarjetas[0].tarjeta_id;
        const ultimosDigitos = tarjetas[0].numero_tarjeta.slice(-4);
        // Forzamos el cambio del título de arriba
        const tituloArriba = document.getElementById('tituloTerminacion');
        if (tituloArriba) {
            tituloArriba.innerText = `💳 Tarjeta terminación: **** ${ultimosDigitos}`;
        }
        // 2. Buscamos el historial de ESA tarjeta usando tu ruta original
        const resHistorial = await fetch(`http://127.0.0.1:8000/api/historial/${tarjetaId}`);
        const datos = await resHistorial.json();

        if (datos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; padding: 20px;">No tienes movimientos recientes.</td></tr>';
            return;
        }

        // 3. Dibujamos los datos reales en la tabla
        tbody.innerHTML = '';

        // Usamos .reverse() para que la recarga más nueva salga hasta arriba
        datos.reverse().forEach(mov => {
            const colorMonto = mov.tipo.includes("Recarga") ? "#2ecc71" : "#e74c3c";
            const signo = mov.tipo.includes("Recarga") ? "+" : "-";

            tbody.innerHTML += `
                <tr>
                    <td>${mov.fecha_hora}</td>
                    <td style="font-family: monospace; color: #555;">**** ${ultimosDigitos}</td> <td style="color: #2ecc71; font-weight: bold;">${mov.tipo}</td>
                    <td>${mov.origen} - ${mov.destino}</td>
                    <td style="color: ${colorMonto}; font-weight: bold;">${signo}$${mov.monto.toFixed(2)}</td>
                </tr>
            `;
        });

    } catch (error) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; color:red; padding: 20px;">Error al cargar el historial.</td></tr>';
    }
}

// Ejecutamos la función apenas se abre la página
cargarHistorial();
