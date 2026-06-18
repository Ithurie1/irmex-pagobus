// static/js/renovacion.js
// Lógica de solicitud de renovación de tarjeta - renovacion.html

document.getElementById('formRenovacion').addEventListener('submit', async (e) => {
    e.preventDefault();

    const archivoInput = document.getElementById('archivoINE');
    if (archivoInput.files.length === 0) {
        alert("Por favor selecciona un archivo.");
        return;
    }

    // MAGIA AQUÍ: Usamos FormData para empaquetar el archivo físicamente
    const formData = new FormData();
    formData.append('archivo', archivoInput.files[0]);

    const token = localStorage.getItem('token');
    const divMensaje = document.getElementById('mensajeEstado');

    try {
        // NOTA: Cuando se envía FormData, NO ponemos 'Content-Type' en los headers, Fetch lo hace automático.
        const response = await fetch('http://127.0.0.1:8000/api/solicitar-renovacion', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        const data = await response.json();

        // Mostramos el mensaje de éxito o error
        divMensaje.style.display = 'block';
        if (response.ok) {
            divMensaje.style.backgroundColor = '#d4edda';
            divMensaje.style.color = '#155724';
            divMensaje.innerText = data.mensaje;
            archivoInput.value = ''; // Vaciamos el input
        } else {
            divMensaje.style.backgroundColor = '#f8d7da';
            divMensaje.style.color = '#721c24';
            divMensaje.innerText = data.detail;
        }
    } catch (error) {
        alert("Error de conexión al enviar el documento.");
    }
});
