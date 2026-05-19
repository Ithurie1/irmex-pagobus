from pydantic import BaseModel
from datetime import date
from typing import Optional

# Esta es la estructura que esperamos recibir desde el frontend
class UsuarioCrear(BaseModel):
    nombre: str
    correo: str
    password: str
    fecha_nacimiento: Optional[date] = None