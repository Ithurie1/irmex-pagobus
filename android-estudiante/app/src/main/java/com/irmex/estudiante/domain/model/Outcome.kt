package com.irmex.estudiante.domain.model

/** Resultado de una operación que puede fallar, con mensaje amigable para el usuario. */
sealed interface Outcome<out T> {
    data class Success<T>(val data: T) : Outcome<T>
    data class Failure(val message: String, val unauthorized: Boolean = false) : Outcome<Nothing>
}

/** Esta app es exclusiva para el rol de estudiante. */
object Roles {
    fun esEstudiante(rol: String?): Boolean = rol?.lowercase() == "estudiante"
}
