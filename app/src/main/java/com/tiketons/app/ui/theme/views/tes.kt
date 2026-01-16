package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/* =======================
   SHAPE TICKET
   ======================= */
fun TicketShape(cutRadius: Float = 24f) = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    val r = cutRadius

    moveTo(0f, 0f)
    lineTo(width, 0f)
    lineTo(width, height / 2 - r)
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(
            width - r,
            height / 2 - r,
            width + r,
            height / 2 + r
        ),
        startAngleDegrees = 270f,
        sweepAngleDegrees = -180f,
        forceMoveTo = false
    )
    lineTo(width, height)
    lineTo(0f, height)
    lineTo(0f, height / 2 + r)
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(
            -r,
            height / 2 - r,
            r,
            height / 2 + r
        ),
        startAngleDegrees = 90f,
        sweepAngleDegrees = -180f,
        forceMoveTo = false
    )
    close()
}

/* =======================
   COMPOSABLE
   ======================= */
@Composable
fun TicketCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    ticketColor: Color
) {
    Surface(
        modifier = modifier
            .clip(TicketShape()),
        color = ticketColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.ConfirmationNumber,
                contentDescription = "Ticket Icon",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

/* =======================
   PREVIEW
   ======================= */

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TicketPreview_Blue() {
    TicketCard(
        title = "Tiket Konser",
        subtitle = "Akses VIP",
        ticketColor = Color(0xFF4F8EF7),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TicketPreview_Green() {
    TicketCard(
        title = "Tiket Seminar",
        subtitle = "Online Zoom",
        ticketColor = Color(0xFF2ECC71),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TicketPreview_Orange() {
    TicketCard(
        title = "Tiket Event",
        subtitle = "Festival Teknologi",
        ticketColor = Color(0xFFF39C12),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}
