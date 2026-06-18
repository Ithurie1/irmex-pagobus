from sqlalchemy import Column, Integer, String, Boolean, ForeignKey, Float, DateTime, Date
from sqlalchemy.orm import declarative_base
from datetime import datetime

Base = declarative_base()

class Usuario(Base):
    __tablename__ = "usuarios"

    usuario_id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String(100), index=True)
    correo = Column(String(100), unique=True, index=True)
    password_hash = Column(String(255)) # Agregado por seguridad
    creado_en = Column(DateTime, default=datetime.utcnow)
    
    # Algunos campos del perfil (agregados aquí para facilitar el registro inicial)
    fecha_nacimiento = Column(Date, nullable=True) 
    es_estudiante = Column(Boolean, default=False)

class Tarjeta(Base):
    __tablename__ = "tarjetas"

    tarjeta_id = Column(Integer, primary_key=True, index=True)
    numero_tarjeta = Column(String(16), unique=True, index=True)
    saldo = Column(Integer, default=0) # Usamos Integer o Float para el dinero
    estatus = Column(String(20), default="Activa")    

class Movimiento(Base):
    __tablename__ = "movimientos"
    id = Column(Integer, primary_key=True, index=True)
    tarjeta_id = Column(Integer, ForeignKey("tarjetas.tarjeta_id"))
    descripcion = Column(String) # Ej: "Recarga" o "Ruta Troncal"
    monto = Column(Float)
    fecha = Column(String) # Guardaremos la fecha como texto por simplicidad    