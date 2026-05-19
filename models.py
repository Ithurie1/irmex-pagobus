from sqlalchemy import Column, Integer, String, Boolean, DateTime, Date
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