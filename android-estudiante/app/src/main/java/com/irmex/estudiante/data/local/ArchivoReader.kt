package com.irmex.estudiante.data.local

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Archivo elegido por el usuario, ya leído en memoria. */
class ArchivoLeido(val nombre: String, val tipo: String, val bytes: ByteArray)

/** Lee los archivos que el usuario elige con el selector del sistema (PDF, JPG, PNG). */
@Singleton
class ArchivoReader @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun nombre(uri: String): String {
        val consulta = context.contentResolver.query(
            Uri.parse(uri), arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null,
        )
        val encontrado = consulta?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
        return encontrado ?: "archivo"
    }

    /** Devuelve null si no se puede abrir el archivo. */
    fun leer(uri: String): ArchivoLeido? {
        val resolver = context.contentResolver
        val destino = Uri.parse(uri)
        val tipo = resolver.getType(destino) ?: return null
        val bytes = resolver.openInputStream(destino)?.use { it.readBytes() } ?: return null
        return ArchivoLeido(conExtension(nombre(uri), tipo), tipo, bytes)
    }

    /** El servidor deduce el formato por la extensión, así que el nombre siempre debe llevarla. */
    private fun conExtension(nombre: String, tipo: String): String {
        if (nombre.substringAfterLast('.', "").isNotEmpty()) return nombre
        val extension = when (tipo) {
            "application/pdf" -> "pdf"
            "image/png" -> "png"
            else -> "jpg"
        }
        return "$nombre.$extension"
    }

    companion object {
        val TIPOS_PERMITIDOS = setOf("application/pdf", "image/jpeg", "image/png")
        const val TAMANO_MAXIMO_BYTES = 10 * 1024 * 1024
    }
}
