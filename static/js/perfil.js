// static/js/perfil.js
// Lógica del perfil de usuario - perfil.html

const correoUsuario = localStorage.getItem('correo');
let fotoBase64 = "";

async function cargarPerfil() {
    const res = await fetch(`http://127.0.0.1:8000/api/perfil/${correoUsuario}`);
    const data = await res.json();

    if (res.ok) {
        document.getElementById('lblCorreo').innerText = data.correo;
        document.getElementById('lblTipo').innerText = data.es_estudiante ? 'Estudiante' : 'Administrador';

        document.getElementById('lblNombre').innerText = data.nombre;
        document.getElementById('lblCurp').innerText = data.curp || 'No registrado';
        document.getElementById('lblCalle').innerText = data.calle || 'No registrada';
        document.getElementById('lblNumero').innerText = data.numero || 'S/N';
        document.getElementById('lblColonia').innerText = data.colonia || 'No registrada';
        document.getElementById('lblCp').innerText = data.cp || '00000';

        // Llenar inputs por si le dan a editar
        document.getElementById('inNombre').value = data.nombre;
        document.getElementById('inCurp').value = data.curp;
        document.getElementById('inCalle').value = data.calle;
        document.getElementById('inNumero').value = data.numero;
        document.getElementById('inColonia').value = data.colonia;
        document.getElementById('inCp').value = data.cp;

        // Cargar Foto
        if (data.foto_perfil) {
            document.getElementById('imgAvatar').src = data.foto_perfil;
            fotoBase64 = data.foto_perfil;
        }
    }
}

function activarEdicion() {
    // Ocultar textos
    document.querySelectorAll('.txt-display').forEach(el => el.style.display = 'none');
    // Mostrar inputs
    document.querySelectorAll('.input-edit').forEach(el => el.style.display = 'block');

    document.getElementById('btnEditar').style.display = 'none';
    document.getElementById('btnGuardar').style.display = 'block';
    document.getElementById('btnCambiarFoto').style.display = 'inline-block';
}

// Convertir imagen a texto Base64
document.getElementById('inputFoto').addEventListener('change', function (e) {
    const file = e.target.files[0];
    const reader = new FileReader();
    reader.onloadend = function () {
        fotoBase64 = reader.result;
        document.getElementById('imgAvatar').src = fotoBase64;
    }
    if (file) reader.readAsDataURL(file);
});

async function guardarPerfil() {
    const datosEditados = {
        nombre: document.getElementById('inNombre').value,
        curp: document.getElementById('inCurp').value,
        calle: document.getElementById('inCalle').value,
        numero: document.getElementById('inNumero').value,
        colonia: document.getElementById('inColonia').value,
        cp: document.getElementById('inCp').value,
        foto_perfil: fotoBase64
    };

    const res = await fetch(`http://127.0.0.1:8000/api/perfil/${correoUsuario}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datosEditados)
    });

    if (res.ok) {
        alert("¡Perfil actualizado correctamente!");
        location.reload(); // Recargar para volver a la vista normal
    } else {
        alert("Hubo un error al guardar");
    }
}

cargarPerfil();
