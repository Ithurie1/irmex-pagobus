from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from passlib.context import CryptContext
import models, schemas
from database import engine, SessionLocal

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