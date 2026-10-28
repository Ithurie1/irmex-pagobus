// static/js/index.js
// Lógica de inicio de sesión - index.html

document.getElementById('loginForm').addEventListener('submit', async function (event) {
    event.preventDefault();

    const correo = document.getElementById('correo').value;
    const password = document.getElementById('password').value;
    const mensajeElemento = document.getElementById('mensajeRespuesta');

    mensajeElemento.innerText = "Verificando credenciales...";
    mensajeElemento.style.color = "var(--gris-texto)";

    try {
        const response = await fetch('http://127.0.0.1:8000/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ correo: correo, password: password })
        });

        const data = await response.json();

        if (response.ok) {
            // 1. Guardamos el Gafete VIP en la memoria del navegador
            localStorage.setItem('token', data.access_token);
            localStorage.setItem('correo', correo);
            localStorage.setItem('es_estudiante', data.es_estudiante);
            localStorage.setItem('rol', data.rol || (data.es_estudiante ? 'estudiante' : 'admin'));
            
            const rol = localStorage.getItem('rol');
            const targetUrl = "dashboard.html";

            window.location.href = targetUrl;
            mensajeElemento.style.color = "green";
            mensajeElemento.innerText = `¡Bienvenido de vuelta, ${data.nombre}! Entrando...`;

            setTimeout(() => {
                window.location.href = targetUrl;
            }, 1000);
        } else {
            mensajeElemento.style.color = "red";
            mensajeElemento.innerText = data.detail;
        }
    } catch (error) {
        mensajeElemento.style.color = "red";
        mensajeElemento.innerText = "Error al conectar con el servidor.";
    }
});
