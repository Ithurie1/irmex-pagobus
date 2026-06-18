// static/js/dashboard.js
// Lógica del dashboard principal - dashboard.html

let numeroTarjetaReal = "";
let tarjetaOculta = true;

function toggleTarjeta() {
    if (!numeroTarjetaReal) return;
    const display = document.getElementById('numeroTarjetaVisible');
    const btn = document.getElementById('btnOjo');

    if (tarjetaOculta) {
        display.innerText = numeroTarjetaReal.match(/.{1,4}/g).join(' ');
        btn.innerText = "🙈";
    } else {
        display.innerText = `**** **** **** ${numeroTarjetaReal.slice(-4)}`;
        btn.innerText = "👁️";
    }
    tarjetaOculta = !tarjetaOculta;
}

async function cargarDashboard() {
    const token = localStorage.getItem('token');
    const esEstudiante = localStorage.getItem('es_estudiante');

    if (!token) {
        window.location.href = "index.html";
        return;
    }

    // 1. MOSTRAR LA INTERFAZ INSTANTÁNEAMENTE (Sin esperar al backend)
    if (esEstudiante === 'true') {
        document.getElementById('vistaEstudiante').style.display = 'flex';
        document.getElementById('badgeVerificacion').style.display = 'inline-block';
        document.getElementById('btnSoporteFlotante').style.display = 'flex';
    } else {
        document.getElementById('vistaAdmin').style.display = 'flex';
    }

    // 2. PEDIR LOS DATOS AL SERVIDOR EN SEGUNDO PLANO
    if (esEstudiante === 'true') {
        cargarTarjetasBilletera();
    }
    cargarMiniPerfil();
}

async function cargarTarjetasBilletera() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://127.0.0.1:8000/api/mis-tarjetas', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const tarjetas = await response.json();
            const badgeSuperior = document.getElementById('badgeVerificacion');
            const btnRecarga = document.getElementById('btnRecarga');

            if (tarjetas.length === 0) {
                document.getElementById('saldoMostrado').innerText = "$ 0.00";
                document.getElementById('estatusTarjetaOjo').innerText = "Estatus: Sin tarjeta asignada";
                badgeSuperior.innerText = "Sin tarjeta ⚠️";
                badgeSuperior.style.backgroundColor = "#e74c3c";
                btnRecarga.style.display = 'none';
                return;
            }

            const tarjeta = tarjetas[0];

            if (tarjeta.requiere_renovacion_urgente && !document.querySelector('.alerta-renovacion')) {
                const contenedorAlerta = document.createElement('div');
                contenedorAlerta.className = 'alerta-renovacion';
                contenedorAlerta.innerHTML = `<div style="background-color: #e74c3c; color: white; padding: 15px; border-radius: 10px; margin-bottom: 20px; font-weight: bold; text-align: center; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">⚠️ ¡Atención! Tu beneficio de estudiante está próximo a vencer. Por favor, ve al módulo "Renovar Tarjeta" antes del ${tarjeta.fecha_expiracion_formateada || 'la fecha límite'}.</div>`;
                const appContainer = document.querySelector('.app-container');
                if (appContainer) {
                    appContainer.insertBefore(contenedorAlerta, appContainer.childNodes[2]);
                }
            }

            localStorage.setItem('tarjeta_seleccionada_id', tarjeta.tarjeta_id);
            numeroTarjetaReal = tarjeta.numero_tarjeta;
            document.getElementById('numeroTarjetaVisible').innerText = `**** **** **** ${numeroTarjetaReal.slice(-4)}`;
            document.getElementById('estatusTarjetaOjo').innerText = `Estatus: ${tarjeta.estatus}`;

            const saldoReal = tarjeta.saldo;
            const elementoSaldo = document.getElementById('saldoMostrado');
            elementoSaldo.innerText = `$ ${saldoReal.toFixed(2)}`;

            if (saldoReal < 20.00) {
                elementoSaldo.style.color = "#e74c3c";
            } else {
                elementoSaldo.style.color = "#2ecc71";
            }

            if (tarjeta.estatus === "Activa") {
                badgeSuperior.innerText = "Verificado ✔️";
                badgeSuperior.style.backgroundColor = "#2ecc71";
                badgeSuperior.style.border = "2px solid #2ecc71";
                btnRecarga.style.display = 'block';
            } else {
                badgeSuperior.innerText = "Sin verificar ⚠️";
                badgeSuperior.style.backgroundColor = "#f39c12";
                badgeSuperior.style.border = "2px solid white";
                btnRecarga.style.display = 'none';
            }
        }
    } catch (error) {
        console.error("Error cargando la billetera:", error);
    }
}

async function cargarMiniPerfil() {
    const correoUsuario = localStorage.getItem('correo');
    if (!correoUsuario) return;

    try {
        const res = await fetch(`http://127.0.0.1:8000/api/perfil/${correoUsuario}`);
        if (res.ok) {
            const data = await res.json();
            if (data.foto_perfil) {
                document.getElementById('fotoPerfilHeader').src = data.foto_perfil;
            }
        }
    } catch (error) {
        console.error("Error al cargar la foto:", error);
    }
}

function cerrarSesion() {
    localStorage.clear();
    window.location.href = "index.html";
}

// ==========================================
// LÓGICA DE SOPORTE Y REPORTES (ESTUDIANTE)
// ==========================================
function abrirModalSoporte() { document.getElementById('modalSoporte').style.display = 'flex'; }
function cerrarModalSoporte() { document.getElementById('modalSoporte').style.display = 'none'; document.getElementById('formSoporte').reset(); }

async function enviarTicket(event) {
    event.preventDefault();
    const asunto = document.getElementById('asuntoSoporte').value;
    const mensaje = document.getElementById('mensajeSoporte').value;
    const token = localStorage.getItem('token');

    if (!token) return;

    try {
        const response = await fetch('http://127.0.0.1:8000/api/soporte/ticket', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ asunto: asunto, mensaje: mensaje })
        });

        if (response.ok) {
            alert("✅ Tu reporte ha sido enviado. Revisa 'Mis Reportes' para ver actualizaciones.");
            cerrarModalSoporte();
            if (document.getElementById('panelMisReportes').style.display === 'block') {
                cargarMisReportes();
            }
        } else {
            alert("Error al enviar el reporte.");
        }
    } catch (error) { alert("Error de conexión."); }
}

function mostrarPanelReportes() {
    document.getElementById('panelMisReportes').style.display = 'block';
    cargarMisReportes();
}

function cerrarPanelReportes() {
    document.getElementById('panelMisReportes').style.display = 'none';
}

async function cargarMisReportes() {
    const contenedor = document.getElementById('contenedorMisReportes');
    const token = localStorage.getItem('token');
    if (!token) return;

    contenedor.innerHTML = "<p>Cargando reportes...</p>";
    try {
        const response = await fetch('http://127.0.0.1:8000/api/soporte/mis-tickets', { headers: { 'Authorization': `Bearer ${token}` } });
        if (response.ok) {
            const tickets = await response.json();
            contenedor.innerHTML = "";
            if (tickets.length === 0) {
                contenedor.innerHTML = "<p style='color: gray; font-style: italic;'>No tienes reportes activos.</p>";
                return;
            }

            tickets.forEach(ticket => {
                const estadoVisual = ticket.revisado
                    ? `<span style="background: #2ecc71; color: white; padding: 3px 8px; border-radius: 12px; font-size: 12px; font-weight: bold;">Resuelto ✔️</span>`
                    : `<span style="background: #f1c40f; color: #2c3e50; padding: 3px 8px; border-radius: 12px; font-size: 12px; font-weight: bold;">En revisión ⏳</span>`;

                const cajaRespuesta = ticket.respuesta
                    ? `<div style="background: #e8f8f5; border-left: 4px solid #2ecc71; padding: 10px; margin-top: 10px; border-radius: 4px;">
                         <strong style="color: #27ae60; font-size: 13px;">Respuesta de Administración:</strong>
                         <p style="margin: 5px 0 0 0; font-size: 14px; color: #2c3e50;">${ticket.respuesta}</p>
                       </div>` : '';

                contenedor.innerHTML += `
                    <div style="border: 1px solid #ddd; border-radius: 8px; padding: 15px; margin-bottom: 15px; background: #fafafa; text-align: left;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                            <strong style="color: #2c3e50; font-size: 16px;">${ticket.asunto}</strong> ${estadoVisual}
                        </div>
                        <p style="color: #7f8c8d; font-size: 14px; margin: 0;">${ticket.mensaje}</p>
                        ${cajaRespuesta}
                    </div>
                `;
            });
        }
    } catch (error) { contenedor.innerHTML = "<p style='color: red;'>Error al cargar los reportes.</p>"; }
}

document.addEventListener('DOMContentLoaded', cargarDashboard);
