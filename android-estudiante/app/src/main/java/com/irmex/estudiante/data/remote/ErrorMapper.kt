package com.irmex.estudiante.data.remote

import com.google.gson.JsonParser
import com.irmex.estudiante.domain.model.Outcome
import retrofit2.HttpException
import java.io.IOException

/** Convierte cualquier fallo en un mensaje amigable; así nunca se propaga una excepción a la UI. */
object ErrorMapper {

    fun toFailure(error: Throwable): Outcome.Failure = when (error) {
        is HttpException -> {
            val detalle = leerDetalle(error)
            when (error.code()) {
                401 -> Outcome.Failure(detalle ?: "Tu sesión expiró. Inicia sesión de nuevo.", unauthorized = true)
                403 -> Outcome.Failure(detalle ?: "No tienes permisos para realizar esta acción.")
                404 -> Outcome.Failure(detalle ?: "No se encontró el recurso solicitado.")
                in 500..599 -> Outcome.Failure("El servidor tuvo un problema. Intenta más tarde.")
                else -> Outcome.Failure(detalle ?: "No se pudo completar la solicitud.")
            }
        }
        is IOException -> Outcome.Failure("Sin conexión con el servidor. Mostrando la última información guardada.")
        else -> Outcome.Failure("Ocurrió un error inesperado.")
    }

    private fun leerDetalle(error: HttpException): String? = try {
        val cuerpo = error.response()?.errorBody()?.string()
        val detail = JsonParser.parseString(cuerpo).asJsonObject.get("detail")
        if (detail != null && detail.isJsonPrimitive) detail.asString else null
    } catch (_: Exception) {
        null
    }
}

/** Ejecuta una llamada de red y devuelve [Outcome] sin lanzar excepciones. */
suspend inline fun <T> safeCall(crossinline block: suspend () -> T): Outcome<T> = try {
    Outcome.Success(block())
} catch (e: kotlinx.coroutines.CancellationException) {
    throw e
} catch (e: Exception) {
    ErrorMapper.toFailure(e)
}
