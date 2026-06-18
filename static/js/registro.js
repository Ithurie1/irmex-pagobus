// static/js/registro.js
// Lógica de registro de usuario - registro.html

document.getElementById('registroForm').addEventListener('submit', async function (event) {
    event.preventDefault();

    const nombre = document.getElementById('nombre').value;
    const correo = document.getElementById('correo').value;
    const password = document.getElementById('password').value;
    const fecha = document.getElementById('fecha').value;
    const mensajeElemento = document.getElementById('mensajeRespuesta');

    mensajeElemento.innerText = "Creando cuenta...";
    mensajeElemento.style.color = "var(--gris-texto)";

    try {
        const response = await fetch('http://127.0.0.1:8000/api/registro', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                nombre: nombre,
                correo: correo,
                password: password,
                fecha_nacimiento: fecha
            })
        });

        const data = await response.json();

        if (response.ok) {
            mensajeElemento.style.color = "green";
            mensajeElemento.innerText = `¡Éxito! Cuenta creada.`;
            // Redirigir al inicio de sesión después de 2 segundos
            setTimeout(() => { window.location.href = "index.html"; }, 2000);
        } else {
            mensajeElemento.style.color = "red";
            mensajeElemento.innerText = data.detail;
        }
    } catch (error) {
        mensajeElemento.style.color = "red";
        mensajeElemento.innerText = "Error al conectar con el servidor.";
    }
});
