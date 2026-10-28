// static/js/buzon_admin.js

let ticketsGlobales = [];
let carpetaActual = 'bandeja';

window.onload = () => cargarTicketsBuzon();

async function cargarTicketsBuzon() {
    const contenedor = document.getElementById('listaTickets');
    contenedor.innerHTML = "<p style='padding: 20px; color: gray;'>Cargando mensajes...</p>";

    try {
        const response = await fetch('http://127.0.0.1:8000/api/admin/tickets');
        ticketsGlobales = await response.json();
        renderizarLista();
    } catch (error) {
        contenedor.innerHTML = "<p style='color: red; padding: 20px;'>Error conectando con el servidor.</p>";
    }
}

function cambiarCarpeta(carpeta, boton) {
    carpetaActual = carpeta;
    
    // Actualizar estilos de los botones del sidebar
    const botones = document.querySelectorAll('.buzon-sidebar button');
    botones.forEach(b => b.classList.remove('active'));
    boton.classList.add('active');

    renderizarLista();
}

function renderizarLista() {
    const contenedor = document.getElementById('listaTickets');
    let tickets = [];

    // Filtrado por carpeta sin usar estatus
    if (carpetaActual === 'bandeja') {
        tickets = ticketsGlobales.filter(t => t.revisado === false);
    } else if (carpetaActual === 'destacados') {
        tickets = ticketsGlobales.filter(t => t.es_importante === true);
    } else if (carpetaActual === 'leidos') {
        tickets = ticketsGlobales.filter(t => t.revisado === true);
    }

    contenedor.innerHTML = "";
    if (tickets.length === 0) {
        contenedor.innerHTML = "<p style='color: gray; text-align: center; margin-top: 20px; padding: 20px;'>No hay mensajes en esta carpeta.</p>";
        return;
    }

    tickets.forEach(ticket => {
        const leidoClass = ticket.revisado ? "leido" : "";
        const estrellaIcono = ticket.es_importante ? "⭐" : "☆";
        const estrellaColor = ticket.es_importante ? "#f2a60c" : "#dadce0";
        const checkboxIcon = ticket.revisado ? "☑️" : "☐";
        
        let fecha = "";
        if (ticket.fecha_creacion) {
            fecha = new Date(ticket.fecha_creacion).toLocaleDateString();
        }
        
        const nombreMostrar = ticket.remitente;
        const extractoMensaje = ticket.mensaje.length > 60 ? ticket.mensaje.substring(0, 60) + "..." : ticket.mensaje;
        
        const etiquetaRespuesta = ticket.respuesta ? ' <span style="font-size:10px; background:#f2f6fc; border:1px solid #dadce0; color:#5f6368; padding:2px 4px; border-radius:4px;">Respondido</span>' : '';

        console.log(ticket);

        const tarjetaTicket = `
            <div class="ticket-row-gmail ${leidoClass}" id="ticket-row-${ticket.id}" onclick="abrirModalLectura(${ticket.id})">
                <div class="ticket-icons">
                    <span onclick="alternarLeido(event, ${ticket.id}, ${!ticket.revisado})" style="cursor: pointer;" title="Marcar como ${ticket.revisado ? 'no leído' : 'leído'}">${checkboxIcon}</span>
                    <span onclick="alternarImportancia(event, ${ticket.id}, ${!ticket.es_importante})" style="cursor: pointer; font-size: 18px; color: ${estrellaColor};" title="Destacar">${estrellaIcono}</span>
                </div>
                
                <div class="ticket-sender">
                    ${nombreMostrar}
                </div>
                
                <div class="ticket-subject">
                    ${ticket.asunto}${etiquetaRespuesta} <span class="ticket-snippet">- ${extractoMensaje}</span>
                </div>
                
                <div class="ticket-date">${fecha}</div>
                
                <div class="ticket-action" onclick="eliminarTicket(event, ${ticket.id})" title="Eliminar">🗑️</div>
            </div>
        `;
        contenedor.innerHTML += tarjetaTicket;
    });
}

async function alternarImportancia(event, id, nuevoEstado) {
    event.stopPropagation();
    await fetch(`http://127.0.0.1:8000/api/admin/tickets/${id}/acciones?importante=${nuevoEstado}`, { method: 'PUT' });
    
    // Update local state to avoid full reload if preferred, but reload is safer
    const t = ticketsGlobales.find(x => x.id === id);
    if(t) t.es_importante = nuevoEstado;
    renderizarLista();
}

async function alternarLeido(event, id, nuevoEstado) {
    event.stopPropagation();
    await fetch(`http://127.0.0.1:8000/api/admin/tickets/${id}/acciones?revisado=${nuevoEstado}`, { method: 'PUT' });
    
    const t = ticketsGlobales.find(x => x.id === id);
    if(t) t.revisado = nuevoEstado;
    renderizarLista();
}

async function eliminarTicket(event, id) {
    event.stopPropagation();
    
    const seguro = confirm("¿Estás seguro de eliminar este ticket permanentemente?");
    if (!seguro) return;

    try {
        const response = await fetch(`http://127.0.0.1:8000/api/admin/tickets/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            cargarTicketsBuzon(); // Recargar de DB
        } else {
            alert("Error al eliminar el ticket.");
        }
    } catch (error) {
        console.error(error);
        alert("Error de conexión al eliminar.");
    }
}

// --- Modal ---
async function abrirModalLectura(id) {
    const ticket = ticketsGlobales.find(t => t.id === id);

    let fechaFormat = "";
    if (ticket.fecha_creacion) {
        fechaFormat = new Date(ticket.fecha_creacion).toLocaleDateString();
    }

    document.getElementById('modalLecturaRemitente').textContent = ticket.remitente || 'Desconocido';
    document.getElementById('modalLecturaCorreo').textContent = ticket.correo || 'Sin correo';
    document.getElementById('modalLecturaAsunto').textContent = ticket.asunto || 'Sin asunto';
    document.getElementById('modalLecturaFecha').textContent = fechaFormat;
    document.getElementById('modalCuerpo').textContent = ticket.mensaje || 'Sin mensaje.';

    if (ticket.evidencia_url) {
        document.getElementById('modalEvidencia').src = ticket.evidencia_url;
        document.getElementById('modalEvidenciaContainer').style.display = 'block';
    } else {
        document.getElementById('modalEvidenciaContainer').style.display = 'none';
    }

    document.getElementById('modalLectura').style.display = 'flex';
    
    // Marcar como leído al abrir
    if (!ticket.revisado) {
        try {
            await fetch(`http://127.0.0.1:8000/api/admin/tickets/${id}/leer`, { method: 'PUT' });
            ticket.revisado = true;
            const row = document.getElementById(`ticket-row-${id}`);
            if (row) row.classList.add('leido');
            
            // Actualizar checkbox a leído visualmente
            const spanIcono = row.querySelector('.ticket-icons span');
            if (spanIcono) spanIcono.innerText = '☑️';
        } catch(e) {
            console.error("No se pudo marcar como leído");
        }
    }
}

function cerrarModalResponder() {
    document.getElementById('modalResponder').style.display = 'none';
}

async function enviarRespuesta() {
    const id = document.getElementById('ticketIdOculto').value;
    const respuesta = document.getElementById('textoRespuesta').value;

    if (!respuesta.trim()) {
        alert("Por favor escribe una respuesta.");
        return;
    }

    try {
        const res = await fetch(`http://127.0.0.1:8000/api/admin/tickets/${id}/responder`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ respuesta: respuesta })
        });

        if (res.ok) {
            alert("✅ Respuesta guardada y enviada");
            cerrarModalResponder();
            cargarTicketsBuzon();
        }
    } catch (error) {
        alert("Error al enviar la respuesta.");
    }
}
