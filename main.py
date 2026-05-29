from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from passlib.context import CryptContext
from datetime import datetime, timedelta
import jwt
from fastapi.security import OAuth2PasswordBearer
from fastapi.middleware.cors import CORSMiddleware
import models, schemas
from database import engine, SessionLocal
# =========================================================
# 1. CREACIÓN DE LA APP Y BASE DE DATOS
# =========================================================
models.Base.metadata.create_all(bind=engine)
app = FastAPI()

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
    return {"mensaje": "¡El backend de IrMex está vivo y seguro!"}

@app.post("/api/registro")
def registrar_usuario(usuario: schemas.UsuarioCrear, db: Session = Depends(get_db)):
    db_usuario = db.query(models.Usuario).filter(models.Usuario.correo == usuario.correo).first()
    if db_usuario:
        raise HTTPException(status_code=400, detail="Este correo ya está registrado")
    
    hashed_password = obtener_password_hash(usuario.password)
    
    nuevo_usuario = models.Usuario(
        nombre=usuario.nombre,
        correo=usuario.correo,
        password_hash=hashed_password,
        fecha_nacimiento=usuario.fecha_nacimiento
    )
    
    db.add(nuevo_usuario)
    db.commit()
    db.refresh(nuevo_usuario)
    
    return {"mensaje": "Usuario creado con éxito de forma segura", "usuario_id": nuevo_usuario.usuario_id}

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
    
    # 4. Entregamos el token al frontend
    return {
        "access_token": token_jwt, 
        "token_type": "bearer",
        "mensaje": "Inicio de sesión exitoso", 
        "usuario_id": db_usuario.usuario_id, 
        "nombre": db_usuario.nombre
    }

# RUTA PROTEGIDA: Solo usuarios con token pueden ver su perfil
@app.get("/api/perfil")
def leer_perfil(usuario_actual: models.Usuario = Depends(obtener_usuario_actual)):
    # Devuelve los datos reales del usuario logueado
    return {
        "nombre": usuario_actual.nombre,
        "correo": usuario_actual.correo,
        "fecha_nacimiento": usuario_actual.fecha_nacimiento
    }

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

@app.get("/api/tarjetas")
def obtener_tarjetas(db: Session = Depends(get_db)):
    tarjetas = db.query(models.Tarjeta).all()
    return tarjetas

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
@app.post("/api/recargar")
def recargar_saldo(data: dict, db: Session = Depends(get_db)):
    # 1. Buscamos la tarjeta
    tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.tarjeta_id == data["tarjeta_id"]).first()
    if not tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no encontrada")
    
    # 2. Sumamos el saldo
    tarjeta.saldo += float(data["monto"])
    
    # 3. Guardamos el historial
    nuevo_mov = models.Movimiento(
        tarjeta_id=tarjeta.tarjeta_id,
        descripcion="Recarga de Saldo",
        monto=float(data["monto"]),
        fecha=datetime.now().strftime("%d/%m/%Y %H:%M")
    )
    db.add(nuevo_mov)
    db.commit()
    return {"mensaje": "Recarga exitosa", "nuevo_saldo": tarjeta.saldo}

# RUTA PARA VER EL HISTORIAL
@app.get("/api/historial/{tarjeta_id}")
def obtener_historial(tarjeta_id: int, db: Session = Depends(get_db)):
    return db.query(models.Movimiento).filter(models.Movimiento.tarjeta_id == tarjeta_id).all()