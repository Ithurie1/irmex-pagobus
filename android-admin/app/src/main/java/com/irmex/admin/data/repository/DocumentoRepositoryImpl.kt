package com.irmex.admin.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.irmex.admin.data.remote.IrmexApi
import com.irmex.admin.data.remote.safeCall
import com.irmex.admin.domain.model.Documento
import com.irmex.admin.domain.model.Outcome
import com.irmex.admin.domain.repository.DocumentoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/** Descarga el documento del estudiante; los PDF se convierten a imágenes (una por página). */
@Singleton
class DocumentoRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    @ApplicationContext private val context: Context,
) : DocumentoRepository {

    override suspend fun obtener(nombreArchivo: String): Outcome<Documento> = withContext(Dispatchers.IO) {
        safeCall {
            val bytes = api.documento(nombreArchivo).use { it.bytes() }
            val esPdf = nombreArchivo.substringAfterLast('.', "").lowercase() == "pdf"
            Documento(nombreArchivo, if (esPdf) paginasDePdf(bytes) else listOf(bytes))
        }
    }

    private fun paginasDePdf(bytes: ByteArray): List<ByteArray> {
        // PdfRenderer necesita un archivo; se usa uno temporal en caché y se borra al terminar.
        val temporal = File.createTempFile("documento", ".pdf", context.cacheDir)
        temporal.writeBytes(bytes)
        val descriptor = ParcelFileDescriptor.open(temporal, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(descriptor)
        try {
            val total = minOf(renderer.pageCount, MAX_PAGINAS)
            return List(total) { indice -> renderizarPagina(renderer, indice) }
        } finally {
            renderer.close()
            descriptor.close()
            temporal.delete()
        }
    }

    private fun renderizarPagina(renderer: PdfRenderer, indice: Int): ByteArray {
        val pagina = renderer.openPage(indice)
        try {
            val alto = ANCHO_PAGINA * pagina.height / pagina.width
            val bitmap = Bitmap.createBitmap(ANCHO_PAGINA, alto, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.WHITE)
            pagina.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            val salida = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
            bitmap.recycle()
            return salida.toByteArray()
        } finally {
            pagina.close()
        }
    }

    private companion object {
        const val ANCHO_PAGINA = 1080
        const val MAX_PAGINAS = 5
    }
}
