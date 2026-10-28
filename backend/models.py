from sqlalchemy import Column, Integer, String, Float, ForeignKey, Date, DateTime, Boolean, Text
from sqlalchemy.orm import declarative_base, relationship
from datetime import datetime 

Base = declarative_base()

class Usuario(Base):
    __tablename__ = "usuarios"

    usuario_id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String(100), index=True)
    correo = Column(String(100), unique=True, index=True)
    password_hash = Column(String(255))
    creado_en = Column(DateTime, default=datetime.utcnow)
    
    fecha_nacimiento = Column(Date, nullable=True) 
    es_estudiante = Column(Boolean, default=False)
    
    # NUEVOS CAMPOS
    curp = Column(String(18), nullable=True)
    calle = Column(String(100), nullable=True)
    numero = Column(String(20), nullable=True)
    colonia = Column(String(100), nullable=True)
    cp = Column(String(10), nullable=True)
    foto_perfil = Column(String, nullable=True)
    rol = Column(String, default="estudiante")
    estatus = Column(String, default="activo")
    
    tarjetas = relationship("Tarjeta", back_populates="usuario")
class Tarjeta(Base):
    __tablename__ = "tarjetas"

    tarjeta_id = Column(Integer, primary_key=True, index=True)
    numero_tarjeta = Column(String(16), unique=True, index=True)
    saldo = Column(Integer, default=0) # Usamos Integer o Float para el dinero
    estatus = Column(String(20), default="Activa")   
    # NUEVO: Identificador del dueño de la tarjeta
    usuario_id = Column(Integer, ForeignKey("usuarios.usuario_id"), nullable=True) 
    fecha_expiracion = Column(Date, nullable=True)
    
    usuario = relationship("Usuario", back_populates="tarjetas")

class Movimiento(Base):
    __tablename__ = "movimientos"
    id = Column(Integer, primary_key=True, index=True)
    tarjeta_id = Column(Integer, ForeignKey("tarjetas.tarjeta_id"))
    tipo = Column(String) # "Recarga" o "Viaje"
    monto = Column(Float)
    fecha_hora = Column(String) 
    
    # Nuevos campos exigidos por el REQ-004
    origen = Column(String, nullable=True)
    destino = Column(String, nullable=True)


class Estacion(Base):
    __tablename__ = "estaciones"

    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String, unique=True, index=True)
    tipo = Column(String) # Ejemplo: "Troncal", "Alimentadora", "Paradero"
    estatus = Column(String, default="Operativa") 
    
class Renovacion(Base):
    __tablename__ = "renovaciones"

    id = Column(Integer, primary_key=True, index=True)
    usuario_id = Column(Integer, ForeignKey("usuarios.usuario_id"))
    archivo_ruta = Column(String) # Aquí guardaremos la ruta segura de la foto/PDF
    estatus = Column(String, default="Pendiente") # Puede ser Pendiente, Aprobada o Rechazada
    fecha = Column(String)    # "Operativa" o "Mantenimiento"

class Historial(Base):
    __tablename__ = "historial"

    id = Column(Integer, primary_key=True, index=True)
    usuario_id = Column(Integer, ForeignKey("usuarios.usuario_id"))
    tipo = Column(String) # Será "Recarga" o "Pago de Pasaje"
    monto = Column(Float)
    fecha = Column(String)    

class LogAcceso(Base):
    __tablename__ = "logs_acceso"
    
    id = Column(Integer, primary_key=True, index=True)
    correo_usuario = Column(String, index=True)
    fecha_hora = Column(DateTime, default=datetime.now)
    
class Transaccion(Base):
    __tablename__ = "transacciones"

    id = Column(Integer, primary_key=True, index=True)
    tarjeta_id = Column(Integer, ForeignKey("tarjetas.tarjeta_id"))
    monto = Column(Float, nullable=False)
    tipo = Column(String(20)) # "Recarga" o "Cobreso"
    # Estados clave: "Pendiente", "Completada", "Fallida", "En_Disputa"
    estatus = Column(String(20), default="Pendiente") 
    referencia_pago = Column(String(50), unique=True) # ID de Stripe o número de folio
    fecha = Column(DateTime, default=datetime.utcnow)

# ==============================================================================
# TABLA DE AUDITORÍA Y SOPORTE (Buzón)
# ==============================================================================
class TicketSoporte(Base):
    __tablename__ = "tickets_soporte"

    id = Column(Integer, primary_key=True, index=True)
    usuario_id = Column(Integer, ForeignKey("usuarios.usuario_id")) 
    asunto = Column(String(100), nullable=False)
    mensaje = Column(Text, nullable=False)
    revisado = Column(Boolean, default=False) 
    es_importante = Column(Boolean, default=False) 
    transaccion_id = Column(Integer, ForeignKey("transacciones.id"), nullable=True)
    fecha_creacion = Column(DateTime, default=datetime.utcnow)
    respuesta = Column(Text, nullable=True)
    evidencia_url = Column(String, nullable=True)