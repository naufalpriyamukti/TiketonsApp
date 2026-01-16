package com.tiketons.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tiketons.app.ui.theme.BluePrimary

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    backgroundColor: Color = BluePrimary
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp), // Tinggi standar tombol profesional
        shape = RoundedCornerShape(12.dp), // Tidak kotak tajam, tidak terlalu bulat
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = Color.Gray
        ),
        enabled = enabled && !isLoading
    ) {
        Text(
            text = if (isLoading) "Loading..." else text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}