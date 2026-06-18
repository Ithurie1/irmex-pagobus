@echo off
title Lanzador Maestro de IrMex
color 0B

echo ===================================================
echo     Iniciando el Sistema IrMex...
echo ===================================================
echo.

:: 1. Levantar la Base de Datos PostgreSQL (Versión Portable)
echo [1/4] Despertando Base de Datos...
cd C:\Users\m2400\OneDrive\Escritorio\pgsql\bin
pg_ctl.exe -D "C:\Users\m2400\OneDrive\Escritorio\pgsql\data" start
timeout /t 2 /nobreak > NUL

:: 2. Navegar a la carpeta del proyecto
cd C:\Users\m2400\OneDrive\Escritorio\Irmex

:: 3. Activar el entorno virtual
echo [2/4] Activando entorno virtual...
call venv\Scripts\activate

:: 4. Levantar el servidor backend (FastAPI)...
echo [3/4] Levantando el servidor backend (FastAPI)...
start "Servidor IrMex" cmd /k "uvicorn backend.main:app --reload"

:: 5. Darle tiempo al servidor para arrancar
echo [4/4] Esperando conexion...
timeout /t 3 /nobreak > NUL

:: 6. Abrir la interfaz web en tu navegador
echo Abriendo aplicacion...
start http://127.0.0.1:8000

:: Cerrar esta ventana
exit