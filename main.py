from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
import models, schemas
from database import engine, SessionLocal

# Crea las tablas si no existen
models.Base.metadata.create_all(bind=engine)

app = FastAPI()

# Esta función abre una "puerta" a la base de datos por cada petición y la cierra al terminar
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# --- AQUÍ EMPIEZAN TUS RUTAS (ENDPOINTS) ---

@app.get("/")
def leer_raiz():
    return {"mensaje": "¡El backend de IrMex está vivo!"}

@app.post("/api/registro")
def registrar_usuario(usuario: schemas.UsuarioCrear, db: Session = Depends(get_db)):
    # 1. Verificamos si el correo ya existe en la base de datos
    db_usuario = db.query(models.Usuario).filter(models.Usuario.correo == usuario.correo).first()
    if db_usuario:
        raise HTTPException(status_code=400, detail="Este correo ya está registrado")
    
    # 2. Preparamos al nuevo usuario para guardarlo
    # NOTA: Por ahora guardaremos el password tal cual, pero pronto le agregaremos encriptación real
    nuevo_usuario = models.Usuario(
        nombre=usuario.nombre,
        correo=usuario.correo,
        password_hash=usuario.password, 
        fecha_nacimiento=usuario.fecha_nacimiento
    )
    
    # 3. Lo guardamos en SQLite
    db.add(nuevo_usuario)
    db.commit()
    db.refresh(nuevo_usuario) # Actualizamos para obtener el ID que le asignó la base de datos
    
    return {"mensaje": "Usuario creado con éxito", "usuario_id": nuevo_usuario.usuario_id}