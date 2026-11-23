package com.supershade.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.supershade.ui.theme.ShadeColorCustomization
import com.supershade.ui.theme.ShadeColorTarget

private val commonColors = listOf(
    Color.Black, Color.White, Color(0xFF2575FC), Color(0xFF10B981),
    Color(0xFF8B5CF6), Color(0xFFF59E0B), Color(0xFFF43F5E), Color(0xFF00BCD4),
)

@Composable
fun ShadeColorSettings(
    colors: ShadeColorCustomization,
    onColorChange: (ShadeColorTarget, Color?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingTarget by remember { mutableStateOf<ShadeColorTarget?>(null) }
    val targets = listOf(
        ShadeColorTarget.PANEL_BACKGROUND to "Fondo general del panel",
        ShadeColorTarget.NOTIFICATION_BACKGROUND to "Fondo de tarjetas de notificación",
        ShadeColorTarget.ACTIVE_CONTROL to "Controles y botones activos",
        ShadeColorTarget.GENERAL_TEXT to "Texto general",
        ShadeColorTarget.BRIGHTNESS_SLIDER to "Deslizador de brillo",
        ShadeColorTarget.NOTIFICATION_TEXT to "Texto de notificaciones",
        ShadeColorTarget.DISMISS_ACTION_BACKGROUND to "Fondo de acción al descartar",
    )
    val colorScheme = MaterialTheme.colorScheme
    fun defaultColor(target: ShadeColorTarget): Color = when (target) {
        ShadeColorTarget.PANEL_BACKGROUND -> colorScheme.background
        ShadeColorTarget.NOTIFICATION_BACKGROUND -> colorScheme.surfaceContainer
        ShadeColorTarget.ACTIVE_CONTROL -> colorScheme.primary
        ShadeColorTarget.GENERAL_TEXT, ShadeColorTarget.NOTIFICATION_TEXT -> colorScheme.onSurface
        ShadeColorTarget.BRIGHTNESS_SLIDER -> colorScheme.primary
        ShadeColorTarget.DISMISS_ACTION_BACKGROUND -> Color(0xFFE53935)
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        targets.forEach { (target, label) ->
            val color = colors.colorFor(target)
            val previewColor = color ?: defaultColor(target)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editingTarget = target }
                    .padding(vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        color?.toHex() ?: "Usar color del tema",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                androidx.compose.foundation.layout.Box(
                    Modifier
                        .size(30.dp)
                        .background(previewColor, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                )
            }
        }
    }

    editingTarget?.let { target ->
        var hex by remember(target, colors) {
            mutableStateOf((colors.colorFor(target) ?: defaultColor(target)).toHex())
        }
        val parsed = remember(hex) { runCatching { Color(android.graphics.Color.parseColor(hex.trim().let { if (it.startsWith("#")) it else "#$it" })) }.getOrNull() }
        val title = targets.first { it.first == target }.second
        AlertDialog(
            onDismissRequest = { editingTarget = null },
            title = { Text(title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Selecciona un color o escribe su código HEX.", style = MaterialTheme.typography.bodySmall)
                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        commonColors.chunked(4).forEach { rowColors ->
                            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                                rowColors.forEach { swatch ->
                                    androidx.compose.foundation.layout.Box(
                                        Modifier
                                            .size(30.dp)
                                            .background(swatch, CircleShape)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                            .clickable { hex = swatch.toHex() },
                                    )
                                }
                            }
                        }
                    }
                    OutlinedTextField(
                        value = hex,
                        onValueChange = { if (it.length <= 9) hex = it },
                        label = { Text("Color HEX") },
                        singleLine = true,
                        leadingIcon = {
                            androidx.compose.foundation.layout.Box(
                                Modifier
                                    .size(20.dp)
                                    .background(parsed ?: Color.Transparent, CircleShape)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                            )
                        },
                    )
                    TextButton(onClick = { onColorChange(target, null); editingTarget = null }) {
                        Text("Usar color del tema")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { parsed?.let { onColorChange(target, it) }; editingTarget = null }, enabled = parsed != null) {
                    Text("Aplicar")
                }
            },
            dismissButton = { TextButton(onClick = { editingTarget = null }) { Text("Cancelar") } },
        )
    }
}

private fun Color.toHex(): String = "#%08X".format(toArgbInt())

private fun Color.toArgbInt(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(),
    (red * 255).toInt(),
    (green * 255).toInt(),
    (blue * 255).toInt(),
)
