// static/js/restablecer.js
// Lógica para restablecer contraseña - restablecer.html

// Capturar el correo que viene en la URL simulada
const urlParams = new URLSearchParams(window.location.search);
const correoRecuperacion = urlParams.get('correo');

if (correoRecuperacion) {
    document.getElementById('lblCorreo').innerText = `Cuenta: ${correoRecuperacion}`;
} else {
    alert("Enlace inválido o expirado.");
    window.location.href = "index.html";
}

document.getElementById('formReset').addEventListener('submit', async (e) => {
    e.preventDefault();

    const pass1 = document.getElementById('pass1').value;
    const pass2 = document.getElementById('pass2').value;

    // Validación de seguridad básica
    if (pass1 !== pass2) {
        alert("❌ Las contraseñas no coinciden. Intenta de nuevo.");
        return;
    }
    if (pass1.length < 8) {
        alert("❌ La contraseña debe tener al menos 8 caracteres.");
        return;
    }

    try {
        const res = await fetch('http://127.0.0.1:8000/api/reset-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ correo: correoRecuperacion, nueva_password: pass1 })
        });

        const data = await res.json();
        if (res.ok) {
            alert("✔️ ¡Contraseña actualizada con éxito! Ya puedes iniciar sesión.");
            window.location.href = "index.html"; // Lo regresamos al login
        } else {
            alert("Error: " + data.detail);
        }
    } catch (error) {
        alert("Error de conexión.");
    }
});
