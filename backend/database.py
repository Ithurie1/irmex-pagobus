import os
from dotenv import load_dotenv
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

# Carga las variables definidas en el archivo .env de la raíz del proyecto
# find_dotenv() sube directorios hasta encontrar el .env automáticamente
load_dotenv()

# Lee la URL completa de conexión desde el entorno (nunca desde el código)
SQLALCHEMY_DATABASE_URL = os.getenv("DATABASE_URL")

if not SQLALCHEMY_DATABASE_URL:
    raise RuntimeError("La variable de entorno DATABASE_URL no está definida. Revisa tu archivo .env")

engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)