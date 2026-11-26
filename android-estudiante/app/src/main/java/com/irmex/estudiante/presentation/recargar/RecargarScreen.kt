package com.irmex.estudiante.presentation.recargar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.estudiante.presentation.common.MensajesEffect
import com.irmex.estudiante.presentation.common.formatoMoneda
import com.irmex.estudiante.presentation.common.formatoVencimiento

private val MONTOS_RAPIDOS = listOf(50, 100, 200, 300)

@Composable
fun RecargarScreen(snackbar: SnackbarHostState, viewModel: RecargarViewModel = hiltViewModel()) {
    val tarjeta by viewModel.tarjeta.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    // El texto de los campos vive en la pantalla y se entrega al ViewModel al pagar.
    var monto by rememberSaveable { mutableStateOf("") }
    var numero by rememberSaveable { mutableStateOf("") }
    var vencimiento by rememberSaveable { mutableStateOf("") }
    var cvc by rememberSaveable { mutableStateOf("") }
    var recibo by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.recargaExitosa.collect {
            monto = ""; numero = ""; vencimiento = ""; cvc = ""
            recibo = true
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text("Recargar saldo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                tarjeta?.let { "Tarjeta IrMex terminación ${it.ultimosDigitos}" } ?: "Aún no tienes una tarjeta asignada",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text("¿Cuánto quieres recargar?", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MONTOS_RAPIDOS.forEach { valor ->
                AssistChip(onClick = { monto = valor.toString() }, label = { Text("$$valor") })
            }
        }
        OutlinedTextField(
            value = monto, onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Monto a recargar ($)") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Tarjeta de crédito o débito", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = numero, onValueChange = { numero = it.filter(Char::isDigit).take(16) },
                    label = { Text("Número (16 dígitos)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = vencimiento, onValueChange = { vencimiento = formatoVencimiento(it) },
                        label = { Text("MM/AA") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = cvc, onValueChange = { cvc = it.filter(Char::isDigit).take(3) },
                        label = { Text("CVC") }, singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(
                    "Pago simulado: los datos de la tarjeta bancaria no se guardan ni se envían a ningún servidor.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Button(
            onClick = { viewModel.pagar(monto, numero, vencimiento, cvc) },
            enabled = !cargando && tarjeta != null,
            modifier = Modifier.fillMaxWidth().height(54.dp),
        ) {
            if (cargando) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp) else Text("Procesar pago")
        }
    }

    if (recibo) {
        AlertDialog(
            onDismissRequest = { recibo = false },
            title = { Text("¡Recarga exitosa!") },
            text = { Text("Tu nuevo saldo es ${formatoMoneda(tarjeta?.saldo ?: 0.0)}.") },
            confirmButton = { TextButton(onClick = { recibo = false }) { Text("Listo") } },
        )
    }
}
