package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import com.example.data.*
import com.example.util.DocExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaperCanvas(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, shape = RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            content()
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
    onNavigateToField: (String) -> Unit
) {
    val dateStamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    PaperCanvas {
        // STYLE & LAYOUT: As requested, support Sidebar vs Single Column
        if (data.layout == "Sidebar Layout") {
            Row(modifier = Modifier.fillMaxWidth()) {
                // LEFT SIDEBAR (Contacts, Skills)
                Column(
                    modifier = Modifier
                        .weight(0.35f)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                        .padding(12.dp)
                ) {
                    Text("CONTACT INFO", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    if (data.email.isNotBlank()) Text(data.email, fontSize = 9.sp, color = Color.Black)
                    if (data.phone.isNotBlank()) Text(data.phone, fontSize = 9.sp, color = Color.Black)
                    if (data.location.isNotBlank()) Text(data.location, fontSize = 9.sp, color = Color.Black)
                    if (data.portfolioUrl.isNotBlank()) Text(data.portfolioUrl, fontSize = 9.sp, color = Color(0xFF0D47A1))
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("TECHNICAL SKILLS", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                    if (data.technicalSkills.isEmpty()) {
                        ClickablePlaceholder("[+ Click here to add skills]", "technicalSkills", onNavigateToField)
                    } else {
                        data.technicalSkills.forEach {
                            Text("• ${it.name} (${it.proficiency})", fontSize = 9.sp, color = Color.DarkGray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("LANGUAGES", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
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
                        Text(data.fullName, style = MaterialTheme.typography.headlineSmall, color = Color(0xFF1E1E1E), fontWeight = FontWeight.Bold)
                    }
                    if (data.jobTitle.isBlank()) {
                        ClickablePlaceholder("[Suggest job title]", "jobTitle", onNavigateToField)
                    } else {
                        Text(data.jobTitle, style = MaterialTheme.typography.titleMedium, color = Color(0xFF555555), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("PROFESSIONAL SUMMARY", fontSize = 11.sp, color = Color(0xFF333333), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    if (data.professionalSummary.isBlank()) {
                        ClickablePlaceholder("[Click to write professional summary]", "professionalSummary", onNavigateToField)
                    } else {
                        Text(data.professionalSummary, fontSize = 10.sp, color = Color.DarkGray, lineHeight = 14.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("WORK EXPERIENCE", fontSize = 11.sp, color = Color(0xFF333333), fontWeight = FontWeight.Bold)
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
                    Text("EDUCATION", fontSize = 11.sp, color = Color(0xFF333333), fontWeight = FontWeight.Bold)
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
                    Text(data.fullName, style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1E1E1E), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
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
                HorizontalDivider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text("PROFESSIONAL SUMMARY", fontSize = 11.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(3.dp))
            if (data.professionalSummary.isBlank()) {
                ClickablePlaceholder("[Click to write professional summary, suggest 3-4 sentences]", "professionalSummary", onNavigateToField)
            } else {
                Text(data.professionalSummary, fontSize = 10.sp, color = Color.DarkGray, lineHeight = 14.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("WORK EXPERIENCE", fontSize = 11.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
            HorizontalDivider(color = Color(0xFFE0E0E0))
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
            Text("EDUCATION", fontSize = 11.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
            HorizontalDivider(color = Color(0xFFE0E0E0))
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
    onNavigateToField: (String) -> Unit
) {
    val activeDate = data.date.ifBlank { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    PaperCanvas {
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
    onNavigateToField: (String) -> Unit
) {
    PaperCanvas {
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
    onNavigateToField: (String) -> Unit
) {
    val totals = DocExporter.calcInvoiceTotals(data)

    // Helper formatting function for inline totals
    fun fmt(v: Double) = String.format("%.2f", v)

    PaperCanvas {
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
    onNavigateToField: (String) -> Unit
) {
    PaperCanvas {
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
