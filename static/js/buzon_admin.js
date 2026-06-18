// static/js/buzon_admin.js
// Lógica del buzón de soporte para administradores - buzon_admin.html

let ticketsGlobales = []; // Variable para no perder los datos de los tickets

async function cargarTicketsBuzon(filtro = 'todos') {
    const contenedor = document.getElementById('listaTickets');
    contenedor.innerHTML = "<p>Cargando mensajes...</p>";

    try {
        const response = await fetch('http://127.0.0.1:8000/api/admin/tickets');
        ticketsGlobales = await response.json();
        let tickets = ticketsGlobales;

        if (filtro === 'importantes') {
            tickets = tickets.filter(t => t.es_importante === true);
        }

        contenedor.innerHTML = "";
        if (tickets.length === 0) {
            contenedor.innerHTML = "<p style='color: gray; text-align: center; margin-top: 20px;'>No hay mensajes para mostrar.</p>";
            return;
        }

        tickets.forEach(ticket => {
            const colorIndicador = ticket.revisado ? "#2ecc71" : "#f1c40f";
            const estrellaIcono = ticket.es_importante ? "⭐" : "☆";
            const estrellaColor = ticket.es_importante ? "orange" : "gray";

            // Etiqueta visual si el ticket ya tiene respuesta
            const avisoRespondido = ticket.respuesta ? `<span style="font-size:12px; background:#2ecc71; color:white; padding:2px 6px; border-radius:10px; margin-left:10px;">Respondido</span>` : '';

            const tarjetaTicket = `
                <div class="ticket-row">
                    <div style="width: 12px; height: 12px; border-radius: 50%; background-color: ${colorIndicador}; margin-right: 15px;" title="Estado"></div>
                    
                    <span onclick="alternarImportancia(${ticket.id}, ${!ticket.es_importante})" style="cursor: pointer; font-size: 20px; color: ${estrellaColor}; margin-right: 15px;" title="Marcar como importante">${estrellaIcono}</span>

                    <div style="flex: 1; cursor: pointer;" onclick="abrirModalResponder(${ticket.id})">
                        <strong style="color: #2c3e50;">${ticket.asunto}</strong> ${avisoRespondido}
                        <span style="color: #7f8c8d; margin-left: 10px;">- ${ticket.mensaje.substring(0, 60)}...</span>
                    </div>
                </div>
            `;
            contenedor.innerHTML += tarjetaTicket;
        });
    } catch (error) {
        contenedor.innerHTML = "<p style='color: red;'>Error conectando con el servidor.</p>";
    }
}

async function alternarImportancia(id, nuevoEstado) {
    await fetch(`http://127.0.0.1:8000/api/admin/tickets/${id}/acciones?importante=${nuevoEstado}`, { method: 'PUT' });
    cargarTicketsBuzon();
}

// --- Funciones para controlar la ventana emergente (Modal) ---
function abrirModalResponder(id) {
    const ticket = ticketsGlobales.find(t => t.id === id);

    document.getElementById('modalAsunto').innerText = ticket.asunto;
    document.getElementById('modalMensaje').innerText = ticket.mensaje;
    document.getElementById('ticketIdOculto').value = ticket.id;

    // Muestra la respuesta si ya existía
    document.getElementById('textoRespuesta').value = ticket.respuesta || "";

    document.getElementById('modalResponder').style.display = 'flex';
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
            alert("✅ Respuesta enviada al estudiante");
            cerrarModalResponder();
            cargarTicketsBuzon();
        }
    } catch (error) {
        alert("Error al enviar la respuesta.");
    }
}

// Cargamos los datos al iniciar la página
window.onload = () => cargarTicketsBuzon();
