import os
from dotenv import load_dotenv
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

# Carga las variables definidas en el archivo .env de la raíz del proyecto
load_dotenv()

# Lee la URL completa de conexión desde el entorno o la construye con DB_PASSWORD
DATABASE_URL = os.getenv("DATABASE_URL")
DB_PASSWORD = os.getenv("DB_PASSWORD")

if not DATABASE_URL and DB_PASSWORD:
    DATABASE_URL = f"postgresql://postgres:{DB_PASSWORD}@localhost:5432/irmex_db"

SQLALCHEMY_DATABASE_URL = DATABASE_URL

if not SQLALCHEMY_DATABASE_URL:
    raise RuntimeError("La variable de entorno DATABASE_URL o DB_PASSWORD no está definida. Revisa tu archivo .env")

engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
