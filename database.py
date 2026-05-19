from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

# Esta línea le dice que cree un archivo llamado irmex.db en tu carpeta
SQLALCHEMY_DATABASE_URL = "sqlite:///./irmex.db"

# Configuramos el "motor" de la base de datos
engine = create_engine(
    SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False}
)

# Creamos la sesión para poder hacer consultas después
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)