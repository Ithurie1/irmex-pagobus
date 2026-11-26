package com.irmex.estudiante.data.remote

import com.irmex.estudiante.data.local.SessionDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** Agrega el JWT guardado en DataStore a cada petición (excepto el login). */
class AuthInterceptor @Inject constructor(
    private val sessionStore: SessionDataStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.url.encodedPath.endsWith("api/login")) return chain.proceed(original)

        // OkHttp ejecuta los interceptores en un hilo de fondo, por eso bloquear aquí es seguro.
        val token = runBlocking { sessionStore.token.first() }
        val request = if (token.isNullOrBlank()) {
            original
        } else {
            original.newBuilder().header("Authorization", "Bearer $token").build()
        }
        return chain.proceed(request)
    }
}
