package com.irmex.estudiante.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.irmex.estudiante.domain.model.Session
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "irmex_session")

/** Sesión del administrador (token + datos básicos) guardada con Jetpack DataStore. */
@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenCipher: TokenCipher,
) {
    private val tokenKey = stringPreferencesKey("token")
    private val nombreKey = stringPreferencesKey("nombre")
    private val rolKey = stringPreferencesKey("rol")
    private val correoKey = stringPreferencesKey("correo")

    /** El token se guarda cifrado; aquí se entrega ya descifrado (o null si no hay sesión válida). */
    val token: Flow<String?> = context.dataStore.data.map { prefs -> prefs[tokenKey]?.let(tokenCipher::descifrar) }

    val session: Flow<Session?> = context.dataStore.data.map { prefs ->
        val token = prefs[tokenKey]?.let(tokenCipher::descifrar)
        if (token.isNullOrBlank()) null
        else Session(token, prefs[nombreKey].orEmpty(), prefs[rolKey].orEmpty(), prefs[correoKey].orEmpty())
    }

    suspend fun guardar(session: Session) {
        context.dataStore.edit {
            it[tokenKey] = tokenCipher.cifrar(session.token)
            it[nombreKey] = session.nombre
            it[rolKey] = session.rol
            it[correoKey] = session.correo
        }
    }

    suspend fun limpiar() {
        context.dataStore.edit { it.clear() }
    }
}
