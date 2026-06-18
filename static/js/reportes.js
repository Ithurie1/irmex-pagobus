// static/js/reportes.js
// Lógica de reportes y auditoría (Admin) - reportes.html

// VARIABLE PARA GUARDAR LA GRÁFICA
let myChart = null;

async function cargarReportes() {
    try {
        const response = await fetch('http://127.0.0.1:8000/api/reportes/general');
        const data = await response.json();

        const contenedor = document.getElementById('contenedorMetricas');

        // Pintar Cuadros
        contenedor.innerHTML = `
            <div class="card-metrica">
                <h3>Total Estudiantes</h3>
                <p>🎓 ${data.total_estudiantes}</p>
            </div>
            <div class="card-metrica">
                <h3>Tarjetas Activas</h3>
                <p style="color: #2ecc71;">✔️ ${data.tarjetas_activas}</p>
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

        // Pintar Gráfica
        dibujarGrafica(data.total_ingresos, data.total_gastos);

    } catch (error) {
        document.getElementById('contenedorMetricas').innerHTML = "<p style='color:red;'>Error al cargar métricas.</p>";
    }
}

function dibujarGrafica(ingresos, gastos) {
    const ctx = document.getElementById('graficaFinanzas').getContext('2d');

    // Destruir gráfica vieja si existe (evita errores al recargar)
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
                    backgroundColor: 'rgba(46, 204, 113, 0.7)', // Verde
                    borderColor: '#27ae60',
                    borderWidth: 1,
                    borderRadius: 5
                },
                {
                    label: 'Gastos (Cobros Validador)',
                    data: [gastos],
                    backgroundColor: 'rgba(231, 76, 60, 0.7)', // Rojo
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
                            return '$' + value; // Poner el signo de pesos en el eje Y
                        }
                    }
                }
            }
        }
    });
}

async function cargarLogs() {
    try {
        const res = await fetch('http://127.0.0.1:8000/api/logs');
        if (res.ok) {
            const logs = await res.json();
            const tbody = document.getElementById('tablaLogs');

            if (logs.length === 0) {
                tbody.innerHTML = '<tr><td colspan="2" style="padding: 15px; text-align: center; color: #7f8c8d;">No hay accesos registrados aún.</td></tr>';
                return;
            }

            tbody.innerHTML = '';
            logs.forEach(log => {
                tbody.innerHTML += `
                    <tr style="border-bottom: 1px solid #eee;">
                        <td style="padding: 12px 15px; color: #34495e; font-weight: bold;">${log.correo}</td>
                        <td style="padding: 12px 15px; color: #7f8c8d; font-family: monospace;">${log.fecha_hora}</td>
                    </tr>
                `;
            });
        }
    } catch (error) {
        console.error("Error al cargar los logs:", error);
        document.getElementById('tablaLogs').innerHTML = '<tr><td colspan="2" style="padding: 15px; text-align: center; color: red;">Error al cargar bitácora.</td></tr>';
    }
}

// Arrancar todo al abrir la página
cargarReportes();
cargarLogs();
