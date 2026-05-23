package com.zaidan.quraneasy.feature.tasbih.presentation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TargetPickerDialog(
    currentTarget: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var customValue by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Dhikr Target") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val targets = listOf(33,99,100,1000)
                targets.forEach { values->
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onConfirm(values)
                        }) {
                        Text(
                            text = "$values Times",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Serif)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customValue,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.all { it.isDigit() }) {
                            customValue = input
                        }
                    },
                    label = { Text("Enter Custom Target") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            // This button handles the custom value saving
            TextButton(
                onClick = {
                    val intValue = customValue.toIntOrNull()
                    if (intValue != null && intValue > 0) {
                        onConfirm(intValue)
                    }
                },
                enabled = customValue.isNotEmpty() // Only clickable if text exists
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            // This button handles cancellation
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
