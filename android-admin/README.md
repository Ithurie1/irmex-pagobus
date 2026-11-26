# IrMex Admin (app móvil)

App Android para **Administradores y Superadministradores** de IrMex. Consume el backend FastAPI ya desplegado
(`https://irmex-pagobus.onrender.com/`), que **no se modifica**.

## Stack
Kotlin 100% · Jetpack Compose + Material 3 · MVVM + Clean Architecture · Hilt · Retrofit + OkHttp (interceptor JWT) ·
Room (caché offline) · DataStore (sesión) · Coroutines + Flow/StateFlow.

## Módulos
Menú de inicio con una tarjeta por módulo (igual que el panel web): Gestión de Tarjetas, Estatus de Usuario,
Gestor de Privilegios (solo superadmin, cambio de rol con NIP), Reportes de Uso, Verificar Documentos, Centro de Soporte y Mi Perfil.
La gestión de tarjetas incluye alta, ajuste de vigencia, recarga manual, bloqueo y borrado (las acciones sensibles piden el NIP maestro).

## Arquitectura (`app/src/main/java/com/irmex/admin`)
| Capa | Contenido |
|---|---|
| `domain` | Modelos, interfaces de repositorio y casos de uso (Kotlin puro, sin Android). Aquí se valida el rol en el login. |
| `data` | `remote` (Retrofit, DTOs, `AuthInterceptor`, `ErrorMapper`), `local` (Room + DataStore), `repository` (implementaciones offline-first). |
| `presentation` | Composables "tontos" + `ViewModel`s con `StateFlow`. Sin lógica en la UI. |
| `di` | Módulos Hilt (red, base de datos, repositorios). |

## Flujo offline
La UI siempre observa Room (`Flow`). Al abrir una pantalla o deslizar para refrescar se descarga del servidor y se reemplaza
la caché; si no hay internet se muestra la última información y un Snackbar. Se cachean: tarjetas, usuarios, tickets y
renovaciones pendientes.

## Centro de soporte
Buzón con tres carpetas (Bandeja, Destacados y Leídos) como en la web. Cada ticket se puede marcar como leído, destacar,
responder o eliminar (con confirmación). La bandeja muestra cuántos mensajes hay sin leer. Todo funciona sobre la caché de Room.

## Reportes de uso
Tarjetas de métricas, gráfica de barras de ingresos contra gastos (dibujada con Compose, sin librerías) y la auditoría de los
últimos 50 accesos. El resumen y los accesos se guardan en Room, así que se ven también sin internet.

## Verificar documentos
Antes de aprobar o rechazar una renovación se puede abrir el documento del estudiante en un visor a pantalla completa:
las imágenes (JPG/PNG) se muestran directo y los PDF se convierten a imágenes con `PdfRenderer` (hasta 5 páginas).

## Seguridad de la sesión
El token JWT se guarda en DataStore **cifrado con AES-256-GCM**; la llave está en Android Keystore y no sale del dispositivo
(`data/local/TokenCipher.kt`). Si el dato no se puede descifrar, la app lo trata como sesión cerrada y pide iniciar sesión.

## Control de acceso
`LoginUseCase` revisa el campo `rol` de `/api/login`; solo `admin` y `superadmin` entran. Un estudiante ve un diálogo y no se
guarda su sesión. Si el token expira (401) se cierra la sesión y se limpia la caché.

## Cómo ejecutarla
1. Abrir la carpeta `android-admin/` con Android Studio (Ladybug o superior, JDK 17).
2. Esperar la sincronización de Gradle y ejecutar en un emulador o teléfono.
3. La URL del backend está en `app/build.gradle.kts` (`API_BASE_URL`).

> El servicio gratuito de Render se duerme: la primera petición puede tardar hasta ~1 minuto (timeouts de 60 s).
