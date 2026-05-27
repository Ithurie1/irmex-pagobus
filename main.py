from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from passlib.context import CryptContext
import models, schemas
from database import engine, SessionLocal
from fastapi.middleware.cors import CORSMiddleware

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def obtener_password_hash(password):
    return pwd_context.hash(password)

# NUEVA FUNCIÓN: Verifica si la contraseña de texto coincide con el hash guardado
def verificar_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

models.Base.metadata.create_all(bind=engine)

app = FastAPI()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

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

# NUEVA RUTA: Inicio de sesión
@app.post("/api/login")
def iniciar_sesion(usuario: schemas.UsuarioLogin, db: Session = Depends(get_db)):
    # 1. Buscar al usuario por correo
    db_usuario = db.query(models.Usuario).filter(models.Usuario.correo == usuario.correo).first()
    
    # 2. Si no existe o la contraseña no coincide, rechazamos el acceso
    if not db_usuario or not verificar_password(usuario.password, db_usuario.password_hash):
        raise HTTPException(status_code=401, detail="Correo o contraseña incorrectos")
    
    # 3. Si todo está bien, damos la bienvenida
    return {
        "mensaje": "Inicio de sesión exitoso", 
        "usuario_id": db_usuario.usuario_id, 
        "nombre": db_usuario.nombre
    }

# Configuración de CORS para permitir que el frontend se comunique con el backend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Permite peticiones de cualquier origen (ideal para desarrollo local)
    allow_credentials=True,
    allow_methods=["*"],  # Permite todos los métodos (GET, POST, etc.)
    allow_headers=["*"],  # Permite todos los encabezados
)


# CREATE: Registrar una nueva tarjeta
@app.post("/api/tarjetas", response_model=schemas.TarjetaRespuesta)
def crear_tarjeta(tarjeta: schemas.TarjetaCrear, db: Session = Depends(get_db)):
    # Verificamos que la tarjeta no exista ya
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

# READ: Obtener todas las tarjetas
@app.get("/api/tarjetas")
def obtener_tarjetas(db: Session = Depends(get_db)):
    tarjetas = db.query(models.Tarjeta).all()
    return tarjetas

# UPDATE: Modificar el estatus de una tarjeta
@app.put("/api/tarjetas/{tarjeta_id}")
def actualizar_tarjeta(tarjeta_id: int, tarjeta_act: schemas.TarjetaActualizar, db: Session = Depends(get_db)):
    db_tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.tarjeta_id == tarjeta_id).first()
    if not db_tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no encontrada")
    
    db_tarjeta.estatus = tarjeta_act.estatus
    db.commit()
    db.refresh(db_tarjeta)
    return db_tarjeta

# DELETE: Eliminar una tarjeta del sistema
@app.delete("/api/tarjetas/{tarjeta_id}")
def eliminar_tarjeta(tarjeta_id: int, db: Session = Depends(get_db)):
    db_tarjeta = db.query(models.Tarjeta).filter(models.Tarjeta.tarjeta_id == tarjeta_id).first()
    if not db_tarjeta:
        raise HTTPException(status_code=404, detail="Tarjeta no encontrada")
    
    db.delete(db_tarjeta)
    db.commit()
    return {"mensaje": "Tarjeta eliminada con éxito"}