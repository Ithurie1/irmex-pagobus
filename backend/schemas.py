from pydantic import BaseModel
from datetime import date
from typing import Optional
from pydantic import BaseModel, Field

# Esta es la estructura que esperamos recibir desde el frontend
class UsuarioCrear(BaseModel):
    nombre: str
    correo: str
    password: str
    fecha_nacimiento: Optional[date] = None

# Nueva estructura para el inicio de sesión
class UsuarioLogin(BaseModel):
    correo: str
    password: str    


class TarjetaCrear(BaseModel):
    numero_tarjeta: str
    saldo: int
    estatus: str = "Activa"

class TarjetaRespuesta(BaseModel):
    tarjeta_id: int
    numero_tarjeta: str
    saldo: int
    estatus: str

class Config:
    from_attributes = True    

class TarjetaActualizar(BaseModel):
    estatus: str        

class TarjetaCrear(BaseModel):
    # Obligamos a que sean exactamente 16 caracteres numéricos
    numero_tarjeta: str = Field(..., min_length=16, max_length=16, pattern=r"^\d{16}$")
    saldo: int
    estatus: str = "Activa"