# IrMex Admin (app móvil)

App Android para **Administradores y Superadministradores** de IrMex. Consume el backend FastAPI ya desplegado
(`https://irmex-pagobus.onrender.com/`), que **no se modifica**.

## Stack
Kotlin 100% · Jetpack Compose + Material 3 · MVVM + Clean Architecture · Hilt · Retrofit + OkHttp (interceptor JWT) ·
Room (caché offline) · DataStore (sesión) · Coroutines + Flow/StateFlow.

## Arquitectura (`app/src/main/java/com/irmex/admin`)
| Capa | Contenido |
|---|---|
| `domain` | Modelos, interfaces de repositorio y casos de uso (Kotlin puro, sin Android). Aquí se valida el rol en el login. |
| `data` | `remote` (Retrofit, DTOs, `AuthInterceptor`, `ErrorMapper`), `local` (Room + DataStore), `repository` (implementaciones offline-first). |
| `presentation` | Composables "tontos" + `ViewModel`s con `StateFlow`. Sin lógica en la UI. |
| `di` | Módulos Hilt (red, base de datos, repositorios). |

## Flujo offline
La UI siempre observa Room (`Flow`). Al abrir una pantalla o deslizar para refrescar se descarga del servidor y se reemplaza
la caché; si no hay internet se muestra la última información y un Snackbar. Se cachean: tarjetas, estaciones, tickets y
renovaciones pendientes.

## Control de acceso
`LoginUseCase` revisa el campo `rol` de `/api/login`; solo `admin` y `superadmin` entran. Un estudiante ve un diálogo y no se
guarda su sesión. Si el token expira (401) se cierra la sesión y se limpia la caché.

## Cómo ejecutarla
1. Abrir la carpeta `android-admin/` con Android Studio (Ladybug o superior, JDK 17).
2. Esperar la sincronización de Gradle y ejecutar en un emulador o teléfono.
3. La URL del backend está en `app/build.gradle.kts` (`API_BASE_URL`).

> El servicio gratuito de Render se duerme: la primera petición puede tardar hasta ~1 minuto (timeouts de 60 s).
