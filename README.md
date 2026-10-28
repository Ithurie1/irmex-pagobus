# IrMex — Sistema de Gestión de Transporte Estudiantil

> **Estado del proyecto:** Desarrollo activo — Funcionalidades core implementadas; integración de pasarela de pago y envío real de correos en desarrollo.

---

## 1. Resumen Ejecutivo

IrMex es una plataforma web de gestión integral para el transporte público estudiantil. Resuelve la problemática de la **gestión manual y descentralizada** de tarjetas de tránsito: registro de usuarios, recarga digital de saldo, renovación documental y auditoría de accesos, todo bajo un único backend RESTful.

La plataforma diferencia dos roles de operación: el **estudiante**, que gestiona su tarjeta y saldo, y el **administrador**, que supervisa el sistema completo a través de un panel de control con reportes, buzón de soporte y control de privilegios. Un simulador de validador de autobús permite probar el ciclo completo de cobro de pasaje sin hardware físico.

---

## 2. Arquitectura y Tecnologías

### Stack Verificado

| Capa | Tecnología | Justificación técnica |
|---|---|---|
| **Backend / API** | FastAPI (Python) | Alto rendimiento asíncrono, generación automática de documentación OpenAPI, tipado estático con Pydantic |
| **ORM / Base de datos** | SQLAlchemy + PostgreSQL | Integridad relacional con llaves foráneas, migraciones de esquema controladas por `metadata.create_all()` |
| **Autenticación** | JWT (`PyJWT`) + Bcrypt (`passlib`) | Tokens de corta vida (60 min), contraseñas con hash irreversible; dependencia de seguridad inyectada vía `Depends` |
| **Servidor ASGI** | Uvicorn | Servidor production-ready, recarga en caliente durante desarrollo |
| **Frontend** | HTML5 + CSS3 (Vanilla) + JavaScript (Vanilla) | Sin framework frontend; comunicación con el backend exclusivamente vía `fetch` / `XMLHttpRequest` |
| **Archivos estáticos** | FastAPI `StaticFiles` | Montaje directo de `/static` (CSS, JS, imágenes) y `/documentos_seguros` (bóveda de documentos) |

### Modelo de Autenticación

```
[Frontend] --POST /api/login--> [FastAPI]
                                    |
                             Verifica bcrypt hash
                                    |
                         Emite JWT (sub=correo, exp=60min)
                                    |
[Frontend] almacena token en localStorage
[Frontend] --GET /api/perfil (Bearer Token)--> [FastAPI Depends(obtener_usuario_actual)]
```

---

## 3. Estructura del Proyecto

```
Irmex/
├── run.py                      # Punto de entrada — lanza uvicorn programáticamente
├── iniciar_irmex.bat           # Automatización: PostgreSQL > venv > uvicorn > browser
├── .env                        # Variables de entorno (vacío actualmente — ver §4)
├── Modelo Relacional.jpeg      # Diagrama del modelo de datos
│
├── backend/
│   ├── main.py                 # Aplicación FastAPI completa (1175 líneas, ~40 endpoints)
│   ├── models.py               # Modelos SQLAlchemy (9 tablas)
│   ├── schemas.py              # Esquemas Pydantic de validación de entrada
│   ├── database.py             # Configuración del motor SQLAlchemy y SessionLocal
│   └── __init__.py
│
├── frontend/                   # Páginas HTML servidas por FastAPI (FileResponse)
│   ├── index.html              # Login
│   ├── dashboard.html          # Panel principal del administrador
│   ├── registro.html           # Registro de nuevos usuarios
│   ├── recarga.html            # Módulo de recarga de saldo
│   ├── historial.html          # Historial de movimientos por tarjeta
│   ├── perfil.html             # Perfil y datos personales del usuario
│   ├── renovacion.html         # Solicitud de renovación (subida de documentos)
│   ├── crud_tarjetas.html      # Gestión administrativa de tarjetas
│   ├── reportes.html           # Panel de reportes financieros y operativos
│   ├── buzon_admin.html        # Sistema de tickets de soporte
│   ├── verificar_estatus.html  # Consulta pública de estatus de tarjeta
│   ├── verificador_documentos.html  # Verificación de renovaciones (admin)
│   ├── estaciones.html         # CRUD de estaciones de transporte
│   ├── simulador.html          # Simulador de validador de autobús
│   ├── gestor_privilegios.html # Gestión de roles de usuario
│   ├── recuperar.html          # Recuperación de contraseña
│   └── restablecer.html        # Restablecimiento de contraseña
│
├── static/
│   ├── css/style.css           # Hoja de estilos global (~19 KB)
│   ├── js/                     # Scripts frontend, uno por vista (17 archivos)
│   └── uploads/                # Evidencias adjuntas a tickets de soporte
│
├── documentos_seguros/         # Bóveda de documentos de renovación (acceso controlado)
└── zUTILS/                     # Archivos de referencia y snapshots de desarrollo
```

---

## 4. Guía de Instalación y Ejecución Local

### Prerrequisitos

- Python 3.10+
- PostgreSQL 14+
- `pip` y capacidad de crear entornos virtuales

### 4.1 Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd Irmex
```

### 4.2 Crear y activar el entorno virtual

```bash
# Windows
python -m venv venv
venv\Scripts\activate

# Linux / macOS
python3 -m venv venv
source venv/bin/activate
```

### 4.3 Instalar dependencias

> **Nota:** El proyecto no incluye un `requirements.txt` en este momento. Las dependencias requeridas, verificadas en el código fuente, son:

```bash
pip install fastapi uvicorn sqlalchemy psycopg2-binary passlib[bcrypt] pyjwt python-multipart
```

### 4.4 Configuración de base de datos

La cadena de conexión se define en `backend/database.py` (línea 8):

```python
SQLALCHEMY_DATABASE_URL = f"postgresql://postgres:{password}@localhost:5432/irmex_db"
```

Crear la base de datos antes de ejecutar:

```sql
CREATE DATABASE irmex_db;
```

> **Nota de seguridad:** Las credenciales de BD y el `SECRET_KEY` del JWT están actualmente en texto plano en el código. Para producción deben migrarse a variables de entorno.

### 4.5 Iniciar el servidor

**Opción A — Script de Python:**

```bash
python run.py
```

**Opción B — Uvicorn directo:**

```bash
uvicorn backend.main:app --reload
```

**Opción C — Lanzador automatizado (Windows):**

```bat
iniciar_irmex.bat
```

La aplicación queda disponible en:
- Aplicación web: `http://127.0.0.1:8000`
- Swagger UI (docs): `http://127.0.0.1:8000/docs`
- ReDoc: `http://127.0.0.1:8000/redoc`

---

## 5. Documentación de la API

### 5.1 Autenticación y Registro

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/registro` | Registra un nuevo usuario. Si el correo contiene `"admin"`, se asigna rol administrativo automáticamente. Para usuarios regulares genera una tarjeta de 16 dígitos en estado `"Inactiva"`. |
| `POST` | `/api/login` | Autentica con correo y contraseña (bcrypt). Devuelve JWT Bearer, `usuario_id`, `nombre`, `es_estudiante` y `rol`. Registra el acceso en `logs_acceso`. |

### 5.2 Gestión de Perfil

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/perfil` | Devuelve el perfil del usuario autenticado vía JWT. Ruta protegida. |
| `PUT` | `/api/perfil` | Actualiza campos del perfil autenticado (`nombre`, `fecha_nacimiento`, `curp`, `calle`, `numero`, `colonia`, `cp`). Ruta protegida. |
| `GET` | `/api/perfil/{correo}` | Obtiene perfil completo incluyendo `foto_perfil` y `fecha_registro`. |
| `PUT` | `/api/perfil/{correo}` | Actualiza perfil completo por correo. |

### 5.3 Recuperación de Contraseña

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/recuperar-password` | Valida correo y fecha de nacimiento; si coinciden, actualiza la contraseña. **El envío de correo real está simulado mediante `print()` en la terminal.** |
| `POST` | `/api/reset-password` | Restablece la contraseña a partir de correo y nueva contraseña, sin verificación secundaria. |

### 5.4 Gestión de Tarjetas

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/tarjetas` | Lista todas las tarjetas con propietario (`joinedload`) y fecha de expiración formateada. |
| `POST` | `/api/tarjetas` | Crea una tarjeta nueva. Valida número de 16 dígitos (Pydantic regex), unicidad y que el usuario no tenga ya una tarjeta `"Activa"`. |
| `PUT` | `/api/tarjetas/{tarjeta_id}` | Actualiza el estatus de una tarjeta. |
| `DELETE` | `/api/tarjetas/{tarjeta_id}` | **Soft delete:** cambia estatus a `"Inactiva"`, no elimina el registro. |
| `PUT` | `/api/tarjetas/{tarjeta_id}/vigencia` | Suma o resta días a `fecha_expiracion`. Si no tiene fecha, inicia desde hoy. |
| `PUT` | `/api/tarjetas/{tarjeta_id}/recarga-manual` | Recarga de emergencia protegida por NIP Maestro. |
| `PUT` | `/api/tarjetas/{tarjeta_id}/estatus-seguro` | Alterna `Activa` / `Bloqueada` con verificación de NIP Maestro. |
| `DELETE` | `/api/tarjetas/{tarjeta_id}/borrar-seguro` | **Hard delete con cascada manual:** elimina movimientos, transacciones y tickets asociados, protegido por NIP Maestro. |
| `GET` | `/api/mis-tarjetas` | Tarjetas del usuario autenticado. Calcula `requiere_renovacion_urgente` si la vigencia es <= 15 días. Ruta protegida. |

### 5.5 Recargas e Historial

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/recargar` | Incrementa el saldo y registra un `Movimiento` con `tipo="Recarga de Saldo"`. |
| `GET` | `/api/historial/{tarjeta_id}` | Devuelve todos los movimientos de una tarjeta. |

### 5.6 Simulador de Validador de Autobús

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/cobrar-pasaje` | Simula el cobro de pasaje (costo fijo: $10.00). Verifica existencia, estatus `"Activa"` y saldo suficiente. Devuelve `saldo_restante`. |

### 5.7 Renovación de Tarjeta

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/solicitar-renovacion` | El estudiante sube un documento (INE, PDF). Se guarda con nombre normalizado `ine_estudiante_{id}.ext` en `documentos_seguros/`. Rechaza si ya existe una solicitud `"Pendiente"`. Ruta protegida. |
| `GET` | `/api/renovaciones/pendientes` | Lista solicitudes pendientes (nombre, correo, fecha, nombre de archivo — sin ruta completa). |
| `PUT` | `/api/renovaciones/{renovacion_id}` | Aprueba o rechaza. Si se aprueba: activa la tarjeta y asigna `fecha_expiracion = hoy + 180 días`. |
| `GET` | `/api/documentos-seguros/{nombre_archivo}` | Sirve el documento físico desde la bóveda, previa verificación de existencia. |

### 5.8 Estaciones de Transporte

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/estaciones` | Registra una estación con `nombre` y `tipo`. Valida unicidad por nombre. |
| `GET` | `/api/estaciones` | Lista todas las estaciones con `nombre`, `tipo` y `estatus`. |
| `PUT` | `/api/estaciones/{estacion_id}` | Alterna estatus entre `"Operativa"` y `"Mantenimiento"`. |

### 5.9 Sistema de Soporte (Buzón de Tickets)

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/soporte/ticket` | El estudiante crea un ticket con `asunto`, `mensaje`, `transaccion_id` opcional y archivo de evidencia (multipart). Ruta protegida. |
| `GET` | `/api/soporte/mis-tickets` | Historial de tickets del usuario autenticado. Ruta protegida. |
| `GET` | `/api/admin/tickets` | Lista todos los tickets ordenados por `es_importante DESC`, `fecha_creacion DESC`. |
| `GET` | `/api/admin/tickets/no-leidos` | Conteo de tickets con `revisado=False`. |
| `PUT` | `/api/admin/tickets/{ticket_id}/responder` | Guarda la respuesta del admin y marca como `revisado=True`. |
| `PUT` | `/api/admin/tickets/{id}/acciones` | Actualiza `es_importante` y/o `revisado`. |
| `PUT` | `/api/admin/tickets/{id}/leer` | Marca un ticket como leído. |
| `DELETE` | `/api/admin/tickets/{id}` | Elimina permanentemente un ticket. |

### 5.10 Auditoría y Reportes

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/logs` | Últimos 50 registros de acceso con correo, fecha/hora y rol. |
| `GET` | `/api/admin/auditoria/exportar` | Exporta todos los logs de acceso como `auditoria_irmex.csv`. |
| `GET` | `/api/reportes/general` | Retorna: total de estudiantes, tarjetas activas/inactivas, suma de recargas y suma de cobros de pasaje. |
| `GET` | `/api/admin/finanzas/exportar` | Exporta movimientos de tipo Recarga en CSV. Acepta `?filtro=total|mensual|semanal`. |

### 5.11 Gestión de Usuarios y Roles

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/usuarios_completos` | Lista todos los usuarios con tarjeta vigente y rol. Usa `joinedload`. |
| `GET` | `/api/estudiantes` | Lista usuarios con `es_estudiante=True`. |
| `DELETE` | `/api/usuarios/{id}` | **Soft delete:** marca usuario como `"inactivo"` y sus tarjetas como `"inactiva"`. |
| `PUT` | `/api/usuarios/{usuario_id}/rol` | Cambia el rol. Requiere NIP Maestro para roles estándar; NIP Raíz para cuentas `"superadmin"`. |
| `GET` | `/api/verificar-estudiante/{correo}` | Consulta pública de estatus de tarjeta por correo. |
| `POST` | `/api/asignar-tarjeta/{correo}` | Desactiva tarjetas anteriores del estudiante y genera una nueva en estado `"Activa"`. |

---

## 6. Modelo Relacional de Base de Datos

### Tablas definidas en `models.py`

| Tabla | Descripción | Columnas clave |
|---|---|---|
| `usuarios` | Todos los usuarios del sistema | `usuario_id` PK, `correo` UNIQUE, `password_hash`, `rol`, `estatus`, `curp`, dirección, `foto_perfil` |
| `tarjetas` | Tarjetas de tránsito | `tarjeta_id` PK, `numero_tarjeta` UNIQUE (16 dígitos), `saldo` INT, `estatus`, `fecha_expiracion`, `usuario_id` FK |
| `movimientos` | Historial de transacciones por tarjeta | `id` PK, `tarjeta_id` FK, `tipo`, `monto` FLOAT, `fecha_hora` STRING, `origen`, `destino` |
| `estaciones` | Catálogo de estaciones | `id` PK, `nombre` UNIQUE, `tipo`, `estatus` |
| `renovaciones` | Solicitudes de renovación | `id` PK, `usuario_id` FK, `archivo_ruta`, `estatus`, `fecha` |
| `historial` | Historial financiero por usuario | `id` PK, `usuario_id` FK, `tipo`, `monto`, `fecha` |
| `logs_acceso` | Auditoría de accesos exitosos | `id` PK, `correo_usuario`, `fecha_hora` DATETIME |
| `transacciones` | Transacciones con referencia de pago | `id` PK, `tarjeta_id` FK, `monto`, `tipo`, `estatus`, `referencia_pago` UNIQUE |
| `tickets_soporte` | Buzón de soporte y quejas | `id` PK, `usuario_id` FK, `asunto`, `mensaje`, `revisado` BOOL, `es_importante` BOOL, `transaccion_id` FK, `respuesta`, `evidencia_url` |

[INSERTAR CAPTURA DE PANTALLA: Modelo Relacional — Diagrama entidad-relacion]

---

## 7. Vistas de la Interfaz

[INSERTAR CAPTURA DE PANTALLA: Pantalla de Login — index.html]

[INSERTAR CAPTURA DE PANTALLA: Panel de Administrador — dashboard.html con KPIs]

[INSERTAR CAPTURA DE PANTALLA: Modulo de Recarga de Saldo — recarga.html]

[INSERTAR CAPTURA DE PANTALLA: Gestion de Tarjetas (CRUD) — crud_tarjetas.html]

[INSERTAR CAPTURA DE PANTALLA: Buzon de Soporte Administrativo — buzon_admin.html]

[INSERTAR CAPTURA DE PANTALLA: Simulador de Validador de Autobus — simulador.html]

[INSERTAR CAPTURA DE PANTALLA: Panel de Reportes Financieros — reportes.html]

[INSERTAR CAPTURA DE PANTALLA: Gestion de Roles — gestor_privilegios.html]

---

## 8. Estado Actual del Proyecto

> El proyecto se encuentra en **fase de desarrollo activo**.

### Implementado y funcional

- [x] Registro, autenticacion JWT y gestion de sesion
- [x] CRUD completo de tarjetas con validacion Pydantic y soft delete
- [x] Sistema de recarga de saldo y cobro de pasaje
- [x] Subida y custodia segura de documentos de renovacion
- [x] Flujo completo de aprobacion/rechazo de renovaciones (activa tarjeta + asigna 180 dias)
- [x] Panel de reportes con metricas financieras y exportacion CSV
- [x] Sistema de auditoria de accesos con exportacion CSV
- [x] Buzon de soporte con tickets, evidencias, marcado de importancia y respuestas
- [x] Gestion de roles con NIP Maestro y NIP Raiz
- [x] Simulador de validador de autobus
- [x] CRUD de estaciones de transporte
- [x] Alertas de renovacion urgente (<= 15 dias para expirar)

### Pendiente / En desarrollo

- [ ] Integracion con pasarela de pago real (tabla `transacciones` y campo `referencia_pago` modelados; endpoint de recarga manual actualmente protegido solo por NIP)
- [ ] Envio real de correo de recuperacion de contrasena (actualmente simulado mediante `print()` en terminal)
- [ ] Generacion de archivo `requirements.txt`
- [ ] Migracion de credenciales (`SECRET_KEY`, contrasena de BD, NIPs) a variables de entorno
- [ ] Restriccion de CORS para produccion (actualmente `allow_origins=["*"]`)
- [ ] Implementacion de verificacion de roles en rutas protegidas (actualmente la mayoria de endpoints administrativos no validan el rol del token JWT)

---

## 9. Notas de Seguridad para Revision Tecnica

Los siguientes aspectos constituyen deuda tecnica documentada, pendiente de resolucion antes de un despliegue en produccion:

1. **`SECRET_KEY` en codigo fuente** — `main.py` linea 65. Debe migrarse a variable de entorno.
2. **NIPs en codigo fuente** — El NIP Maestro y el NIP Raiz estan definidos como literales en `main.py`. Deben almacenarse en variables de entorno o tabla de configuracion cifrada.
3. **CORS abierto** — `allow_origins=["*"]` es adecuado solo para desarrollo local.
4. **Contrasena de BD sin `.env`** — `database.py` construye la URL con la contrasena directamente. El archivo `.env` existe en el repositorio pero no contiene valores.
5. **Autorizacion de rutas admin** — Los endpoints del panel de administracion no validan el rol del token JWT entrante en la mayoria de los casos.

---

*Generado a partir de analisis directo del codigo fuente. Ultima revision: octubre 2026.*
