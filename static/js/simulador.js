// static/js/simulador.js
// Lógica del simulador de validador de autobús - simulador.html

async function cobrarPasaje() {
    const numeroTarjeta = document.getElementById('inputTarjeta').value;
    const contenedor = document.getElementById('contenedorPrincipal');
    const mensaje = document.getElementById('mensajePantalla');
    const saldoDiv = document.getElementById('saldoRestante');

    if (!numeroTarjeta) {
        alert("Por favor ingresa un número de tarjeta.");
        return;
    }

    mensaje.innerText = "PROCESANDO...";
    saldoDiv.innerText = "";
    contenedor.style.borderColor = "#f39c12"; // Amarillo de proceso

    try {
        const response = await fetch('http://127.0.0.1:8000/api/cobrar-pasaje', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ numero_tarjeta: numeroTarjeta })
        });

        const data = await response.json();

        if (response.ok) {
            // ÉXITO
            contenedor.style.borderColor = "#2ecc71"; // Verde
            mensaje.style.color = "#2ecc71";
            mensaje.innerText = "✔️ ACCESO CONCEDIDO";
            saldoDiv.innerText = `Saldo restante: $${data.saldo_restante.toFixed(2)}`;
        } else {
            // RECHAZO
            contenedor.style.borderColor = "#e74c3c"; // Rojo
            mensaje.style.color = "#e74c3c";
            mensaje.innerText = `❌ ${data.detail.toUpperCase()}`;
        }

        // Restaurar la pantalla después de 4 segundos
        setTimeout(() => {
            contenedor.style.borderColor = "#333";
            mensaje.style.color = "white";
            mensaje.innerText = "ESPERANDO TARJETA...";
            saldoDiv.innerText = "";
            document.getElementById('inputTarjeta').value = "";
        }, 4000);

    } catch (error) {
        mensaje.innerText = "ERROR DE CONEXIÓN";
    }
}
