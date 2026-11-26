package com.irmex.admin.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.ui.graphics.vector.ImageVector

/** Módulos del panel. Cada uno es una tarjeta del menú y un destino de navegación. */
enum class Modulo(
    val ruta: String,
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val soloSuperadmin: Boolean = false,
) {
    TARJETAS("tarjetas", "Gestión de Tarjetas", "Consulta tarjetas, saldos y vigencias.", Icons.Filled.CreditCard),
    ESTATUS_USUARIOS("usuarios", "Estatus de Usuario", "Busca estudiantes y da de baja cuentas.", Icons.Filled.ManageSearch),
    PRIVILEGIOS("privilegios", "Gestor de Privilegios", "Administra los roles de acceso.", Icons.Filled.AdminPanelSettings, soloSuperadmin = true),
    REPORTES("reportes", "Reportes de Uso", "Métricas financieras y de tarjetas.", Icons.Filled.BarChart),
    DOCUMENTOS("documentos", "Verificar Documentos", "Aprueba o rechaza renovaciones.", Icons.Filled.ConfirmationNumber),
    SOPORTE("soporte", "Centro de Soporte", "Atiende tickets de los alumnos.", Icons.Filled.SupportAgent),
}
