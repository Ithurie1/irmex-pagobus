package com.irmex.admin.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    observeSession: ObserveSessionUseCase,
    private val logout: LogoutUseCase,
) : ViewModel() {

    /** null = aún leyendo DataStore; true = hay sesión; false = sin sesión. */
    val haySesion: StateFlow<Boolean?> = observeSession()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Nombre y rol del administrador en sesión (para el saludo y para mostrar módulos según el rol). */
    val usuario: StateFlow<Pair<String, String>> = observeSession()
        .map { (it?.nombre ?: "") to (it?.rol ?: "") }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "" to "")

    fun cerrarSesion() {
        viewModelScope.launch { logout() }
    }
}
