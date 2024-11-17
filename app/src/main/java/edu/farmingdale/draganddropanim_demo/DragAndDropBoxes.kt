@file:OptIn(ExperimentalFoundationApi::class)

package edu.farmingdale.draganddropanim_demo

import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay
import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform

@Composable
fun DragAndDropBoxes(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Add state for animation direction
        var animationDirection by remember { mutableStateOf("none") }

        Row(
            modifier = modifier
                .fillMaxWidth().weight(0.2f)
        ) {
            val boxCount = 4
            var dragBoxIndex by remember {
                mutableIntStateOf(0)
            }

            repeat(boxCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(10.dp)
                        .border(1.dp, Color.Black)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event
                                    .mimeTypes()
                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        dragBoxIndex = index
                                        // Set animation based on which box it was dropped in
                                        animationDirection = when(index) {
                                            0 -> "up"
                                            1 -> "down"
                                            2 -> "left"
                                            3 -> "right"
                                            else -> "none"
                                        }
                                        return true
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Add text to show which box does what
                    Text(
                        text = when(index) {
                            0 -> "Up"
                            1 -> "Down"
                            2 -> "Left"
                            3 -> "Right"
                            else -> ""
                        },
                        color = Color.Gray,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    this@Row.AnimatedVisibility(
                        visible = index == dragBoxIndex,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Arrow",
                            tint = Color.Red,
                            modifier = Modifier
                                .fillMaxSize()
                                .dragAndDropSource {
                                    detectTapGestures(
                                        onLongPress = { offset ->
                                            startTransfer(
                                                transferData = DragAndDropTransferData(
                                                    clipData = ClipData.newPlainText(
                                                        "text",
                                                        ""
                                                    )
                                                )
                                            )
                                        }
                                    )
                                }
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(0.8f)
        ) {
            var canvasWidth by remember { mutableStateOf(0f) }
            var canvasHeight by remember { mutableStateOf(0f) }

            var rectOffsetX by remember { mutableStateOf(0f) }
            var rectOffsetY by remember { mutableStateOf(0f) }
            var rotation by remember { mutableStateOf(0f) }

            // Add animation offset states
            var animationOffsetY by remember { mutableStateOf(0f) }
            var animationOffsetX by remember { mutableStateOf(0f) }

            // Animation effect based on direction
            LaunchedEffect(animationDirection) {
                when(animationDirection) {
                    "up" -> {
                        repeat(100) {
                            delay(16)
                            animationOffsetY -= 2f
                        }
                        animationOffsetY = 0f
                    }
                    "down" -> {
                        repeat(100) {
                            delay(16)
                            animationOffsetY += 2f
                        }
                        animationOffsetY = 0f
                    }
                    "left" -> {
                        repeat(100) {
                            delay(16)
                            animationOffsetX -= 2f
                        }
                        animationOffsetX = 0f
                    }
                    "right" -> {
                        repeat(100) {
                            delay(16)
                            animationOffsetX += 2f
                        }
                        animationOffsetX = 0f
                    }
                }
                animationDirection = "none"
            }

            LaunchedEffect(Unit) {
                while(true) {
                    delay(16)
                    rotation += 2f
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            rectOffsetX += dragAmount.x
                            rectOffsetY += dragAmount.y
                        }
                    }
            ) {
                canvasWidth = size.width
                canvasHeight = size.height

                if (rectOffsetX == 0f && rectOffsetY == 0f) {
                    rectOffsetX = size.width / 2 - 50f
                    rectOffsetY = size.height / 2 - 50f
                }

                translate(rectOffsetX + animationOffsetX, rectOffsetY + animationOffsetY) {
                    rotate(degrees = rotation, pivot = Offset(50f, 50f)) {
                        drawRect(
                            color = Color.Blue,
                            topLeft = Offset(0f, 0f),
                            size = Size(100f, 100f)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Button(
                    onClick = {
                        rectOffsetX = canvasWidth / 2 - 50f
                        rectOffsetY = canvasHeight / 2 - 50f
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Reset Position")
                }
            }
        }
    }
}