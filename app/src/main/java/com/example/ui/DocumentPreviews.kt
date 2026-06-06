package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.rotate
import androidx.compose.runtime.staticCompositionLocalOf
import coil.compose.AsyncImage
import com.example.data.*
import com.example.util.DocExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DocumentStyleConfig(
    val useWatermark: Boolean = false,
    val watermarkText: String = "",
    val watermarkImageUri: String = "",
    val useSignature: Boolean = false,
    val signatureText: String = "",
    val signatureStyle: String = "Cursive",
    val signatureBitmapBase64: String = "",
    val spacingMultiplier: Float = 1.0f,
    // Universal design styling fields
    val customFontFamily: String = "Default",
    val isFontBold: Boolean = false,
    val isFontItalic: Boolean = false,
    val customFontSizeOffset: Int = 0,
    val customPaperColorHex: String = "",
    val documentCornerRadius: Int = 4,
    val hasBorderOutline: Boolean = true,
    val borderThicknessDp: Float = 1.0f,
    val watermarkSymbol: String = "",
    val isProStudioEnabled: Boolean = true
)

val LocalDocumentStyleConfig = staticCompositionLocalOf { DocumentStyleConfig() }

@Composable
fun PaperCanvas(
    modifier: Modifier = Modifier,
    paperColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    val styleConfig = LocalDocumentStyleConfig.current
    val spacingMultiplier = styleConfig.spacingMultiplier

    // Parse custom paper color if supplied
    val finalPaperColor = if (styleConfig.isProStudioEnabled && styleConfig.customPaperColorHex.isNotBlank()) {
        try {
            Color(android.graphics.Color.parseColor(styleConfig.customPaperColorHex))
        } catch (e: Exception) {
            paperColor
        }
    } else {
        paperColor
    }

    // Border stroke selector
    val cardBorder = if (styleConfig.isProStudioEnabled && styleConfig.hasBorderOutline) {
        BorderStroke(styleConfig.borderThicknessDp.dp, Color(0xFFE0E0E0))
    } else {
        BorderStroke(1.dp, Color(0xFFE0E0E0))
    }

    // Global Font family mapping for the canvas document sheet
    val baseFontFamily = if (styleConfig.isProStudioEnabled) {
        when (styleConfig.customFontFamily) {
            "Serif" -> androidx.compose.ui.text.font.FontFamily.Serif
            "Monospace" -> androidx.compose.ui.text.font.FontFamily.Monospace
            "Cursive" -> androidx.compose.ui.text.font.FontFamily.Cursive
            else -> androidx.compose.ui.text.font.FontFamily.Default
        }
    } else {
        androidx.compose.ui.text.font.FontFamily.Default
    }

    val currentTypography = MaterialTheme.typography
    val offsetVal = if (styleConfig.isProStudioEnabled) styleConfig.customFontSizeOffset else 0
    fun addFontSizeOffset(unit: androidx.compose.ui.unit.TextUnit, offset: Int): androidx.compose.ui.unit.TextUnit {
        return if (unit.isSp) {
            (unit.value + offset).sp
        } else {
            unit
        }
    }
    val forcedWeight = if (styleConfig.isProStudioEnabled && styleConfig.isFontBold) androidx.compose.ui.text.font.FontWeight.Bold else null
    val forcedStyle = if (styleConfig.isProStudioEnabled && styleConfig.isFontItalic) androidx.compose.ui.text.font.FontStyle.Italic else null
    val cornerRadius = if (styleConfig.isProStudioEnabled) styleConfig.documentCornerRadius else 4

    // Overriding typography system to support live customizations
    val customTypography = androidx.compose.material3.Typography(
        displayLarge = currentTypography.displayLarge.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.displayLarge.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.displayLarge.fontWeight, fontStyle = forcedStyle ?: currentTypography.displayLarge.fontStyle),
        displayMedium = currentTypography.displayMedium.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.displayMedium.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.displayMedium.fontWeight, fontStyle = forcedStyle ?: currentTypography.displayMedium.fontStyle),
        displaySmall = currentTypography.displaySmall.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.displaySmall.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.displaySmall.fontWeight, fontStyle = forcedStyle ?: currentTypography.displaySmall.fontStyle),
        headlineLarge = currentTypography.headlineLarge.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.headlineLarge.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.headlineLarge.fontWeight, fontStyle = forcedStyle ?: currentTypography.headlineLarge.fontStyle),
        headlineMedium = currentTypography.headlineMedium.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.headlineMedium.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.headlineMedium.fontWeight, fontStyle = forcedStyle ?: currentTypography.headlineMedium.fontStyle),
        headlineSmall = currentTypography.headlineSmall.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.headlineSmall.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.headlineSmall.fontWeight, fontStyle = forcedStyle ?: currentTypography.headlineSmall.fontStyle),
        titleLarge = currentTypography.titleLarge.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.titleLarge.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.titleLarge.fontWeight, fontStyle = forcedStyle ?: currentTypography.titleLarge.fontStyle),
        titleMedium = currentTypography.titleMedium.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.titleMedium.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.titleMedium.fontWeight, fontStyle = forcedStyle ?: currentTypography.titleMedium.fontStyle),
        titleSmall = currentTypography.titleSmall.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.titleSmall.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.titleSmall.fontWeight, fontStyle = forcedStyle ?: currentTypography.titleSmall.fontStyle),
        bodyLarge = currentTypography.bodyLarge.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.bodyLarge.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.bodyLarge.fontWeight, fontStyle = forcedStyle ?: currentTypography.bodyLarge.fontStyle),
        bodyMedium = currentTypography.bodyMedium.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.bodyMedium.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.bodyMedium.fontWeight, fontStyle = forcedStyle ?: currentTypography.bodyMedium.fontStyle),
        bodySmall = currentTypography.bodySmall.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.bodySmall.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.bodySmall.fontWeight, fontStyle = forcedStyle ?: currentTypography.bodySmall.fontStyle),
        labelLarge = currentTypography.labelLarge.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.labelLarge.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.labelLarge.fontWeight, fontStyle = forcedStyle ?: currentTypography.labelLarge.fontStyle),
        labelMedium = currentTypography.labelMedium.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.labelMedium.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.labelMedium.fontWeight, fontStyle = forcedStyle ?: currentTypography.labelMedium.fontStyle),
        labelSmall = currentTypography.labelSmall.copy(fontFamily = baseFontFamily, fontSize = addFontSizeOffset(currentTypography.labelSmall.fontSize, offsetVal), fontWeight = forcedWeight ?: currentTypography.labelSmall.fontWeight, fontStyle = forcedStyle ?: currentTypography.labelSmall.fontStyle)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding((8 * spacingMultiplier).dp)
            .shadow(4.dp, shape = RoundedCornerShape(cornerRadius.dp)),
        colors = CardDefaults.cardColors(containerColor = finalPaperColor),
        shape = RoundedCornerShape(cornerRadius.dp),
        border = cardBorder
    ) {
        MaterialTheme(typography = customTypography) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Content layer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding((24 * spacingMultiplier).dp)
                ) {
                    content()
                    
                    // If digital signature is enabled, append signature under professional line
                    if (styleConfig.useSignature) {
                        Spacer(modifier = Modifier.height((24 * spacingMultiplier).dp))
                        Column(
                            modifier = Modifier.align(Alignment.End),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Digitally Signed",
                                fontSize = (9 * spacingMultiplier).sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            if (styleConfig.signatureBitmapBase64.isNotBlank()) {
                                val imageBytes = android.util.Base64.decode(styleConfig.signatureBitmapBase64, android.util.Base64.DEFAULT)
                                val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                if (bitmap != null) {
                                    androidx.compose.foundation.Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Signature",
                                        modifier = Modifier.height((40 * spacingMultiplier).dp)
                                    )
                                }
                            } else if (styleConfig.signatureText.isNotBlank()) {
                                Text(
                                    text = styleConfig.signatureText,
                                    fontSize = (15 * spacingMultiplier).sp,
                                    fontStyle = if (styleConfig.signatureStyle == "Cursive") FontStyle.Italic else FontStyle.Normal,
                                    fontWeight = if (styleConfig.signatureStyle == "Official Bold") FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = if (styleConfig.signatureStyle == "Cursive") FontFamily.Cursive else FontFamily.Default,
                                    color = Color(0xFF0D47A1), // professional signature color
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width((120 * spacingMultiplier).dp)
                                    .height(1.dp)
                                    .background(Color.LightGray)
                            )
                        }
                    }
                }

                // Watermark overlay layer
                if (styleConfig.useWatermark) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .align(Alignment.Center)
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (styleConfig.watermarkSymbol.isNotBlank()) {
                            Text(
                                text = styleConfig.watermarkSymbol,
                                fontSize = (65 * spacingMultiplier).sp,
                                color = Color(0x1B9E9E9E), // subtle custom watermark symbol stamp
                                textAlign = TextAlign.Center,
                                modifier = Modifier.rotate(-20f)
                            )
                        } else if (styleConfig.watermarkImageUri.isNotBlank()) {
                            AsyncImage(
                                model = styleConfig.watermarkImageUri,
                                contentDescription = "Watermark",
                                modifier = Modifier
                                    .fillMaxSize(0.6f)
                                    .rotate(-35f),
                                alpha = 0.15f,
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                            )
                        } else if (styleConfig.watermarkText.isNotBlank()) {
                            Text(
                                text = styleConfig.watermarkText,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0x139E9E9E), // subtle opacity watermark
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp,
                                maxLines = 1,
                                modifier = Modifier.rotate(-35f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClickablePlaceholder(
    text: String,
    fieldName: String,
    onNavigateToField: (String) -> Unit
) {
    Text(
        text = text,
        color = Color(0xFFD84315),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .background(Color(0xFFFFE0B2), RoundedCornerShape(2.dp))
            .border(0.5.dp, Color(0xFFFFB74D), RoundedCornerShape(2.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clickable { onNavigateToField(fieldName) }
    )
}

@Composable
fun CvDocumentPreview(
    data: CvData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    val dateStamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val themeColor = when (data.stylePreference) {
        "Minimal" -> Color(0xFF1E293B) // Slate Charcoal
        "Professional" -> Color(0xFF1E3A8A) // deep blue
        "Creative" -> Color(0xFFFF6B6B) // creative warm coral
        else -> Color(0xFF0F766E) // Modern pine teal
    }

    val sidebarBg = when (data.stylePreference) {
        "Minimal" -> Color(0xFFFAFAFA)
        "Professional" -> Color(0xFFF1F5F9)
        "Creative" -> Color(0xFFFFF0F0)
        else -> Color(0xFFF5F5F5)
    }

    PaperCanvas(paperColor = paperColor) {
        // STYLE & LAYOUT: As requested, support Sidebar vs Single Column
        if (data.layout == "Sidebar Layout") {
            Row(modifier = Modifier.fillMaxWidth()) {
                // LEFT SIDEBAR (Contacts, Skills)
                Column(
                    modifier = Modifier
                        .weight(0.35f)
                        .background(sidebarBg, RoundedCornerShape(4.dp))
                        .padding(12.dp)
                ) {
                    Text("CONTACT INFO", style = MaterialTheme.typography.labelSmall, color = themeColor, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    if (data.email.isNotBlank()) Text(data.email, fontSize = 9.sp, color = Color.Black)
                    if (data.phone.isNotBlank()) Text(data.phone, fontSize = 9.sp, color = Color.Black)
                    if (data.location.isNotBlank()) Text(data.location, fontSize = 9.sp, color = Color.Black)
                    if (data.portfolioUrl.isNotBlank()) Text(data.portfolioUrl, fontSize = 9.sp, color = Color(0xFF0D47A1))
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("TECHNICAL SKILLS", style = MaterialTheme.typography.labelSmall, color = themeColor, fontWeight = FontWeight.Bold)
                    if (data.technicalSkills.isEmpty()) {
                        ClickablePlaceholder("[+ Click here to add skills]", "technicalSkills", onNavigateToField)
                    } else {
                        data.technicalSkills.forEach {
                            Text("• ${it.name} (${it.proficiency})", fontSize = 9.sp, color = Color.DarkGray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("LANGUAGES", style = MaterialTheme.typography.labelSmall, color = themeColor, fontWeight = FontWeight.Bold)
                    if (data.languages.isEmpty()) {
                        ClickablePlaceholder("[+ Add languages]", "languages", onNavigateToField)
                    } else {
                        data.languages.forEach {
                            Text("• ${it.language}: ${it.proficiency}", fontSize = 9.sp, color = Color.DarkGray)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT COLUMN: Main Experience
                Column(modifier = Modifier.weight(0.65f)) {
                    // Header
                    if (data.fullName.isBlank()) {
                        ClickablePlaceholder("[Click to add your full name]", "fullName", onNavigateToField)
                    } else {
                        Text(data.fullName, style = MaterialTheme.typography.headlineSmall, color = themeColor, fontWeight = FontWeight.Bold)
                    }
                    if (data.jobTitle.isBlank()) {
                        ClickablePlaceholder("[Suggest job title]", "jobTitle", onNavigateToField)
                    } else {
                        Text(data.jobTitle, style = MaterialTheme.typography.titleMedium, color = Color(0xFF555555), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("PROFESSIONAL SUMMARY", fontSize = 11.sp, color = themeColor, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    if (data.professionalSummary.isBlank()) {
                        ClickablePlaceholder("[Click to write professional summary]", "professionalSummary", onNavigateToField)
                    } else {
                        Text(data.professionalSummary, fontSize = 10.sp, color = Color.DarkGray, lineHeight = 14.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("WORK EXPERIENCE", fontSize = 11.sp, color = themeColor, fontWeight = FontWeight.Bold)
                    if (data.workExperiences.isEmpty()) {
                        ClickablePlaceholder("[Click to add work experiences]", "workExperiences", onNavigateToField)
                    } else {
                        data.workExperiences.forEach { job ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${job.jobTitle} at ${job.companyName}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(job.dates + if (job.location.isNotBlank()) " | " + job.location else "", fontSize = 8.5.sp, color = Color.Gray)
                            job.bullets.forEach { b ->
                                Text("• $b", fontSize = 9.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 6.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("EDUCATION", fontSize = 11.sp, color = themeColor, fontWeight = FontWeight.Bold)
                    if (data.educations.isEmpty()) {
                        ClickablePlaceholder("[Add your academics]", "educations", onNavigateToField)
                    } else {
                        data.educations.forEach { edu ->
                            Text("${edu.degree} - ${edu.institution} (${edu.year})", fontSize = 9.5.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                            if (edu.gpa.isNotBlank()) Text("GPA: " + edu.gpa, fontSize = 8.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        } else {
            // SINGLE COLUMN LAYOUT
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                if (data.fullName.isBlank()) {
                    ClickablePlaceholder("[Click to add your full name]", "fullName", onNavigateToField)
                } else {
                    Text(data.fullName, style = MaterialTheme.typography.headlineMedium, color = themeColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
                if (data.jobTitle.isBlank()) {
                    ClickablePlaceholder("[Suggest job title]", "jobTitle", onNavigateToField)
                } else {
                    Text(data.jobTitle, fontSize = 13.sp, color = Color(0xFF555555), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.height(6.dp))

                // Contacts centered block
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val elements = listOfNotNull(
                        data.email.takeIf { it.isNotBlank() },
                        data.phone.takeIf { it.isNotBlank() },
                        data.location.takeIf { it.isNotBlank() },
                        data.portfolioUrl.takeIf { it.isNotBlank() }
                    )
                    Text(
                        text = if (elements.isEmpty()) "[No contact details added yet]" else elements.joinToString("  |  "),
                        fontSize = 9.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = themeColor.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text("PROFESSIONAL SUMMARY", fontSize = 11.sp, color = themeColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(3.dp))
            if (data.professionalSummary.isBlank()) {
                ClickablePlaceholder("[Click to write professional summary, suggest 3-4 sentences]", "professionalSummary", onNavigateToField)
            } else {
                Text(data.professionalSummary, fontSize = 10.sp, color = Color.DarkGray, lineHeight = 14.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("WORK EXPERIENCE", fontSize = 11.sp, color = themeColor, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = themeColor.copy(alpha = 0.15f))
            if (data.workExperiences.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                ClickablePlaceholder("[Click to add work experiences, add job title, dates and accomplishments]", "workExperiences", onNavigateToField)
            } else {
                data.workExperiences.forEach { job ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${job.jobTitle} at ${job.companyName}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(job.dates, fontSize = 9.sp, color = Color.Gray)
                    }
                    if (job.location.isNotBlank()) Text("Location: " + job.location, fontSize = 8.5.sp, color = Color.Gray)
                    job.bullets.forEach { b ->
                        Text("• $b", fontSize = 9.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("EDUCATION", fontSize = 11.sp, color = themeColor, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = themeColor.copy(alpha = 0.15f))
            if (data.educations.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                ClickablePlaceholder("[Click to add your academic degrees and institution details]", "educations", onNavigateToField)
            } else {
                data.educations.forEach { edu ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${edu.degree} - ${edu.institution}", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        Text(edu.year, fontSize = 9.sp, color = Color.Gray)
                    }
                    if (edu.gpa.isNotBlank()) Text("GPA: " + edu.gpa, fontSize = 8.5.sp, color = Color.DarkGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Updated: $dateStamp (auto generated)",
            style = MaterialTheme.typography.labelSmall,
            color = Color.LightGray,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun CoverLetterDocumentPreview(
    data: CoverLetterData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    val activeDate = data.date.ifBlank { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    PaperCanvas(paperColor = paperColor) {
        Text("COVER LETTER (Business standard format with zero sample data)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        Text(activeDate, fontSize = 10.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        // Recipient block
        if (data.hiringManager.isBlank()) {
            ClickablePlaceholder("[Click to add hiring manager name]", "hiringManager", onNavigateToField)
        } else {
            Text(data.hiringManager, fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Medium)
        }
        if (data.companyName.isBlank()) {
            ClickablePlaceholder("[Click to add company name]", "companyName", onNavigateToField)
        } else {
            Text(data.companyName, fontSize = 10.sp, color = Color.Black)
        }
        if (data.companyAddress.isNotBlank()) {
            Text(data.companyAddress, fontSize = 10.sp, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Subject Block
        Row {
            Text("Subject: Application for ", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            if (data.jobTitle.isBlank()) {
                ClickablePlaceholder("[Click for Job Title]", "jobTitle", onNavigateToField)
            } else {
                Text(data.jobTitle, fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Text(" – ", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            if (data.yourName.isBlank()) {
                ClickablePlaceholder("[Your Name]", "yourName", onNavigateToField)
            } else {
                Text(data.yourName, fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Dear " + if (data.hiringManager.isBlank()) "Hiring Manager" else data.hiringManager + ",", fontSize = 10.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(14.dp))

        // Paragraphs inline guides (completely blank canvases, prompts show as bracket labels)
        if (data.paragraph1.isBlank()) {
            ClickablePlaceholder("[Click here to add opening pitch. Suggestion: mention target job title, where you discovered the job posting and why you apply]", "paragraph1", onNavigateToField)
        } else {
            Text(data.paragraph1, fontSize = 10.sp, color = Color.DarkGray, lineHeight = 15.sp)
        }
        
        Spacer(modifier = Modifier.height(14.dp))
        if (data.paragraph2.isBlank()) {
            ClickablePlaceholder("[Click here to add paragraph 2. Suggestion: reference key project milestones, metrics, and core technology stack]", "paragraph2", onNavigateToField)
        } else {
            Text(data.paragraph2, fontSize = 10.sp, color = Color.DarkGray, lineHeight = 15.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))
        if (data.paragraph3.isBlank()) {
            ClickablePlaceholder("[Click here to add closing statement. Suggestion: express enthusiasm, request callback/interview and mention attachment package]", "paragraph3", onNavigateToField)
        } else {
            Text(data.paragraph3, fontSize = 10.sp, color = Color.DarkGray, lineHeight = 15.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Sincerely,", fontSize = 10.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(20.dp))

        if (data.yourName.isBlank()) {
            ClickablePlaceholder("[Click for Your Name Signature]", "yourName", onNavigateToField)
        } else {
            Text(data.yourName, fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        }
        if (data.yourContact.isBlank()) {
            ClickablePlaceholder("[Click to add contact address/email]", "yourContact", onNavigateToField)
        } else {
            Text(data.yourContact, fontSize = 8.5.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun EmailDocumentPreview(
    data: EmailData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    PaperCanvas(paperColor = paperColor) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                .border(0.5.dp, Color.LightGray, RoundedCornerShape(4.dp))
                .padding(10.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("To: ", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
                    if (data.recipientEmail.isBlank()) {
                        ClickablePlaceholder("[Click to set Recipient Email]", "recipientEmail", onNavigateToField)
                    } else {
                        Text(data.recipientEmail, fontSize = 10.sp, color = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Subject: ", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
                    if (data.subjectLine.isBlank()) {
                        ClickablePlaceholder("[Click to add Subject Line]", "subjectLine", onNavigateToField)
                    } else {
                        Text(data.subjectLine, fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email Body Render
        if (data.bodyMarkdown.isBlank()) {
            ClickablePlaceholder("[Click to design Email Body. Suggested guidelines: include polite greetings, reference attached files and calendar availability]", "bodyMarkdown", onNavigateToField)
        } else {
            Text(
                text = data.bodyMarkdown,
                fontSize = 10.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                lineHeight = 15.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Signature
        if (data.signatureName.isBlank()) {
            ClickablePlaceholder("[Click to set signature name]", "sigName", onNavigateToField)
        } else {
            Text(data.signatureName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        if (data.signatureTitle.isNotBlank()) {
            Text(data.signatureTitle, fontSize = 9.sp, color = Color.DarkGray)
        }
        if (data.signaturePhone.isNotBlank()) {
            Text("Ph: " + data.signaturePhone, fontSize = 9.sp, color = Color.Gray)
        }
        if (data.signatureWebsite.isNotBlank() || data.signatureLinkedIn.isNotBlank()) {
            Text(
                "${data.signatureWebsite}  |  ${data.signatureLinkedIn}",
                fontSize = 8.sp,
                color = Color(0xFF0288D1)
            )
        }
    }
}

@Composable
fun InvoiceDocumentPreview(
    data: InvoiceData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    val totals = DocExporter.calcInvoiceTotals(data)

    // Helper formatting function for inline totals
    fun fmt(v: Double) = String.format("%.2f", v)

    PaperCanvas(paperColor = paperColor) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("VANCE INVOICE SHEET", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Row {
                Text("No: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                if (data.invoiceNumber.isBlank()) {
                    ClickablePlaceholder("[INV-0000]", "invoiceNum", onNavigateToField)
                } else {
                    Text(data.invoiceNumber, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("FROM (Vendor):", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                if (data.myBusinessName.isBlank()) {
                    ClickablePlaceholder("[Click for Business Name]", "myBusinessName", onNavigateToField)
                } else {
                    Text(data.myBusinessName, fontSize = 9.5.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                }
                if (data.myAddress.isNotBlank()) Text(data.myAddress, fontSize = 9.sp, color = Color.DarkGray)
                if (data.myTaxId.isNotBlank()) Text("Tax ID: " + data.myTaxId, fontSize = 8.5.sp, color = Color.Gray)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("BILL TO (Client):", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                if (data.clientName.isBlank()) {
                    ClickablePlaceholder("[Click for Client Name]", "clientName", onNavigateToField)
                } else {
                    Text(data.clientName, fontSize = 9.5.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                }
                if (data.clientCompany.isNotBlank()) Text(data.clientCompany, fontSize = 9.sp, color = Color.DarkGray)
                if (data.clientAddress.isNotBlank()) Text(data.clientAddress, fontSize = 9.sp, color = Color.DarkGray)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Issue Date: " + data.invoiceDate.ifBlank { "[No Picked Date]" }, fontSize = 8.5.sp, modifier = Modifier.weight(1f), color = Color.DarkGray)
            Text("Due Date: " + data.dueDate.ifBlank { "[No Picked Due Date]" }, fontSize = 8.5.sp, modifier = Modifier.weight(1f), color = Color.DarkGray)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Table headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEEEEE))
                .padding(4.dp)
        ) {
            Text("Service Item Description", fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Qty", fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
            Text("Unit ($)", fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp), textAlign = TextAlign.End)
            Text("Sub ($)", fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp), textAlign = TextAlign.End)
        }

        // Table Rows
        if (data.lineItems.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            ClickablePlaceholder("[No line items added yet. Click Add Line Item to build fee calculations.]", "lineItems", onNavigateToField)
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            data.lineItems.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 4.dp)) {
                    Text(item.description, fontSize = 8.5.sp, color = Color.Black, modifier = Modifier.weight(1f))
                    Text(item.quantity.toString(), fontSize = 8.5.sp, color = Color.Black, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
                    Text(fmt(item.unitPrice.toDouble()), fontSize = 8.5.sp, color = Color.Black, modifier = Modifier.width(60.dp), textAlign = TextAlign.End)
                    Text(fmt((item.quantity * item.unitPrice).toDouble()), fontSize = 8.5.sp, color = Color.Black, modifier = Modifier.width(70.dp), textAlign = TextAlign.End)
                }
            }
        }
        HorizontalDivider(color = Color.LightGray)

        // Totals Ledger block
        Column(modifier = Modifier.align(Alignment.End).width(200.dp).padding(top = 10.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Subtotal:", fontSize = 8.5.sp, color = Color.DarkGray)
                val sub = (totals["subtotal"] as? Float)?.toDouble() ?: (totals["subtotal"] as? Double) ?: 0.0
                Text("$${fmt(sub)}", fontSize = 8.5.sp, color = Color.Black)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Discount (-${data.discount}%):", fontSize = 8.5.sp, color = Color.DarkGray)
                val disc = (totals["discountAmount"] as? Float)?.toDouble() ?: (totals["discountAmount"] as? Double) ?: 0.0
                Text("-$${fmt(disc)}", fontSize = 8.5.sp, color = Color.Black)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Tax (+${data.taxRate}%):", fontSize = 8.5.sp, color = Color.DarkGray)
                val tax = (totals["taxAmount"] as? Float)?.toDouble() ?: (totals["taxAmount"] as? Double) ?: 0.0
                Text("+$${fmt(tax)}", fontSize = 8.5.sp, color = Color.Black)
            }
            HorizontalDivider(color = Color.Black, modifier = Modifier.padding(vertical = 4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("TOTAL DUE:", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                val tot = (totals["total"] as? Float)?.toDouble() ?: (totals["total"] as? Double) ?: 0.0
                Text("$${fmt(tot)}", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        if (data.paymentInstructions.isNotBlank()) {
            Text("Payment Instructions: " + data.paymentInstructions, fontSize = 8.sp, color = Color.DarkGray)
        }
        if (data.notesTerms.isNotBlank()) {
            Text("Notes & terms: " + data.notesTerms, fontSize = 8.sp, color = Color.Gray, fontStyle = FontStyle.Italic)
        }
    }
}

@Composable
fun ProposalDocumentPreview(
    data: ProposalData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    PaperCanvas(paperColor = paperColor) {
        Text("BUSINESS OVERVIEW PROPOSAL", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        if (data.title.isBlank()) {
            ClickablePlaceholder("[Click to write Campaign Proposal Title]", "title", onNavigateToField)
        } else {
            Text(data.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
        }
        Spacer(modifier = Modifier.height(14.dp))

        Text("1. Executive Summary", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
        if (data.executiveSummary.isBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            ClickablePlaceholder("[Click to write executive summary. Suggestion: pitch the core user pain point and how the strategy aligns]", "executiveSummary", onNavigateToField)
        } else {
            Text(data.executiveSummary, fontSize = 9.5.sp, color = Color.DarkGray, lineHeight = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("2. Project Scope & Approach Phases", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
        if (data.scope.isEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            ClickablePlaceholder("[Click to add scope items, outline Phase 1, Phase 2, etc.]", "scope", onNavigateToField)
        } else {
            data.scope.forEachIndexed { i, s ->
                Text("Phase ${i + 1}: $s", fontSize = 9.sp, color = Color.Black, modifier = Modifier.padding(start = 6.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("3. Key Deliverables Checklist", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
        if (data.deliverables.isEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            ClickablePlaceholder("[Click to add project milestones, e.g. final assets, layouts]", "deliverables", onNavigateToField)
        } else {
            data.deliverables.forEach {
                val sign = if (it.isChecked) "[X]" else "[  ]"
                Text("$sign ${it.title}", fontSize = 9.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 6.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("4. Timeline Schedules", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
        if (data.timeline.isEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            ClickablePlaceholder("[Click to add projected weeks and timeline steps]", "timeline", onNavigateToField)
        } else {
            data.timeline.forEach {
                Text("• ${it.phase}: ${it.description}", fontSize = 9.sp, color = Color.Black, modifier = Modifier.padding(start = 6.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("5. Financial Investment", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
        if (data.investmentDetails.isBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            ClickablePlaceholder("[Click to map costs, flat retainer prices, and milestone links]", "investmentDetails", onNavigateToField)
        } else {
            Text(data.investmentDetails, fontSize = 9.5.sp, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("6. Why Choose Me / Teams Background", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
        if (data.whyMe.isBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            ClickablePlaceholder("[Click to add background statements about past expertise]", "whyMe", onNavigateToField)
        } else {
            Text(data.whyMe, fontSize = 9.5.sp, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("7. Acceptance Next Steps", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
        if (data.nextSteps.isEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            ClickablePlaceholder("[Click to map acceptance steps, signing requirements]", "nextSteps", onNavigateToField)
        } else {
            data.nextSteps.forEachIndexed { i, s ->
                Text("${i + 1}. $s", fontSize = 9.sp, color = Color.Black, modifier = Modifier.padding(start = 6.dp))
            }
        }
    }
}

@Composable
fun OfferLetterDocumentPreview(
    data: OfferLetterData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    PaperCanvas(paperColor = paperColor) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Enterprise Letterhead
            if (data.companyName.isBlank()) {
                ClickablePlaceholder("[Click to enter Company Name]", "companyName", onNavigateToField)
            } else {
                Text(data.companyName.uppercase(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
            }
            if (data.companyAddress.isNotBlank()) {
                Text(data.companyAddress, fontSize = 8.5.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color(0xFF1565C0), thickness = 1.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Date
            if (data.startDate.isBlank()) {
                ClickablePlaceholder("[Set date / start date]", "startDate", onNavigateToField)
            } else {
                Text("Date: ${data.startDate}", fontSize = 9.5.sp, color = Color.DarkGray, modifier = Modifier.align(Alignment.End))
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Candidate Recipient Block
            Text("TO:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            if (data.candidateName.isBlank()) {
                ClickablePlaceholder("[Click to specify candidate full name]", "candidateName", onNavigateToField)
            } else {
                Text(data.candidateName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("SUBJECT: OFFER OF EMPLOYMENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))

            // Greeting
            Text("Dear ${if(data.candidateName.isNotBlank()) data.candidateName else "Candidate"},", fontSize = 9.5.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            // Main Body
            val companyPhrase = if (data.companyName.isNotBlank()) "at ${data.companyName}" else "within our organization"
            val titlePhrase = if (data.jobTitle.isNotBlank()) "as ${data.jobTitle}" else "in the proposed role"
            Text(
                text = "We are absolutely thrilled to extend you this formal offer of employment $companyPhrase $titlePhrase. We believe your exceptional skills and accomplishments will make you a vital addition to our engineering team.",
                fontSize = 9.5.sp,
                lineHeight = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Terms details block
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBFD)),
                border = BorderStroke(0.5.dp, Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("EMPLOYMENT AGREEMENT TERMS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Position Title:", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        if (data.jobTitle.isBlank()) {
                            ClickablePlaceholder("[Suggest job title]", "jobTitle", onNavigateToField)
                        } else {
                            Text(data.jobTitle, fontSize = 8.5.sp, color = Color.DarkGray)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Base Compensation:", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        if (data.salary.isBlank()) {
                            ClickablePlaceholder("[Enter salary / compensation]", "salary", onNavigateToField)
                        } else {
                            Text(data.salary, fontSize = 8.5.sp, color = Color.DarkGray)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Commencement Date:", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(if (data.startDate.isNotBlank()) data.startDate else "[Specify start date]", fontSize = 8.5.sp, color = Color.DarkGray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Offer details text
            Text("DETAILS & ADDITIONAL BENEFITS:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            if (data.offerDetails.isBlank()) {
                ClickablePlaceholder("[Click to specify extra details like stock, medical schemes]", "offerDetails", onNavigateToField)
            } else {
                Text(data.offerDetails, fontSize = 9.5.sp, lineHeight = 13.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Sign-off
            Text("Sincerely,", fontSize = 9.5.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp)) // Signature Gap
            if (data.signatoryName.isBlank()) {
                ClickablePlaceholder("[Click to write authorized signatory name]", "signatoryName", onNavigateToField)
            } else {
                Text(data.signatoryName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            if (data.signatoryTitle.isNotBlank()) {
                Text(data.signatoryTitle, fontSize = 8.5.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ResignationLetterDocumentPreview(
    data: ResignationLetterData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    PaperCanvas(paperColor = paperColor) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Contact Block
            if (data.employeeName.isBlank()) {
                ClickablePlaceholder("[Click to add employee name]", "employeeName", onNavigateToField)
            } else {
                Text(data.employeeName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            if (data.companyName.isNotBlank()) {
                Text(data.companyName, fontSize = 9.sp, color = Color.DarkGray)
            }
            
            Text("Date: " + SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date()), fontSize = 9.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(14.dp))

            // To section
            Text("TO:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            if (data.managerName.isBlank()) {
                ClickablePlaceholder("[Click to add manager/supervisor name]", "managerName", onNavigateToField)
            } else {
                Text(data.managerName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            if (data.companyName.isNotBlank()) {
                Text(data.companyName, fontSize = 9.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Salutation
            Text("Dear ${if(data.managerName.isNotBlank()) data.managerName else "Supervisor"},", fontSize = 9.5.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))

            // Resignation Core Statement
            val targetLastDay = if (data.lastWorkingDay.isNotBlank()) data.lastWorkingDay else "[Select Last Working Day]"
            val targetCompany = if (data.companyName.isNotBlank()) "from my position at ${data.companyName}" else "from my active position"
            Text(
                text = "Please accept this formal notification that I am resigning $targetCompany. My final working day will be $targetLastDay.",
                fontSize = 9.5.sp,
                lineHeight = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Reason for Departure
            Text("REASON FOR DEPARTURE:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            if (data.resignationReason.isBlank()) {
                ClickablePlaceholder("[Click to specify primary reason or departure goals]", "resignationReason", onNavigateToField)
            } else {
                Text(data.resignationReason, fontSize = 9.5.sp, lineHeight = 13.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Gratitude note
            Text("PERSONAL STATEMENT:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            if (data.personalNote.isBlank()) {
                ClickablePlaceholder("[Click to write gratitude statement or thank coworkers]", "personalNote", onNavigateToField)
            } else {
                Text(data.personalNote, fontSize = 9.5.sp, lineHeight = 13.sp, color = Color.DarkGray, fontStyle = FontStyle.Italic)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Sign-off signature footer
            Text("Sincerely yours,", fontSize = 9.5.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))
            if (data.signatureName.isBlank()) {
                ClickablePlaceholder("[Specify printed signature name]", "signatureName", onNavigateToField)
            } else {
                Text(data.signatureName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Declaring Employee", fontSize = 8.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ServiceContractDocumentPreview(
    data: ServiceContractData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    PaperCanvas(paperColor = paperColor) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Document Title
            Text("PROFESSIONAL SERVICES AGREEMENT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = Color(0xFF1B5E20), thickness = 1.5.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Recitals introductory
            val contractDate = if (data.agreementDate.isNotBlank()) data.agreementDate else "[State contract date]"
            val contractor = if (data.contractorName.isNotBlank()) data.contractorName else "[Provider Name]"
            val client = if (data.clientName.isNotBlank()) data.clientName else "[Client Name]"
            
            Text(
                text = "This Technical Services Agreement (the \"Agreement\") is executed as of $contractDate, by and between $contractor (\"Contractor\") and $client (\"Client\"). Both parties hereby covenant and agree as follows:",
                fontSize = 9.5.sp,
                lineHeight = 14.sp,
                color = Color.Black,
                fontStyle = FontStyle.Normal
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Section 1: Scope of Work
            Text("1. SCOPE OF SERVICES & EXPECTED ACTIONS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            if (data.scopeOfWork.isBlank()) {
                ClickablePlaceholder("[Click to write scope of work services detail block]", "scopeOfWork", onNavigateToField)
            } else {
                Text(data.scopeOfWork, fontSize = 9.5.sp, lineHeight = 13.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Section 2: Compensation
            Text("2. FINANCIAL COMPENSATION & TERM SCHEDULING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            if (data.compensation.isBlank()) {
                ClickablePlaceholder("[State payment rate compensation details]", "compensation", onNavigateToField)
            } else {
                Text("Compensation Rate: " + data.compensation, fontSize = 9.5.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
            }
            if (data.paymentTerms.isBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                ClickablePlaceholder("[Specify payment terms, e.g. Net 15 days clearing]", "paymentTerms", onNavigateToField)
            } else {
                Spacer(modifier = Modifier.height(2.dp))
                Text("Payment Terms: " + data.paymentTerms, fontSize = 9.5.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Section 3: Legal governing law
            Text("3. LEGAL COMPLIANCE & JURISDICTION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            Text("This contract Agreement is structured and governed according to the legislations and enforcement mechanisms of the State of ${if (data.governingLaw.isNotBlank()) data.governingLaw else "Delaware"}.", fontSize = 9.5.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(24.dp))

            // Signatory side by side row block
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Contractor Box
                Column(modifier = Modifier.weight(1f)) {
                    Text("CONTRACTOR REPRESENTATIVE:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.Black, thickness = 0.5.dp, modifier = Modifier.width(100.dp).padding(vertical = 2.dp))
                    Text(contractor, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Client Box
                Column(modifier = Modifier.weight(1f)) {
                    Text("CLIENT REPRESENTATIVE:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.Black, thickness = 0.5.dp, modifier = Modifier.width(100.dp).padding(vertical = 2.dp))
                    Text(client, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun CertificateDocumentPreview(
    data: CertificateData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, shape = RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = paperColor),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(3.dp, Color(0xFFC5A059))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Emblem decoration",
                tint = Color(0xFFC5A059),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CERTIFICATE OF ACHIEVEMENT",
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F1E36),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "This credential document is proudly awarded to",
                fontSize = 8.5.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))

            if (data.recipientName.isBlank()) {
                ClickablePlaceholder("[Click to enter Recipient Name]", "recipientName", onNavigateToField)
            } else {
                Text(
                    text = data.recipientName.uppercase(),
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC5A059),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "In recognition of outstanding competence and milestone qualification of",
                fontSize = 8.5.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (data.achievementTitle.isBlank()) {
                ClickablePlaceholder("[Describe the qualification/achievement title]", "achievementTitle", onNavigateToField)
            } else {
                Text(
                    text = data.achievementTitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2A38),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            if (data.certificateDescription.isBlank()) {
                ClickablePlaceholder("[Enter complete verification achievements details block]", "certificateDescription", onNavigateToField)
            } else {
                Text(
                    text = data.certificateDescription,
                    fontSize = 9.sp,
                    lineHeight = 13.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (data.dateOfIssue.isNotBlank()) data.dateOfIssue else "[Issue Date]", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.width(80.dp).padding(vertical = 2.dp))
                    Text("OFFICIAL VALIDITY DATE", fontSize = 7.sp, color = Color.Gray)
                }
                
                if (data.awardingOrg.isNotBlank()) {
                    Text(data.awardingOrg.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (data.authoritySignatory.isNotBlank()) data.authoritySignatory else "[Signatory Official]", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)
                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.width(80.dp).padding(vertical = 2.dp))
                    Text("AUTHORIZED SIGNATURE", fontSize = 7.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun MeetingMinutesDocumentPreview(
    data: MeetingMinutesData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    PaperCanvas(paperColor = paperColor) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    Text("MINUTES OF PROCEEDINGS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))
                    if (data.meetingTitle.isBlank()) {
                        ClickablePlaceholder("[Click to select title]", "meetingTitle", onNavigateToField)
                    } else {
                        Text(data.meetingTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
                Text("SESSION DOCUMENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = Color(0xFFD84315), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Date: ${if(data.meetingDate.isNotBlank()) data.meetingDate else "[Select date]"}", fontSize = 9.sp, color = Color.DarkGray)
                Text("Chair/Facilitator: ${if(data.facilitator.isNotBlank()) data.facilitator else "[Specify facilitator]"}", fontSize = 9.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text("ATTENDEES:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            if (data.attendees.isBlank()) {
                ClickablePlaceholder("[Specify list of participating attendees]", "attendees", onNavigateToField)
            } else {
                Text(data.attendees, fontSize = 9.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text("CONCURRENT DISCUSSION SYNCH:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))
            if (data.discussionSummary.isBlank()) {
                ClickablePlaceholder("[Write summary about core discussion items]", "discussionSummary", onNavigateToField)
            } else {
                Text(data.discussionSummary, fontSize = 9.5.sp, lineHeight = 13.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text("RESOLVED ACTION CHECKLISTS:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))
            if (data.actionItems.isEmpty()) {
                ClickablePlaceholder("[Add specific action items checklist]", "actionItems", onNavigateToField)
            } else {
                data.actionItems.forEach { s ->
                    Text("[  ]  $s", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color.Black, modifier = Modifier.padding(vertical = 2.dp).padding(start = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (data.nextMeetingDate.isNotBlank()) {
                Text("NEXT PROCEEDING SESSION METRICS:", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("Date scheduled: " + data.nextMeetingDate, fontSize = 9.sp, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun BusinessLetterDocumentPreview(
    data: BusinessLetterData,
    paperColor: Color = Color.White,
    onNavigateToField: (String) -> Unit
) {
    PaperCanvas(paperColor = paperColor) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (data.senderAddress.isBlank()) {
                ClickablePlaceholder("[Click to specify Sender Contact Address]", "senderAddress", onNavigateToField)
            } else {
                Text(data.senderAddress, fontSize = 9.5.sp, lineHeight = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (data.date.isBlank()) {
                ClickablePlaceholder("[Official letter Date]", "date", onNavigateToField)
            } else {
                Text(data.date, fontSize = 9.5.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (data.recipientAddress.isBlank()) {
                ClickablePlaceholder("[Click to write receiving recipient mailing block]", "recipientAddress", onNavigateToField)
            } else {
                Text(data.recipientAddress, fontSize = 9.5.sp, lineHeight = 13.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(14.dp))

            if (data.subject.isBlank()) {
                ClickablePlaceholder("[Enter letter subject line]", "subject", onNavigateToField)
            } else {
                Text("SUBJECT: " + data.subject.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(14.dp))

            Text(if (data.salutation.isNotBlank()) data.salutation else "To Whom It May Concern:", fontSize = 10.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            if (data.paragraph1.isBlank()) {
                ClickablePlaceholder("[Write introduction paragraph stating main purpose]", "paragraph1", onNavigateToField)
            } else {
                Text(data.paragraph1, fontSize = 9.5.sp, lineHeight = 14.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (data.paragraph2.isNotBlank()) {
                Text(data.paragraph2, fontSize = 9.5.sp, lineHeight = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (data.paragraph3.isNotBlank()) {
                Text(data.paragraph3, fontSize = 9.5.sp, lineHeight = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(if (data.valediction.isNotBlank()) data.valediction else "Sincerely,", fontSize = 9.5.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(20.dp))
            
            if (data.senderName.isBlank()) {
                ClickablePlaceholder("[Sender Printed Name]", "senderName", onNavigateToField)
            } else {
                Text(data.senderName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

