from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import urllib.parse

# Si tu contraseña tiene símbolos, ponla aquí
password = urllib.parse.quote_plus("2506") 

SQLALCHEMY_DATABASE_URL = f"postgresql://postgres:{password}@localhost:5432/irmex_db"

engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)