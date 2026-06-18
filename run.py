# run.py
# Script de arranque - Ejecutar desde la RAÍZ del proyecto:
#   python run.py
#   ó
#   uvicorn backend.main:app --reload
#
# FastAPI correrá en http://127.0.0.1:8000

import uvicorn

if __name__ == "__main__":
    uvicorn.run(
        "backend.main:app",
        host="127.0.0.1",
        port=8000,
        reload=True
    )
