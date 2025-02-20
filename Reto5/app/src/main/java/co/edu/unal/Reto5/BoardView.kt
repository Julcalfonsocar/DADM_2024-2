package co.edu.unal.Reto5

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.IntSize

@Composable
fun BoardView(
    board: List<Player>,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val xImage = ImageBitmap.imageResource(id = R.drawable.x_img)
    val oImage = ImageBitmap.imageResource(id = R.drawable.o_img)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val cellSize = size.width / 3
                    val row = (offset.y / cellSize).toInt()
                    val col = (offset.x / cellSize).toInt()
                    val index = row * 3 + col
                    if (index in 0..8) {
                        onCellClick(index)
                    }
                }
            }
    ) {
        // Dibujar líneas del tablero
        drawBoard()
        
        // Dibujar X y O
        board.forEachIndexed { index, player ->
            if (player != Player.NONE) {
                val row = index / 3
                val col = index % 3
                val cellSize = size.width / 3
                val padding = cellSize * 0.2f
                
                translate(
                    left = col * cellSize + padding,
                    top = row * cellSize + padding
                ) {
                    val imageSize = (cellSize - (padding * 2)).toInt()
                    when (player) {
                        Player.HUMAN -> drawImage(
                            image = xImage,
                            dstSize = IntSize(imageSize, imageSize)
                        )
                        Player.COMPUTER -> drawImage(
                            image = oImage,
                            dstSize = IntSize(imageSize, imageSize)
                        )
                        else -> {}
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawBoard() {
    // Líneas verticales
    for (i in 1..2) {
        val x = size.width * (i / 3f)
        drawLine(
            Color.White,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 5f
        )
    }
    
    // Líneas horizontales
    for (i in 1..2) {
        val y = size.height * (i / 3f)
        drawLine(
            Color.White,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 5f
        )
    }
} 