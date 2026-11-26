package com.irmex.admin.domain.model

/** Resultado de una operación que puede fallar, con mensaje amigable para el usuario. */
sealed interface Outcome<out T> {
    data class Success<T>(val data: T) : Outcome<T>
    data class Failure(val message: String, val unauthorized: Boolean = false) : Outcome<Nothing>
}

/** Roles que pueden usar esta app. */
object Roles {
    val ADMINISTRATIVOS = setOf("admin", "superadmin")
    const val SUPERADMIN = "superadmin"
    fun esSuperadmin(rol: String?): Boolean = rol?.lowercase() == SUPERADMIN
    fun puedeEntrar(rol: String?): Boolean = rol != null && rol.lowercase() in ADMINISTRATIVOS
}
