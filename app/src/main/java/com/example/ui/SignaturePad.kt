package com.example.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Base64
import androidx.compose.foundation.Canvas as ComposeCanvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.io.ByteArrayOutputStream

@Composable
fun DigitalSignatureDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    // We use a list frame to trigger recomposition
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Draw Digital Signature", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val newPath = Path()
                                    newPath.moveTo(offset.x, offset.y)
                                    currentPath = newPath
                                },
                                onDrag = { change, _ ->
                                    currentPath?.lineTo(change.position.x, change.position.y)
                                    // Trigger recomposition
                                    currentPath = Path().apply {
                                        addPath(currentPath ?: Path())
                                    }
                                },
                                onDragEnd = {
                                    currentPath?.let { paths.add(it) }
                                    currentPath = null
                                }
                            )
                        }
                ) {
                    ComposeCanvas(modifier = Modifier.fillMaxSize()) {
                        paths.forEach { path ->
                            drawPath(
                                path = path,
                                color = Color.Black,
                                style = Stroke(
                                    width = 8f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                        currentPath?.let { path ->
                            drawPath(
                                path = path,
                                color = Color.Black,
                                style = Stroke(
                                    width = 8f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { paths.clear(); currentPath = null }) {
                        Text("Clear")
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        val allPaths = mutableListOf<Path>().apply {
                            addAll(paths)
                            currentPath?.let { add(it) }
                        }
                        
                        // Render to Bitmap
                        val bitmap = Bitmap.createBitmap(800, 400, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(bitmap)
                        canvas.drawColor(android.graphics.Color.WHITE) // Background
                        
                        val paint = Paint().apply {
                            color = android.graphics.Color.BLACK
                            style = Paint.Style.STROKE
                            strokeWidth = 10f
                            strokeCap = Paint.Cap.ROUND
                            strokeJoin = Paint.Join.ROUND
                            isAntiAlias = true
                        }
                        
                        // We scale coordinates by 2x just to have a decent resolution
                        canvas.scale(2f, 2f)
                        
                        allPaths.forEach { path ->
                            canvas.drawPath(path.asAndroidPath(), paint)
                        }
                        
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                        val bArray = baos.toByteArray()
                        val base64 = Base64.encodeToString(bArray, Base64.DEFAULT)
                        onSave(base64)
                    }) {
                        Text("Save Signature")
                    }
                }
            }
        }
    }
}
