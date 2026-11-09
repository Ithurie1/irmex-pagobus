// static/js/reportes.js
// Lógica de reportes y auditoría (Admin) - reportes.html

// Helper de autorización JWT
const authHeaders = () => ({ "Authorization": "Bearer " + localStorage.getItem("token") });

// VARIABLE PARA GUARDAR LA GRÁFICA
let myChart = null;

async function cargarReportes() {
    try {
        const response = await fetch('http://127.0.0.1:8000/api/reportes/general', {
            headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
        });
        const data = await response.json();

        const contenedor = document.getElementById('contenedorMetricas');

        contenedor.innerHTML = `
            <div class="card-metrica">
                <h3>Total Estudiantes</h3>
                <p>👨‍🎓 ${data.total_estudiantes}</p>
            </div>
            <div class="card-metrica">
                <h3>Tarjetas Activas</h3>
                <p style="color: #2ecc71;">✅ ${data.tarjetas_activas}</p>
            </div>
            <div class="card-metrica" style="background-color: rgba(46, 204, 113, 0.1); border-color: #2ecc71;">
                <h3 style="color: #27ae60;">Ingresos Totales</h3>
                <p class="dinero">+$${data.total_ingresos.toFixed(2)}</p>
            </div>
            <div class="card-metrica" style="background-color: rgba(231, 76, 60, 0.1); border-color: #e74c3c;">
                <h3 style="color: #c0392b;">Pasajes Cobrados</h3>
                <p style="color: #e74c3c; margin: 10px 0 0 0; font-size: 28px; font-weight: bold;">-$${data.total_gastos.toFixed(2)}</p>
            </div>
        `;

        dibujarGrafica(data.total_ingresos, data.total_gastos);

    } catch (error) {
        document.getElementById('contenedorMetricas').innerHTML = "<p style='color:red;'>Error al cargar métricas.</p>";
    }
}

function dibujarGrafica(ingresos, gastos) {
    const ctx = document.getElementById('graficaFinanzas').getContext('2d');

    if (myChart) {
        myChart.destroy();
    }

    myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Balance Monetario IrMex'],
            datasets: [
                {
                    label: 'Ingresos (Recargas)',
                    data: [ingresos],
                    backgroundColor: 'rgba(46, 204, 113, 0.7)',
                    borderColor: '#27ae60',
                    borderWidth: 1,
                    borderRadius: 5
                },
                {
                    label: 'Gastos (Cobros Validador)',
                    data: [gastos],
                    backgroundColor: 'rgba(231, 76, 60, 0.7)',
                    borderColor: '#c0392b',
                    borderWidth: 1,
                    borderRadius: 5
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function (value) {
                            return '$' + value;
                        }
                    }
                }
            }
        }
    });
}

async function cargarLogs() {
    try {
        const res = await fetch('http://127.0.0.1:8000/api/logs', {
            headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
        });
        if (res.ok) {
            const logs = await res.json();
            const tbody = document.getElementById('tablaLogs');

            if (logs.length === 0) {
                tbody.innerHTML = '<tr><td colspan="3" style="padding: 15px; text-align: center; color: #7f8c8d;">No hay accesos registrados aún.</td></tr>';
                return;
            }

            tbody.innerHTML = '';
            logs.forEach(log => {
                tbody.innerHTML += `
                    <tr style="border-bottom: 1px solid #eee;">
                        <td style="padding: 12px 15px; color: #34495e; font-weight: bold;">${log.correo}</td>
                        <td style="padding: 12px 15px;">${log.rol || 'N/A'}</td>
                        <td style="padding: 12px 15px; color: #7f8c8d; font-family: monospace;">${log.fecha_hora || log.fecha || ''}</td>
                    </tr>
                `;
            });
        }
    } catch (error) {
        console.error("Error al cargar los logs:", error);
        document.getElementById('tablaLogs').innerHTML = '<tr><td colspan="3" style="padding: 15px; text-align: center; color: red;">Error al cargar bitácora.</td></tr>';
    }
}

// ==========================================
// DESCARGAS SEGURAS CON JWT (BLOB + DOM LINK)
// ==========================================

async function exportarFinanzas(event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }

    const filtroSelect = document.getElementById('filtroVentas');
    const filtro = filtroSelect ? filtroSelect.value : 'total';

    try {
        const response = await fetch(`http://127.0.0.1:8000/api/admin/finanzas/exportar?filtro=${filtro}`, {
            headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
        });

        if (!response.ok) {
            if (response.status === 401) {
                alert("Sesión expirada o no autorizada (Error 401). Inicia sesión nuevamente.");
            } else if (response.status === 403) {
                alert("Acceso denegado (Error 403). Se requieren permisos de administrador.");
            } else {
                alert("Error al descargar reporte de ventas (Código: " + response.status + ").");
            }
            return;
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `ventas_irmex_${filtro}.csv`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error("Error al exportar finanzas:", error);
        alert("Error de conexión al descargar el reporte de ventas.");
    }
}

async function exportarAuditoria(event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }

    try {
        const response = await fetch('http://127.0.0.1:8000/api/admin/auditoria/exportar', {
            headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
        });

        if (!response.ok) {
            if (response.status === 401) {
                alert("Sesión expirada o no autorizada (Error 401). Inicia sesión nuevamente.");
            } else if (response.status === 403) {
                alert("Acceso denegado (Error 403). Se requieren permisos de administrador.");
            } else {
                alert("Error al descargar auditoría (Código: " + response.status + ").");
            }
            return;
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = "auditoria_irmex.csv";
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error("Error al exportar auditoría:", error);
        alert("Error de conexión al descargar la auditoría.");
    }
}

function actualizarEnlaceVentas() {
    // Sincronización conservada para compatibilidad con el evento onchange del select
}

// Vincular eventos de descarga segura al DOM
function inicializarDescargas() {
    const btnVentas = document.getElementById('btnExportarVentas');
    if (btnVentas) {
        btnVentas.removeAttribute('target');
        btnVentas.href = 'javascript:void(0);';
        btnVentas.onclick = (e) => exportarFinanzas(e);
    }

    const btnAuditoria = document.querySelector('a[href*="/api/admin/auditoria/exportar"]');
    if (btnAuditoria) {
        btnAuditoria.removeAttribute('target');
        btnAuditoria.href = 'javascript:void(0);';
        btnAuditoria.onclick = (e) => exportarAuditoria(e);
    }
}

// Exponer funciones globales
window.exportarFinanzas = exportarFinanzas;
window.exportarAuditoria = exportarAuditoria;
window.actualizarEnlaceVentas = actualizarEnlaceVentas;

// Arrancar todo al abrir la página
cargarReportes();
cargarLogs();
document.addEventListener('DOMContentLoaded', inicializarDescargas);
inicializarDescargas();
