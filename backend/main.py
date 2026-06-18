import sys, os
# =========================================================
# CONFIGURACIÓN DE RUTAS (ejecutar desde la raíz del proyecto)
# =========================================================
# Permite importar models, schemas y database desde backend/
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DOCUMENTOS_DIR = os.path.join(BASE_DIR, "documentos_seguros")          # .../Irmex/backend
PROJECT_ROOT = os.path.dirname(BASE_DIR)                       # .../Irmex
sys.path.insert(0, BASE_DIR)

from fastapi import FastAPI, Depends, HTTPException
from fastapi.staticfiles import StaticFiles
from sqlalchemy.orm import Session
from passlib.context import CryptContext
from datetime import datetime, timedelta, date
# pyrefly: ignore [missing-import]
import jwt
from fastapi.security import OAuth2PasswordBearer
from fastapi.middleware.cors import CORSMiddleware
import models, schemas
from database import engine, SessionLocal
import random
from pydantic import BaseModel
from fastapi import File, UploadFile
import shutil
from fastapi.responses import FileResponse
from sqlalchemy import func
from typing import Optional

# Rutas absolutas relativas a la raíz del proyecto
STATIC_DIR        = os.path.join(PROJECT_ROOT, "static")
FRONTEND_DIR      = os.path.join(PROJECT_ROOT, "frontend")
DOCS_SEGUROS_DIR  = os.path.join(PROJECT_ROOT, "documentos_seguros")

# Crear carpeta de documentos seguros (cumpliendo con RN_02 de seguridad)
os.makedirs(DOCS_SEGUROS_DIR, exist_ok=True)
# =========================================================
# 1. CREACIÓN DE LA APP Y BASE DE DATOS
# =========================================================
models.Base.metadata.create_all(bind=engine)
app = FastAPI()

# =========================================================
# MONTAJE DE ARCHIVOS ESTÁTICOS (CSS, JS, imágenes y documentos seguros)
# =========================================================
app.mount("/static", StaticFiles(directory=STATIC_DIR), name="static")
app.mount("/documentos_seguros", StaticFiles(directory=DOCS_SEGUROS_DIR), name="documentos_seguros")

# =========================================================
# 2. CONFIGURACIÓN DE CORS (Para conectar con el HTML)
# =========================================================
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Permite peticiones de cualquier origen (ideal para desarrollo local)
    allow_credentials=True,
    allow_methods=["*"],  # Permite todos los métodos (GET, POST, etc.)
    allow_headers=["*"],  # Permite todos los encabezados
)

# =========================================================
# 3. CONFIGURACIÓN DE SEGURIDAD (Bcrypt y JWT)
# =========================================================
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
SECRET_KEY = "la_clave_super_secreta_de_irmex_no_compartir" 
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60 

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/login")

# =========================================================
# 4. FUNCIONES AUXILIARES Y DEPENDENCIAS
# =========================================================
def obtener_password_hash(password):
    return pwd_context.hash(password)

def verificar_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

def crear_token_acceso(data: dict, expires_delta: timedelta):
    to_encode = data.copy()
    expire = datetime.utcnow() + expires_delta
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# EL "CADENERO" (Filtro de seguridad JWT)
def obtener_usuario_actual(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)):
    excepcion_credenciales = HTTPException(
        status_code=401,
        detail="No se pudieron validar las credenciales o el token expiró",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        correo: str = payload.get("sub")
        if correo is None:
            raise excepcion_credenciales
    except jwt.InvalidTokenError:
        raise excepcion_credenciales
    
    usuario = db.query(models.Usuario).filter(models.Usuario.correo == correo).first()
    if usuario is None:
        raise excepcion_credenciales
    
    return usuario


# =========================================================
# 5. RUTAS DE LA API (Endpoints)
# =========================================================

@app.get("/")
def leer_raiz():
    return FileResponse(os.path.join(FRONTEND_DIR, "index.html"))

# =========================================================
# RUTAS PARA SERVIR PÁGINAS HTML (desde frontend/)
# =========================================================
@app.get("/index.html")
def servir_index(): return FileResponse(os.path.join(FRONTEND_DIR, "index.html"))

@app.get("/dashboard.html")
def servir_dashboard(): return FileResponse(os.path.join(FRONTEND_DIR, "dashboard.html"))

@app.get("/registro.html")
def servir_registro(): return FileResponse(os.path.join(FRONTEND_DIR, "registro.html"))

@app.get("/historial.html")
def servir_historial(): return FileResponse(os.path.join(FRONTEND_DIR, "historial.html"))

@app.get("/recarga.html")
def servir_recarga(): return FileResponse(os.path.join(FRONTEND_DIR, "recarga.html"))

@app.get("/perfil.html")
def servir_perfil(): return FileResponse(os.path.join(FRONTEND_DIR, "perfil.html"))

@app.get("/recuperar.html")
def servir_recuperar(): return FileResponse(os.path.join(FRONTEND_DIR, "recuperar.html"))

@app.get("/restablecer.html")
def servir_restablecer(): return FileResponse(os.path.join(FRONTEND_DIR, "restablecer.html"))

@app.get("/renovacion.html")
def servir_renovacion(): return FileResponse(os.path.join(FRONTEND_DIR, "renovacion.html"))

@app.get("/crud_tarjetas.html")
def servir_crud_tarjetas(): return FileResponse(os.path.join(FRONTEND_DIR, "crud_tarjetas.html"))

@app.get("/reportes.html")
def servir_reportes(): return FileResponse(os.path.join(FRONTEND_DIR, "reportes.html"))

@app.get("/buzon_admin.html")
def servir_buzon_admin(): return FileResponse(os.path.join(FRONTEND_DIR, "buzon_admin.html"))

@app.get("/verificar_estatus.html")
def servir_verificar_estatus(): return FileResponse(os.path.join(FRONTEND_DIR, "verificar_estatus.html"))

@app.get("/verificador_documentos.html")
def servir_verificador_documentos(): return FileResponse(os.path.join(FRONTEND_DIR, "verificador_documentos.html"))

@app.get("/estaciones.html")
def servir_estaciones(): return FileResponse(os.path.join(FRONTEND_DIR, "estaciones.html"))

@app.get("/simulador.html")
def servir_simulador(): return FileResponse(os.path.join(FRONTEND_DIR, "simulador.html"))

@app.post("/api/registro")
def registrar_usuario(usuario: schemas.UsuarioCrear, db: Session = Depends(get_db)):
    db_usuario = db.query(models.Usuario).filter(models.Usuario.correo == usuario.correo).first()
    if db_usuario:
        raise HTTPException(status_code=400, detail="Este correo ya está registrado")
    
    hashed_password = obtener_password_hash(usuario.password)
    
    # --- REGLA DE NEGOCIO AUTOMÁTICA PARA ROLES ---
    # Si el correo contiene la palabra "admin", es_estudiante será False (es decir, será Admin).
    # De lo contrario, será True (Estudiante).
    es_estu = True
    if "admin" in usuario.correo.lower():
        es_estu = False
    # -----------------------------------------------
    
    nuevo_usuario = models.Usuario(
        nombre=usuario.nombre,
        correo=usuario.correo,
        password_hash=hashed_password,
        fecha_nacimiento=usuario.fecha_nacimiento,
        es_estudiante=es_estu  # <-- Le pasamos el rol calculado
    )
    
    db.add(nuevo_usuario)
    db.commit()
    db.refresh(nuevo_usuario)

    if es_estu:
        numero_aleatorio = str(random.randint(1000000000000000, 9999999999999999))
        nueva_tarjeta = models.Tarjeta(
            numero_tarjeta=numero_aleatorio,
            saldo=0.0,
            estatus="Inactiva", # La tarjeta inicia como inactiva hasta que el admin apruebe su renovación
            usuario_id=nuevo_usuario.usuario_id # Queda vinculada a este usuario
        )
        db.add(nueva_tarjeta)
        db.commit()
    
    return {"mensaje": "Usuario creado con éxito", "usuario_id": nuevo_usuario.usuario_id}

@app.post("/api/login")
def iniciar_sesion(usuario: schemas.UsuarioLogin, db: Session = Depends(get_db)):
    # 1. Buscar al usuario por correo
    db_usuario = db.query(models.Usuario).filter(models.Usuario.correo == usuario.correo).first()
    
    # 2. Verificar contraseña
    if not db_usuario or not verificar_password(usuario.password, db_usuario.password_hash):
        raise HTTPException(status_code=401, detail="Correo o contraseña incorrectos")
    
    # 3. ¡FABRICAMOS EL TOKEN!
    tiempo_expiracion = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    token_jwt = crear_token_acceso(
        data={"sub": db_usuario.correo}, 
        expires_delta=tiempo_expiracion
    )
    
    # ===================================================
    # 3.5 === CÓDIGO RF_18: REGISTRAR ACCESO EXITOSO ===
    # ===================================================
    nuevo_log = models.LogAcceso(correo_usuario=db_usuario.correo)
    db.add(nuevo_log)
    db.commit()
    # ===================================================
    
    # 4. Entregamos el token al frontend
    return {
        "access_token": token_jwt, 
        "token_type": "bearer",
        "mensaje": "Inicio de sesión exitoso", 
        "usuario_id": db_usuario.usuario_id, 
        "nombre": db_usuario.nombre,
        "es_estudiante": db_usuario.es_estudiante  # <-- NUEVO: Mandamos el rol
    }


# =========================================================
# INCISO B: MÓDULO DE AUDITORÍA (RF_18) - VA AFUERA DEL LOGIN
# =========================================================
@app.get("/api/logs")
def obtener_logs_acceso(db: Session = Depends(get_db)):
    # Traemos los últimos 50 accesos, ordenados del más nuevo al más viejo
    logs = db.query(models.LogAcceso).order_by(models.LogAcceso.fecha_hora.desc()).limit(50).all()
    
    return [
        {
            "correo": log.correo_usuario,
            "fecha_hora": log.fecha_hora.strftime("%d/%m/%Y %H:%M:%S")
        }
        for log in logs
    ]

# =========================================================
# MÓDULO DE RECUPERACIÓN DE CONTRASEÑA (RF_03)
# =========================================================
class RecuperarRequest(BaseModel):
    correo: str

@app.post("/api/recuperar-password")
def enviar_correo_recuperacion(datos: RecuperarRequest, db: Session = Depends(get_db)):
    usuario = db.query(models.Usuario).filter(models.Usuario.correo == datos.correo).first()
    
    if not usuario:
        raise HTTPException(status_code=404, detail="Correo no encontrado en el sistema.")
    
    # 🌟 SIMULACIÓN DE ENVÍO DE CORREO (Impreso en la terminal)
    print("\n" + "="*60)
    print(f"📧 SERVIDOR SMTP SIMULADO - ENVIANDO CORREO A: {datos.correo}")
    print("Asunto: Recuperación de Contraseña - IrMex")
    print(f"Cuerpo: Hola {usuario.nombre}, hemos recibido una solicitud para cambiar tu contraseña.")
    print(f"Haz clic en el siguiente enlace para restablecerla:")
    print(f"👉 http://127.0.0.1:5500/restablecer.html?correo={datos.correo}") # Cambia el 5500 por el puerto de tu Live Server
    print("="*60 + "\n")

    return {"mensaje": "Correo de recuperación enviado con éxito. Revisa tu bandeja de entrada (Consola)."}

class ResetRequest(BaseModel):
    correo: str
    nueva_password: str

@app.post("/api/reset-password")
def cambiar_password(datos: ResetRequest, db: Session = Depends(get_db)):
    usuario = db.query(models.Usuario).filter(models.Usuario.correo == datos.correo).first()
    if not usuario:
        raise HTTPException(status_code=404, detail="Usuario no encontrado.")
    
    # 🔒 AQUÍ ESTÁ LA MAGIA: Encriptamos la nueva contraseña antes de guardarla
    usuario.password_hash = obtener_password_hash(datos.nueva_password) 
    
    db.commit()
    
    return {"mensaje": "Contraseña actualizada exitosamente."}

# RUTA PROTEGIDA: Solo usuarios con token pueden ver su perfil
@app.get("/api/perfil")
def leer_perfil(usuario_actual: models.Usuario = Depends(obtener_usuario_actual)):
    return {
        "nombre": usuario_actual.nombre,
        "correo": usuario_actual.correo,
        "fecha_nacimiento": usuario_actual.fecha_nacimiento,
        "curp": usuario_actual.curp,
        "calle": usuario_actual.calle,
        "numero": usuario_actual.numero,
        "colonia": usuario_actual.colonia,
        "cp": usuario_actual.cp
    }

# RUTA PARA ACTUALIZAR LOS DATOS DEL PERFIL (PUT)
@app.put("/api/perfil")
def actualizar_perfil(datos: dict, usuario_actual: models.Usuario = Depends(obtener_usuario_actual), db: Session = Depends(get_db)):
    if "nombre" in datos and datos["nombre"].strip():
        usuario_actual.nombre = datos["nombre"]
        
    if "fecha_nacimiento" in datos and datos["fecha_nacimiento"]:
        try:
            from datetime import datetime
            fecha_objeto = datetime.strptime(datos["fecha_nacimiento"], "%Y-%m-%d").date()
            usuario_actual.fecha_nacimiento = fecha_objeto
        except ValueError:
            raise HTTPException(status_code=400, detail="Formato de fecha incorrecto")

    # Guardamos los nuevos campos si vienen en la petición
    if "curp" in datos: usuario_actual.curp = datos["curp"]
    if "calle" in datos: usuario_actual.calle = datos["calle"]
    if "numero" in datos: usuario_actual.numero = datos["numero"]
    if "colonia" in datos: usuario_actual.colonia = datos["colonia"]
    if "cp" in datos: usuario_actual.cp = datos["cp"]

    db.commit()
    db.refresh(usuario_actual)
    return {"mensaje": "Perfil actualizado con éxito"}

# --- RUTAS DEL CRUD DE TARJETAS ---

@app.post("/api/tarjetas", response_model=schemas.TarjetaRespuesta)
def crear_tarjeta(tarjeta: schemas.TarjetaCrear, db: Session = Depends(get_db)):
    db_tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.numero_tarjeta == tarjeta.numero_tarjeta).first()
    if db_tarjeta:
        raise HTTPException(status_code=400, detail="Este número de tarjeta ya está registrado")
    
    nueva_tarjeta = models.Tarjeta(
        numero_tarjeta=tarjeta.numero_tarjeta,
        saldo=tarjeta.saldo,
        estatus=tarjeta.estatus
    )
    db.add(nueva_tarjeta)
    db.commit()
    db.refresh(nueva_tarjeta)
    return nueva_tarjeta

# 1. Modificamos el GET para que devuelva la vigencia
@app.get("/api/tarjetas")
def obtener_tarjetas(db: Session = Depends(get_db)):
    tarjetas = db.query(models.Tarjeta).all()
    resultados = []
    for t in tarjetas:
        # Formateamos la fecha si existe, si no, ponemos un aviso
        fecha_str = t.fecha_expiracion.strftime("%d/%m/%Y") if t.fecha_expiracion else "Sin vigencia"
        resultados.append({
            "tarjeta_id": t.tarjeta_id,
            "numero_tarjeta": t.numero_tarjeta,
            "saldo": t.saldo,
            "estatus": t.estatus,
            "fecha_expiracion": fecha_str # <-- El nuevo dato
        })
    return resultados

# 2. NUEVO ENDPOINT: Para sumar o restar días de vida útil
class ActualizarVigencia(BaseModel):
    dias: int

@app.put("/api/tarjetas/{tarjeta_id}/vigencia")
def modificar_vigencia(tarjeta_id: int, datos: ActualizarVigencia, db: Session = Depends(get_db)):
    tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.tarjeta_id == tarjeta_id).first()
    if not tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no encontrada")
    
    # Si la tarjeta era nueva y no tenía vigencia, empezamos a contar desde hoy
    if not tarjeta.fecha_expiracion:
        tarjeta.fecha_expiracion = date.today()
        
    # Sumamos (o restamos si manda un número negativo) los días
    tarjeta.fecha_expiracion += timedelta(days=datos.dias)
    db.commit()
    
    return {"mensaje": "Vigencia actualizada", "nueva_fecha": tarjeta.fecha_expiracion.strftime("%d/%m/%Y")}

@app.put("/api/tarjetas/{tarjeta_id}")
def actualizar_tarjeta(tarjeta_id: int, tarjeta_act: schemas.TarjetaActualizar, db: Session = Depends(get_db)):
    db_tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.tarjeta_id == tarjeta_id).first()
    if not db_tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no encontrada")
    
    db_tarjeta.estatus = tarjeta_act.estatus
    db.commit()
    db.refresh(db_tarjeta)
    return db_tarjeta

@app.delete("/api/tarjetas/{tarjeta_id}")
def eliminar_tarjeta(tarjeta_id: int, db: Session = Depends(get_db)):
    db_tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.tarjeta_id == tarjeta_id).first()
    if not db_tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no encontrada")
    
    db.delete(db_tarjeta)
    db.commit()
    return {"mensaje": "Tarjeta eliminada con éxito"}

# RUTA PARA RECARGAR DINERO
# RUTA PARA RECARGAR DINERO
@app.post("/api/recargar")
def recargar_saldo(data: dict, db: Session = Depends(get_db)):
    # 1. Buscamos la tarjeta
    tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.tarjeta_id == data["tarjeta_id"]).first()
    if not tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no encontrada")
    
    # 2. Sumamos el saldo
    tarjeta.saldo += float(data["monto"])
    
    # 3. Guardamos el historial (CON LOS NOMBRES ACTUALIZADOS)
    nuevo_mov = models.Movimiento(
        tarjeta_id=tarjeta.tarjeta_id,
        tipo="Recarga de Saldo",          # <-- Antes decía 'descripcion'
        monto=float(data["monto"]),
        fecha_hora=datetime.now().strftime("%d/%m/%Y %H:%M"), # <-- Antes decía 'fecha'
        origen="N/A",                     # <-- Agregamos origen
        destino="N/A"                     # <-- Agregamos destino
    )
    db.add(nuevo_mov)
    db.commit()
    return {"mensaje": "Recarga exitosa", "nuevo_saldo": tarjeta.saldo}

# RUTA PARA VER EL HISTORIAL
@app.get("/api/historial/{tarjeta_id}")
def obtener_historial(tarjeta_id: int, db: Session = Depends(get_db)):
    return db.query(models.Movimiento).filter(models.Movimiento.tarjeta_id == tarjeta_id).all()

# RUTA PROTEGIDA: Solo devuelve las tarjetas del usuario logueado
@app.get("/api/mis-tarjetas")
def obtener_mis_tarjetas(usuario_actual: models.Usuario = Depends(obtener_usuario_actual), db: Session = Depends(get_db)):
    tarjetas = db.query(models.Tarjeta).filter(models.Tarjeta.usuario_id == usuario_actual.usuario_id).all()
    
    resultados = []
    fecha_actual = date.today()
    
    for t in tarjetas:
        requiere_renovacion = False
        fecha_str = "Sin vigencia asignada"
        
        # Leemos la fecha real de la base de datos
        if t.fecha_expiracion:
            dias_restantes = (t.fecha_expiracion - fecha_actual).days
            requiere_renovacion = dias_restantes <= 15
            fecha_str = t.fecha_expiracion.strftime("%d/%m/%Y")
            
        resultados.append({
            "tarjeta_id": t.tarjeta_id,
            "numero_tarjeta": t.numero_tarjeta,
            "saldo": t.saldo,
            "estatus": t.estatus,
            "requiere_renovacion_urgente": requiere_renovacion,
            "fecha_expiracion_formateada": fecha_str
        })
        
    return resultados
# Esquema para recibir los datos de recuperación
class RecuperarPassword(BaseModel):
    correo: str
    fecha_nacimiento: str
    nueva_password: str

@app.post("/api/recuperar-password")
def recuperar_password(datos: RecuperarPassword, db: Session = Depends(get_db)):
    # 1. Buscamos si existe el correo (le quitamos espacios en blanco por si acaso)
    usuario = db.query(models.Usuario).filter(models.Usuario.correo == datos.correo.strip()).first()
    
    if not usuario:
        raise HTTPException(status_code=404, detail="El correo no está registrado.")
        
    # 2. Validamos la "Pregunta de Seguridad" (Fecha de nacimiento)
    # FORZAMOS a que ambos sean texto para que la comparación sea exacta
    fecha_db = str(usuario.fecha_nacimiento).strip()
    fecha_input = str(datos.fecha_nacimiento).strip()
    
    if fecha_db != fecha_input:
        raise HTTPException(status_code=400, detail="Los datos de validación no coinciden.")
        
    # 3. Si todo está bien, encriptamos la nueva contraseña y actualizamos
    usuario.password_hash = obtener_password_hash(datos.nueva_password)
    db.commit()
    
    return {"mensaje": "Contraseña actualizada correctamente. Ya puedes iniciar sesión."}

class EstacionCrear(BaseModel):
    nombre: str
    tipo: str

# ==========================================
# RUTAS PARA GESTIÓN DE ESTACIONES (REQ-008)
# ==========================================

@app.post("/api/estaciones")
def crear_estacion(estacion: EstacionCrear, db: Session = Depends(get_db)):
    db_estacion = db.query(models.Estacion).filter(models.Estacion.nombre == estacion.nombre).first()
    if db_estacion:
        raise HTTPException(status_code=400, detail="Esta estación ya existe")
    
    nueva_estacion = models.Estacion(nombre=estacion.nombre, tipo=estacion.tipo)
    db.add(nueva_estacion)
    db.commit()
    return {"mensaje": "Estación registrada con éxito"}

@app.get("/api/estaciones")
def obtener_estaciones(db: Session = Depends(get_db)):
    return db.query(models.Estacion).all()

@app.put("/api/estaciones/{estacion_id}")
def cambiar_estatus_estacion(estacion_id: int, db: Session = Depends(get_db)):
    estacion = db.query(models.Estacion).filter(models.Estacion.id == estacion_id).first()
    if not estacion:
        raise HTTPException(status_code=404, detail="Estación no encontrada")
    
    # Alternar el estatus entre Operativa y Mantenimiento
    estacion.estatus = "Mantenimiento" if estacion.estatus == "Operativa" else "Operativa"
    db.commit()
    return {"mensaje": "Estatus actualizado", "nuevo_estatus": estacion.estatus}    

# ==========================================
# RF_05: VERIFICAR ESTATUS DE USUARIO
# ==========================================
@app.get("/api/verificar-estudiante/{correo}")
def verificar_estudiante(correo: str, db: Session = Depends(get_db)):
    # 1. Buscamos al estudiante por su correo (matrícula)
    usuario = db.query(models.Usuario).filter(models.Usuario.correo == correo, models.Usuario.es_estudiante == True).first()
    
    if not usuario:
        raise HTTPException(status_code=404, detail="Estudiante no encontrado en el sistema.")
        
    # 2. Buscamos la tarjeta vinculada a este estudiante
    tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.usuario_id == usuario.usuario_id).first()
    
    # 3. Determinamos su estatus
    estatus_tarjeta = tarjeta.estatus if tarjeta else "Sin tarjeta asignada"
    
    return {
        "nombre": usuario.nombre,
        "correo": usuario.correo,
        "estatus_tarjeta": estatus_tarjeta
    }

# ==========================================
# REPOSICIÓN DE TARJETA (Propuesta del Tester)
# ==========================================
@app.post("/api/asignar-tarjeta/{correo}")
def asignar_nueva_tarjeta(correo: str, db: Session = Depends(get_db)):
    usuario = db.query(models.Usuario).filter(models.Usuario.correo == correo, models.Usuario.es_estudiante == True).first()
    if not usuario:
        raise HTTPException(status_code=404, detail="Estudiante no encontrado.")

    # 1. Por seguridad, desactivamos todas las tarjetas anteriores que tenga el usuario
    tarjetas_antiguas = db.query(models.Tarjeta).filter(models.Tarjeta.usuario_id == usuario.usuario_id).all()
    for t in tarjetas_antiguas:
        t.estatus = "Inactiva"

    # 2. Le creamos su nueva tarjeta aleatoria
    import random
    numero_aleatorio = str(random.randint(1000000000000000, 9999999999999999))
    nueva_tarjeta = models.Tarjeta(
        numero_tarjeta=numero_aleatorio,
        saldo=0.0,  # Empieza en cero por ser reposición
        estatus="Activa",
        usuario_id=usuario.usuario_id
    )
    db.add(nueva_tarjeta)
    db.commit()
    
    return {"mensaje": f"Reposición exitosa. Nueva tarjeta asignada: {numero_aleatorio}"}

# ==========================================
# RF_09: SOLICITAR RENOVACIÓN (Subida de archivos)
# ==========================================
@app.post("/api/solicitar-renovacion")
async def solicitar_renovacion(
    archivo: UploadFile = File(...),
    usuario_actual: models.Usuario = Depends(obtener_usuario_actual),
    db: Session = Depends(get_db)
):
    # 1. Validamos que el estudiante no tenga ya una solicitud pendiente
    solicitud_previa = db.query(models.Renovacion).filter(
        models.Renovacion.usuario_id == usuario_actual.usuario_id,
        models.Renovacion.estatus == "Pendiente"
    ).first()
    
    if solicitud_previa:
        raise HTTPException(status_code=400, detail="Ya tienes una solicitud en revisión.")

    # 2. Generamos un nombre único y seguro para el archivo
    extension = archivo.filename.split(".")[-1]
    nombre_seguro = f"ine_estudiante_{usuario_actual.usuario_id}.{extension}"
    ruta_guardado = os.path.join(DOCS_SEGUROS_DIR, nombre_seguro)

    # 3. Guardamos el archivo físico en el servidor
    with open(ruta_guardado, "wb") as buffer:
        shutil.copyfileobj(archivo.file, buffer)

    # 4. Registramos el trámite en la base de datos
    nueva_renovacion = models.Renovacion(
        usuario_id=usuario_actual.usuario_id,
        archivo_ruta=ruta_guardado,
        estatus="Pendiente",
        fecha=datetime.now().strftime("%d/%m/%Y")
    )
    db.add(nueva_renovacion)
    db.commit()

    return {"mensaje": "Documento subido y protegido. En revisión por administración."}

# ==========================================
# RF_10 y RN_02: VERIFICAR RENOVACIONES (Admin)
# ==========================================

# 1. Obtener la lista de solicitudes pendientes
@app.get("/api/renovaciones/pendientes")
def obtener_renovaciones_pendientes(db: Session = Depends(get_db)):
    # Buscamos solo las que dicen "Pendiente"
    pendientes = db.query(models.Renovacion).filter(models.Renovacion.estatus == "Pendiente").all()
    
    resultados = []
    for r in pendientes:
        usuario = db.query(models.Usuario).filter(models.Usuario.usuario_id == r.usuario_id).first()
        if usuario:
            # Solo enviamos el nombre del archivo, no la ruta completa por seguridad
            nombre_archivo = r.archivo_ruta.split("/")[-1] 
            
            resultados.append({
                "id_renovacion": r.id,
                "nombre_usuario": usuario.nombre,
                "correo": usuario.correo,
                "fecha": r.fecha,
                "nombre_archivo": nombre_archivo
            })
    return resultados

# 2. Servir el documento de forma segura (RN_02)
@app.get("/api/documentos-seguros/{nombre_archivo}")
def ver_documento_seguro(nombre_archivo: str):
    ruta_fisica = os.path.join(DOCS_SEGUROS_DIR, nombre_archivo)
    
    # Verificamos que el archivo realmente exista en la bóveda
    if not os.path.exists(ruta_fisica):
        raise HTTPException(status_code=404, detail="El documento no existe o fue eliminado.")
        
    # FileResponse toma el archivo físico y lo manda a la pantalla
    return FileResponse(ruta_fisica)

# 3. Aprobar o Rechazar el trámite
class EstatusRenovacion(BaseModel):
    estatus: str  # Debe ser "Aprobada" o "Rechazada"

@app.put("/api/renovaciones/{renovacion_id}")
def procesar_renovacion(renovacion_id: int, datos: EstatusRenovacion, db: Session = Depends(get_db)):
    renovacion = db.query(models.Renovacion).filter(models.Renovacion.id == renovacion_id).first()
    
    if not renovacion:
        raise HTTPException(status_code=404, detail="Solicitud no encontrada")
        
    renovacion.estatus = datos.estatus
    
    # Si se aprueba, reactivamos y le damos 180 días de vida útil real
    if datos.estatus == "Aprobada":
        tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.usuario_id == renovacion.usuario_id).first()
        if tarjeta:
            tarjeta.estatus = "Activa"
            tarjeta.fecha_expiracion = date.today() + timedelta(days=180) # <-- MAGIA AQUÍ
            
    db.commit()
    return {"mensaje": f"La solicitud fue {datos.estatus.lower()}."}
# =========================================================
# SIMULADOR DE VALIDADOR DE AUTOBÚS
# =========================================================
class PeticionPasaje(BaseModel):
    numero_tarjeta: str

@app.post("/api/cobrar-pasaje")
def cobrar_pasaje(datos: PeticionPasaje, db: Session = Depends(get_db)):
    # 1. Buscamos la tarjeta por su número físico
    tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.numero_tarjeta == datos.numero_tarjeta).first()
    
    if not tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no reconocida")
        
    # 2. Verificamos que esté activa (que no sea de un usuario nuevo sin verificar)
    if tarjeta.estatus != "Activa":
        raise HTTPException(status_code=403, detail="Tarjeta inactiva o bloqueada")
        
    # 3. Verificamos el saldo (Asumimos que el pasaje cuesta $10.00)
    costo_pasaje = 10.00
    if tarjeta.saldo < costo_pasaje:
        raise HTTPException(status_code=402, detail="Saldo insuficiente")
        
    # 4. Hacemos el cobro
    tarjeta.saldo -= costo_pasaje
    
    # 5. Guardamos el movimiento en el historial
    nuevo_mov = models.Movimiento(
        tarjeta_id=tarjeta.tarjeta_id,
        tipo="Pago de Pasaje",
        monto=costo_pasaje,
        origen="Validador Unidad 15", # Simulamos el número del camión
        destino="Ruta Universitaria",
        fecha_hora=datetime.now().strftime("%d/%m/%Y %H:%M")
    )
    db.add(nuevo_mov)
    db.commit()
    
    return {"mensaje": "¡Acceso Concedido!", "saldo_restante": tarjeta.saldo}

# =========================================================
# MÓDULO DE REPORTES PARA EL ADMINISTRADOR
# =========================================================
@app.get("/api/reportes/general")
def obtener_reportes(db: Session = Depends(get_db)):
    # 1. Total de Estudiantes registrados
    total_estudiantes = db.query(models.Usuario).filter(models.Usuario.es_estudiante == True).count()
    
    # 2. Estatus de las tarjetas
    tarjetas_activas = db.query(models.Tarjeta).filter(models.Tarjeta.estatus == "Activa").count()
    tarjetas_inactivas = db.query(models.Tarjeta).filter(models.Tarjeta.estatus == "Inactiva").count()
    
    # 3. Finanzas (Sumar recargas vs Sumar pasajes)
    # Buscamos la palabra "Recarga" en el tipo de movimiento
    ingresos = db.query(func.sum(models.Movimiento.monto)).filter(models.Movimiento.tipo.like("%Recarga%")).scalar() or 0.0
    
    # Buscamos la palabra "Pasaje" en el tipo de movimiento
    gastos = db.query(func.sum(models.Movimiento.monto)).filter(models.Movimiento.tipo.like("%Pasaje%")).scalar() or 0.0

    return {
        "total_estudiantes": total_estudiantes,
        "tarjetas_activas": tarjetas_activas,
        "tarjetas_inactivas": tarjetas_inactivas,
        "total_ingresos": ingresos,
        "total_gastos": gastos
    }

# =========================================================
# MÓDULO DE PERFIL DE USUARIO (Actualizado)
# =========================================================
class DatosPerfilUpdate(BaseModel):
    nombre: str
    curp: str
    calle: str
    numero: str
    colonia: str
    cp: str
    foto_perfil: Optional[str] = None

@app.get("/api/perfil/{correo}")
def obtener_perfil(correo: str, db: Session = Depends(get_db)):
    usuario = db.query(models.Usuario).filter(models.Usuario.correo == correo).first()
    if not usuario:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
        
    return {
        "nombre": usuario.nombre,
        "correo": usuario.correo,
        "es_estudiante": usuario.es_estudiante,
        "curp": usuario.curp or "",
        "calle": usuario.calle or "",
        "numero": usuario.numero or "",
        "colonia": usuario.colonia or "",
        "cp": usuario.cp or "",
        "foto_perfil": usuario.foto_perfil or "",
        "fecha_registro": usuario.creado_en.strftime("%d/%m/%Y") if usuario.creado_en else "Desconocida"
    }

@app.put("/api/perfil/{correo}")
def actualizar_perfil(correo: str, datos: DatosPerfilUpdate, db: Session = Depends(get_db)):
    usuario = db.query(models.Usuario).filter(models.Usuario.correo == correo).first()
    if not usuario:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
        
    usuario.nombre = datos.nombre
    usuario.curp = datos.curp
    usuario.calle = datos.calle
    usuario.numero = datos.numero
    usuario.colonia = datos.colonia
    usuario.cp = datos.cp
    
    if datos.foto_perfil:
        usuario.foto_perfil = datos.foto_perfil
        
    db.commit()
    return {"mensaje": "Perfil actualizado con éxito"}

class CrearTicket(BaseModel):
    asunto: str
    mensaje: str
    transaccion_id: Optional[int] = None

# 1. El estudiante envía la falla
@app.post("/api/soporte/ticket")
def crear_ticket(datos: CrearTicket, usuario_actual: models.Usuario = Depends(obtener_usuario_actual), db: Session = Depends(get_db)):
    nuevo_ticket = models.TicketSoporte(
        usuario_id=usuario_actual.usuario_id,
        asunto=datos.asunto,
        mensaje=datos.mensaje,
        transaccion_id=datos.transaccion_id
    )
    db.add(nuevo_ticket)
    db.commit()
    return {"mensaje": "Reporte enviado al centro de atención."}

# 2. El Administrador lee el buzón completo
@app.get("/api/admin/tickets")
def listar_tickets(db: Session = Depends(get_db)):
    # Cambiamos order_style por order_by
    return db.query(models.TicketSoporte).order_by(
        models.TicketSoporte.es_importante.desc(), 
        models.TicketSoporte.fecha_creacion.desc()
    ).all()

# 3. El Admin alterna los estados (Marcar como leído o Importante)
@app.put("/api/admin/tickets/{ticket_id}/acciones")
def actualizar_acciones_ticket(ticket_id: int, revisado: Optional[bool] = None, importante: Optional[bool] = None, db: Session = Depends(get_db)):
    ticket = db.query(models.TicketSoporte).filter(models.TicketSoporte.id == ticket_id).first()
    if not ticket:
        raise HTTPException(status_code=404, detail="Ticket no encontrado")
        
    if revisado is not None:
        ticket.revisado = revisado
    if importante is not None:
        ticket.es_importante = importante
        
    db.commit()
    return {"status": "Actualizado correctamente"}

# Esquema que exige el monto y la llave de seguridad
class RecargaEmergencia(BaseModel):
    monto: float
    pin_seguridad: str

@app.put("/api/tarjetas/{tarjeta_id}/recarga-manual")
def recarga_manual_admin(tarjeta_id: int, datos: RecargaEmergencia, db: Session = Depends(get_db)):
    # EL BLINDAJE: Definimos un PIN Maestro (En la vida real esto va en variables de entorno o base de datos)
    PIN_MAESTRO = "2026" 
    
    if datos.pin_seguridad != PIN_MAESTRO:
        raise HTTPException(status_code=401, detail="NIP de Autorización Inválido. Incidente reportado.")
        
    tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.tarjeta_id == tarjeta_id).first()
    if not tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no encontrada")
        
    # Aplicamos la recarga física
    tarjeta.saldo += datos.monto
    
    # (Opcional pero recomendado) Aquí guardaríamos el registro en la tabla "Transacciones"
    # transaccion = models.Transaccion(tarjeta_id=tarjeta_id, monto=datos.monto, tipo="Recarga Manual Admin", estatus="Completada")
    # db.add(transaccion)
    
    db.commit()
    return {"mensaje": f"Recarga de ${datos.monto} exitosa", "nuevo_saldo": tarjeta.saldo}

# Esquema universal para acciones que solo requieren el NIP
class AccionSegura(BaseModel):
    pin_seguridad: str

# 1. BLINDAJE PARA CAMBIAR ESTATUS (Bloquear/Activar)
@app.put("/api/tarjetas/{tarjeta_id}/estatus-seguro")
def cambiar_estatus_seguro(tarjeta_id: int, datos: AccionSegura, db: Session = Depends(get_db)):
    PIN_MAESTRO = "2026"
    if datos.pin_seguridad != PIN_MAESTRO:
        raise HTTPException(status_code=401, detail="NIP Inválido. Operación bloqueada.")
        
    tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.tarjeta_id == tarjeta_id).first()
    if not tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no encontrada")
        
    # Alternamos el estatus
    tarjeta.estatus = "Bloqueada" if tarjeta.estatus == "Activa" else "Activa"
    db.commit()
    return {"mensaje": f"Estatus cambiado a {tarjeta.estatus}"}

# 2. BLINDAJE PARA BORRAR TARJETA (Acción Destructiva)
@app.delete("/api/tarjetas/{tarjeta_id}/borrar-seguro")
def eliminar_tarjeta_segura(tarjeta_id: int, datos: AccionSegura, db: Session = Depends(get_db)):
    PIN_MAESTRO = "2026"
    if datos.pin_seguridad != PIN_MAESTRO:
        raise HTTPException(status_code=401, detail="NIP Inválido. Intento de borrado bloqueado.")
        
    tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.tarjeta_id == tarjeta_id).first()
    if not tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no encontrada")
        
    db.delete(tarjeta)
    db.commit()
    return {"mensaje": "Tarjeta eliminada permanentemente del sistema."}

# Esquema para recibir tu respuesta
class RespuestaAdmin(BaseModel):
    respuesta: str

# 1. Endpoint para que el ADMIN guarde su respuesta
@app.put("/api/admin/tickets/{ticket_id}/responder")
def responder_ticket(ticket_id: int, datos: RespuestaAdmin, db: Session = Depends(get_db)):
    ticket = db.query(models.TicketSoporte).filter(models.TicketSoporte.id == ticket_id).first()
    if not ticket:
        raise HTTPException(status_code=404, detail="Ticket no encontrado")
        
    ticket.respuesta = datos.respuesta
    ticket.revisado = True # Lo marcamos como resuelto (verde) automáticamente
    db.commit()
    return {"mensaje": "Respuesta enviada al estudiante"}

# 2. Endpoint para que el ESTUDIANTE vea su historial de quejas
@app.get("/api/soporte/mis-tickets")
def obtener_mis_tickets(usuario_actual: models.Usuario = Depends(obtener_usuario_actual), db: Session = Depends(get_db)):
    tickets = db.query(models.TicketSoporte).filter(models.TicketSoporte.usuario_id == usuario_actual.usuario_id).order_by(models.TicketSoporte.fecha_creacion.desc()).all()
    return tickets