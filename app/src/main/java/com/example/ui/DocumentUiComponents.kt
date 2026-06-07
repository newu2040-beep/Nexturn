package com.example.ui

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.data.SavedDocument

@Composable
fun PromptBox(
    title: String,
    prompt: String,
    activeIcon: ImageVector = Icons.Default.Info,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = activeIcon,
                contentDescription = "Guidance Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun borderStroke(color: Color) = Modifier.border(1.dp, color, RoundedCornerShape(12.dp))

@Composable
fun HighlightField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    promptText: String,
    placeholder: String,
    highlightIfEmpty: Boolean,
    isMultiline: Boolean = false,
    focusedField: MutableState<String>,
    fieldName: String,
    leadingIcon: ImageVector? = null
) {
    val isMissing = highlightIfEmpty && value.trim().isEmpty()
    val isFocused = focusedField.value == fieldName
    val context = LocalContext.current

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (spokenText != null) {
                onValueChange(spokenText)
                focusedField.value = fieldName
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                focusedField.value = fieldName
            },
            label = { Text(if (isMissing) "⚠️ $label (Required)" else label) },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { focusedField.value = fieldName },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = if (isMissing) Color(0x33FF9800) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedBorderColor = if (isMissing) Color(0xFFFF9800) else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isMissing) Color(0xFFFF9800) else MaterialTheme.colorScheme.outline
            ),
            singleLine = !isMultiline,
            minLines = if (isMultiline) 3 else 1,
            leadingIcon = if (leadingIcon != null) {
                { Icon(leadingIcon, contentDescription = null, tint = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) }
            } else null,
            trailingIcon = {
                IconButton(onClick = {
                    focusedField.value = fieldName
                    try {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech input for: $label")
                        }
                        speechLauncher.launch(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Voice input not supported or enabled.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Speak to dictate",
                        tint = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        )
        if (isFocused) {
            Text(
                text = "💡 $promptText",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun TemplateSettingsRow(
    docType: String,
    currentTemplateName: String?,
    savedTemplates: List<SavedDocument>,
    onSaveRequested: (String) -> Unit,
    onLoadRequested: (SavedDocument) -> Unit,
    onDeleteRequested: (Long) -> Unit,
    onResetRequested: () -> Unit
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveName by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "📁 Template Manager",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Active: ${currentTemplateName ?: "Draft (Autosaved)"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { dropdownExpanded = true }) {
                        Icon(Icons.Default.FolderOpen, contentDescription = "Load Templates", tint = MaterialTheme.colorScheme.primary)
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("📄 Create New / Reset to Blank") },
                            onClick = {
                                onResetRequested()
                                dropdownExpanded = false
                            },
                            leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) }
                        )
                        if (savedTemplates.isNotEmpty()) {
                            HorizontalDivider()
                            Text(
                                text = " Saved (Offline)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                            savedTemplates.forEach { template ->
                                DropdownMenuItem(
                                    text = { Text(template.name) },
                                    onClick = {
                                        onLoadRequested(template)
                                        dropdownExpanded = false
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { onDeleteRequested(template.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                )
                            }
                        } else {
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("[No Saved Templates yet]") },
                                onClick = {},
                                enabled = false
                            )
                        }
                    }

                    Button(
                        onClick = { showSaveDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save As", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Document Template") },
            text = {
                Column {
                    Text("Give this template a name (e.g. 'Software Engineer Resume'):")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = saveName,
                        onValueChange = { saveName = it },
                        label = { Text("Template Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (saveName.isNotBlank()) {
                            onSaveRequested(saveName)
                            showSaveDialog = false
                            saveName = ""
                        }
                    }
                ) {
                    Text("Save Locally")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RenderPresetAvatar(presetType: String, sizeClass: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        drawPresetSilhouette(this, presetType)
    }
}

fun drawPresetSilhouette(drawScope: androidx.compose.ui.graphics.drawscope.DrawScope, presetType: String) {
    val size = drawScope.size
    val bgPaint = when (presetType) {
        "preset_blue_scholar" -> Color(0xFFE3F2FD)
        "preset_gray_executive" -> Color(0xFFECEFF1)
        "preset_orange_designer" -> Color(0xFFFFF3E0)
        "preset_green_leader" -> Color(0xFFE8F5E9)
        else -> Color(0xFFEEEEEE)
    }
    drawScope.drawRect(color = bgPaint)

    val suitColor = when (presetType) {
        "preset_blue_scholar" -> Color(0xFF1565C0)
        "preset_gray_executive" -> Color(0xFF37474F)
        "preset_orange_designer" -> Color(0xFFD84315)
        "preset_green_leader" -> Color(0xFF2E7D32)
        else -> Color(0xFF212121)
    }

    val headColor = Color(0xFFE0A96D)
    
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(0f, size.height)
        quadraticTo(size.width / 4f, size.height * 0.55f, size.width / 2f, size.height * 0.55f)
        quadraticTo(size.width * 0.75f, size.height * 0.55f, size.width, size.height)
        close()
    }
    drawScope.drawPath(path, color = suitColor)

    val vNeck = androidx.compose.ui.graphics.Path().apply {
        moveTo(size.width * 0.42f, size.height * 0.55f)
        lineTo(size.width * 0.5f, size.height * 0.65f)
        lineTo(size.width * 0.58f, size.height * 0.55f)
        close()
    }
    drawScope.drawPath(vNeck, color = Color.White)

    val tieColor = when (presetType) {
        "preset_blue_scholar" -> Color(0xFFD32F2F)
        "preset_gray_executive" -> Color(0xFF00796B)
        "preset_orange_designer" -> Color(0xFFFFEB3B)
        "preset_green_leader" -> Color(0xFFE64A19)
        else -> Color(0xFFD32F2F)
    }
    val tie = androidx.compose.ui.graphics.Path().apply {
        moveTo(size.width * 0.48f, size.height * 0.64f)
        lineTo(size.width * 0.52f, size.height * 0.64f)
        lineTo(size.width * 0.53f, size.height * 0.78f)
        lineTo(size.width * 0.5f, size.height * 0.83f)
        lineTo(size.width * 0.47f, size.height * 0.78f)
        close()
    }
    drawScope.drawPath(tie, color = tieColor)

    drawScope.drawRect(
        color = headColor,
        topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.44f, size.height * 0.38f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.12f, size.height * 0.18f)
    )

    drawScope.drawCircle(
        color = headColor,
        radius = size.width * 0.22f,
        center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height * 0.32f)
    )

    val hairPath = androidx.compose.ui.graphics.Path().apply {
        addArc(
            oval = androidx.compose.ui.geometry.Rect(
                size.width * 0.26f, size.height * 0.08f, size.width * 0.74f, size.height * 0.4f
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 180f
        )
    }
    drawScope.drawPath(hairPath, color = suitColor.copy(alpha = 0.85f))
}

@Composable
fun RenderAttachedPhotoOnPreview(photoUri: String, photoSize: String, modifier: Modifier = Modifier) {
    if (photoUri.isEmpty()) return
    
    val shape = when (photoSize) {
        "Circle" -> androidx.compose.foundation.shape.CircleShape
        else -> RoundedCornerShape(when (photoSize) {
            "Portrait" -> 6.dp
            "Square" -> 3.dp
            else -> 0.dp
        })
    }
    
    val widthDp = when (photoSize) {
        "Passport" -> 64.dp
        "Portrait" -> 60.dp
        "Circle" -> 54.dp
        "Square" -> 56.dp
        else -> 60.dp
    }
    
    val heightDp = when (photoSize) {
        "Passport" -> 64.dp
        "Portrait" -> 80.dp
        "Circle" -> 54.dp
        "Square" -> 56.dp
        else -> 80.dp
    }
    
    Card(
        modifier = modifier
            .width(widthDp)
            .height(heightDp),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(
            0.5.dp,
            if (photoSize == "Passport") Color.LightGray.copy(alpha = 0.8f) else Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            if (photoUri.startsWith("preset_")) {
                RenderPresetAvatar(
                    presetType = photoUri,
                    sizeClass = photoSize,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                coil.compose.AsyncImage(
                    model = photoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun PhotoAttachmentCard(viewModel: DocumentViewModel, modifier: Modifier = Modifier) {
    val photoUri by viewModel.attachedPhotoUri.collectAsState()
    val photoSize by viewModel.attachedPhotoSize.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(uri, flag)
                } catch (_: Exception) {}
                viewModel.setAttachedPhotoUri(uri.toString())
            }
        }
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp) else Color(0xFFF5F7FA)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Professional Photo & Portrait Alignment",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        text = if (photoUri.isNotEmpty()) "Attached: $photoSize (Visible on layout & PDF)" else "No Photo Attached (Tap to unlock professional format)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                            .background(Color.White)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUri.isNotEmpty()) {
                            if (photoUri.startsWith("preset_")) {
                                RenderPresetAvatar(
                                    presetType = photoUri,
                                    sizeClass = photoSize,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                coil.compose.AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Attached Photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text("No Photo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Select Option Source:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    val request = androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                    pickerLauncher.launch(request)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gallery", style = MaterialTheme.typography.bodySmall)
                            }
                            
                            if (photoUri.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { viewModel.setAttachedPhotoUri("") },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Or choose a professional quick-silhouette:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val presets = listOf(
                        "preset_blue_scholar" to "Scholar [Blue]",
                        "preset_gray_executive" to "Exec [Gray]",
                        "preset_orange_designer" to "Design [Orange]",
                        "preset_green_leader" to "Lead [Green]"
                    )
                    presets.forEach { (preset, labelStr) ->
                        val isSelected = photoUri == preset
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp)
                                .clickable { viewModel.setAttachedPhotoUri(preset) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(labelStr, style = MaterialTheme.typography.labelSmall, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, maxLines = 1)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Photo Frame Size & Alignment Mode:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val sizeOptions = listOf("Passport", "Portrait", "Circle", "Square")
                    sizeOptions.forEach { opt ->
                        val isSelected = photoSize == opt
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp)
                                .clickable { viewModel.setAttachedPhotoSize(opt) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(opt, style = MaterialTheme.typography.bodySmall, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}
