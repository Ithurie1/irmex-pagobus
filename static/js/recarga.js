// static/js/recarga.js
// Lógica de recarga de saldo - recarga.html

// ========================================================
// 1. AL ABRIR LA PÁGINA: Buscar tarjeta y mostrar 4 dígitos
// ========================================================
async function cargarDatosTarjeta() {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = "index.html"; // Si no hay sesión, pa' fuera
            return;
        }

        const res = await fetch('http://127.0.0.1:8000/api/mis-tarjetas', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (res.ok) {
            const tarjetas = await res.json();
            if (tarjetas.length === 0) {
                alert("No tienes una tarjeta asignada. Serás redirigido.");
                window.location.href = "dashboard.html";
                return;
            }

            // AQUÍ ESTÁ LA MAGIA: Pinta los 4 dígitos en el texto
            const lblTerminacion = document.getElementById('lblTerminacionRecarga');
            if (lblTerminacion) {
                lblTerminacion.innerText = tarjetas[0].numero_tarjeta.slice(-4);
            }

            // Nos aseguramos de tener el ID correcto en memoria
            localStorage.setItem('tarjeta_seleccionada_id', tarjetas[0].tarjeta_id);
        }
    } catch (error) {
        console.error("Error al cargar la tarjeta:", error);
    }
}

// Ejecutamos la función inmediatamente
cargarDatosTarjeta();

// ========================================================
// 2. UX: Poner la barrita de fecha automáticamente
// ========================================================
document.getElementById('ccVencimiento').addEventListener('input', function (e) {
    let valor = e.target.value.replace(/\D/g, ''); // Quita todo lo que no sea número
    if (valor.length > 2) {
        valor = valor.substring(0, 2) + '/' + valor.substring(2, 4);
    }
    e.target.value = valor;
});

// ========================================================
// 3. LA FUNCIÓN DE PAGO ÚNICA
// ========================================================
async function realizarRecarga(e) {
    // Si el botón está dentro de un <form>, evitamos que recargue la página
    if (e) e.preventDefault();

    // --- VALIDACIÓN RF_12 ESTRICTA ---
    const ccNum = document.getElementById('ccNumero').value.replace(/\s/g, '');
    const ccVenc = document.getElementById('ccVencimiento').value;
    const ccCvc = document.getElementById('ccCvc').value;

    if (!/^\d{16}$/.test(ccNum)) {
        alert("❌ Error: El número de tarjeta debe tener 16 dígitos exactos.");
        return;
    }
    if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(ccVenc)) {
        alert("❌ Error: Formato de fecha inválido. Usa MM/YY.");
        return;
    }
    if (!/^\d{3}$/.test(ccCvc)) {
        alert("❌ Error: El código de seguridad (CVC) debe ser de 3 dígitos.");
        return;
    }

    const [mesExp, anioExp] = ccVenc.split('/');
    const fechaExpiracion = new Date(`20${anioExp}`, parseInt(mesExp) - 1);
    const fechaActual = new Date(2026, 5); // Junio 2026

    if (fechaExpiracion < fechaActual) {
        alert("❌ Error: La tarjeta bancaria ha expirado.");
        return;
    }
    // ----------------------------------

    // Validar que haya escrito un monto
    let inputMonto = document.getElementById('inputMonto');
    if (!inputMonto) inputMonto = document.getElementById('montoRecarga'); // Por si tu HTML usa el otro ID

    const montoRecarga = parseFloat(inputMonto.value);

    if (!inputMonto.value || montoRecarga <= 0) {
        alert("Por favor, ingresa un monto válido mayor a $0.");
        return;
    }

    // Bloquear el botón
    const btn = document.querySelector('button[type="submit"]') || document.querySelector('.btn-principal');
    const textoOriginal = btn.innerText;
    btn.innerText = "⏳ Procesando pago...";
    btn.disabled = true;

    const tarjetaId = localStorage.getItem('tarjeta_seleccionada_id');

    try {
        // Hacer el cobro en el backend
        const response = await fetch('http://127.0.0.1:8000/api/recargar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                tarjeta_id: parseInt(tarjetaId),
                monto: montoRecarga
            })
        });

        const data = await response.json();

        if (response.ok) {
            alert(`✔️ ¡Recarga Exitosa! Tu nuevo saldo es de $${data.nuevo_saldo}.00`);
            window.location.href = "dashboard.html";
        } else {
            alert("Error en la recarga: " + (data.detail || "Error desconocido"));
            btn.innerText = textoOriginal;
            btn.disabled = false;
        }
    } catch (error) {
        alert("Error de conexión con el servidor.");
        btn.innerText = textoOriginal;
        btn.disabled = false;
    }
}

// Detectar si dan clic en el botón de un formulario
const form = document.getElementById('formRecarga');
if (form) {
    form.addEventListener('submit', realizarRecarga);
}
