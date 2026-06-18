// static/js/recuperar.js
// Lógica de recuperación de contraseña - recuperar.html

document.getElementById('formRecuperar').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('btnEnviar');
    btn.innerText = "Enviando..."; btn.disabled = true;

    const correo = document.getElementById('correoRecuperar').value;

    try {
        const res = await fetch('http://127.0.0.1:8000/api/recuperar-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ correo: correo })
        });

        const data = await res.json();
        if (res.ok) {
            alert(data.mensaje);
            // Aquí la ingeniera verá el correo simulado en tu consola de Python
        } else {
            alert("Error: " + data.detail);
        }
    } catch (error) {
        alert("Error de conexión con el servidor.");
    }
    btn.innerText = "Enviar enlace de recuperación"; btn.disabled = false;
});
