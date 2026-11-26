# IrMex Estudiante (app móvil)

App Android para **estudiantes** de IrMex: consulta de saldo, recargas, historial, renovación de la tarjeta y reportes de soporte.
Consume el backend FastAPI ya desplegado (`https://irmex-pagobus.onrender.com/`), que **no se modifica**.
Es independiente de la app de administradores (`android-admin/`) y tiene una identidad visual distinta: clara, morada y turquesa,
con una billetera en degradado y barra de navegación inferior.

## Módulos
| Pantalla | Qué hace |
|---|---|
| Acceso | Inicio de sesión, registro (se inicia sesión solo al crear la cuenta) y recuperación de contraseña por correo. Solo entran cuentas de estudiante. |
| Inicio | Billetera con saldo, número de tarjeta enmascarado (con ojito para verlo), estatus y aviso de renovación. |
| Recargar | Monto rápido o libre y formulario de tarjeta bancaria con las mismas validaciones de la web. **El pago es simulado**: los datos de la tarjeta bancaria no se guardan ni se envían. |
| Historial | Recargas y viajes de la tarjeta, del más reciente al más antiguo. |
| Reportes | Historial de reportes de soporte con su respuesta, y formulario para crear uno nuevo con evidencia opcional. |
| Renovar tarjeta | Subir el comprobante de estudios o INE (PDF, JPG o PNG, máx. 10 MB) para que un administrador lo revise. |
| Perfil | Datos personales y domicilio, igual que en la app de administradores. |

## Stack
Kotlin 100% · Jetpack Compose + Material 3 · MVVM + Clean Architecture · Hilt · Retrofit + OkHttp (interceptor JWT) ·
Room (caché offline) · DataStore (sesión) · Coroutines + Flow/StateFlow.

## Arquitectura (`app/src/main/java/com/irmex/estudiante`)
| Capa | Contenido |
|---|---|
| `domain` | Modelos, interfaces de repositorio y casos de uso (Kotlin puro). Aquí se valida el rol, el registro, el pago simulado y los archivos. |
| `data` | `remote` (Retrofit, DTOs, `AuthInterceptor`, `ErrorMapper`), `local` (Room, DataStore, cifrado del token, lector de archivos) y `repository` (offline-first). |
| `presentation` | Composables + `ViewModel`s con `StateFlow`; sin lógica de negocio en la UI. |
| `di` | Módulos Hilt (red, base de datos, repositorios). |

## Offline y seguridad
- La UI siempre observa Room; se cachean la tarjeta, el historial, los reportes y el perfil. Sin internet se ve la última información.
- El token JWT se guarda cifrado (AES-256-GCM con Android Keystore) en DataStore. Si el token expira (401), se cierra la sesión.
- Al cerrar sesión se borra la caché local.

## Cómo ejecutarla
1. Abrir la carpeta `android-estudiante/` con Android Studio (JDK 17 o superior) y esperar la sincronización de Gradle.
2. Ejecutar en un emulador o teléfono.
3. La URL del backend está en `app/build.gradle.kts` (`API_BASE_URL`).

> El servicio gratuito de Render se duerme: la primera petición puede tardar hasta ~1 minuto (timeouts de 60 s).
