package com.irmex.admin.presentation.renovaciones

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/** Convierte los bytes de una página en imagen, reduciéndola si es muy grande para no agotar la memoria. */
fun decodificarPagina(bytes: ByteArray, anchoMaximo: Int = 2048): ImageBitmap? {
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, limites)
    if (limites.outWidth <= 0 || limites.outHeight <= 0) return null

    var muestreo = 1
    while (limites.outWidth / muestreo > anchoMaximo) muestreo *= 2

    val opciones = BitmapFactory.Options().apply { inSampleSize = muestreo }
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opciones)?.asImageBitmap()
}
