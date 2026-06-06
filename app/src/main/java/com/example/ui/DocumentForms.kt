package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.*

@Composable
fun CvForm(
    data: CvData,
    onDataChanged: (CvData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    var newSkillName by remember { mutableStateOf("") }
    var newSkillDesc by remember { mutableStateOf("") }
    
    // Work Experience States
    var newJobTitle by remember { mutableStateOf("") }
    var newJobCompany by remember { mutableStateOf("") }
    var newJobDates by remember { mutableStateOf("") }
    var newJobLocation by remember { mutableStateOf("") }
    var newJobBullet by remember { mutableStateOf("") }

    // Education States
    var newEduDegree by remember { mutableStateOf("") }
    var newEduInst by remember { mutableStateOf("") }
    var newEduYear by remember { mutableStateOf("") }
    var newEduGpa by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // HEADER INFO
        item {
            Text("👤 Personal Header Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HighlightField(
                value = data.fullName,
                onValueChange = { onDataChanged(data.copy(fullName = it)) },
                label = "Full Name",
                promptText = "Enter your complete name (rendered in central bold header).",
                placeholder = "e.g. Elena Rostova",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "fullName",
                leadingIcon = Icons.Default.Person
            )
            HighlightField(
                value = data.jobTitle,
                onValueChange = { onDataChanged(data.copy(jobTitle = it)) },
                label = "Target Job Title",
                promptText = "The target role you are applying for (shown below your name).",
                placeholder = "e.g. Senior Frontend Engineer",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "jobTitle",
                leadingIcon = Icons.Default.Badge
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.email,
                        onValueChange = { onDataChanged(data.copy(email = it)) },
                        label = "Email Address",
                        promptText = "A professional email address to coordinate interviews.",
                        placeholder = "e.g. elena.dev@nexturn.com",
                        highlightIfEmpty = false,
                        focusedField = focusedField,
                        fieldName = "email",
                        leadingIcon = Icons.Default.Email
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.phone,
                        onValueChange = { onDataChanged(data.copy(phone = it)) },
                        label = "Phone Number",
                        promptText = "A valid telephone contact with relevant country codes.",
                        placeholder = "e.g. +1 (332) 902-1240",
                        highlightIfEmpty = false,
                        focusedField = focusedField,
                        fieldName = "phone",
                        leadingIcon = Icons.Default.Phone
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.location,
                        onValueChange = { onDataChanged(data.copy(location = it)) },
                        label = "Primary Location",
                        promptText = "Your metropolitan location or work permission area.",
                        placeholder = "e.g. New York, USA",
                        highlightIfEmpty = false,
                        focusedField = focusedField,
                        fieldName = "location",
                        leadingIcon = Icons.Default.LocationOn
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.portfolioUrl,
                        onValueChange = { onDataChanged(data.copy(portfolioUrl = it)) },
                        label = "Portfolio/LinkedIn",
                        promptText = "Relevant hyperlinks showcasing open source projects.",
                        placeholder = "e.g. github.com/elena-dev",
                        highlightIfEmpty = false,
                        focusedField = focusedField,
                        fieldName = "portfolioUrl",
                        leadingIcon = Icons.Default.Link
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        // SUMMARY
        item {
            Text("📄 Professional Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HighlightField(
                value = data.professionalSummary,
                onValueChange = { onDataChanged(data.copy(professionalSummary = it)) },
                label = "Biography (Suggest 3-4 sentences)",
                promptText = "Give a brief high-level overview of your seniority, technology focus, and unique product value.",
                placeholder = "Write summary here...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "professionalSummary"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        // CORE COMPETENCIES LIST EDITOR
        item {
            Text("✨ Core Competencies (6-8 items)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    OutlinedTextField(
                        value = newSkillName,
                        onValueChange = { newSkillName = it },
                        label = { Text("Competency Title") },
                        placeholder = { Text("e.g. System Design") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newSkillDesc,
                        onValueChange = { newSkillDesc = it },
                        label = { Text("Short Description") },
                        placeholder = { Text("e.g. Enterprise monolithic transitions") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (newSkillName.isNotBlank() && newSkillDesc.isNotBlank()) {
                                onDataChanged(data.copy(
                                    coreCompetencies = data.coreCompetencies + CvData.CompetencyItem(newSkillName, newSkillDesc)
                                ))
                                newSkillName = ""
                                newSkillDesc = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Add Competency")
                    }
                }
            }
        }

        itemsIndexed(data.coreCompetencies) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(item.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = {
                    val updated = data.coreCompetencies.toMutableList().apply { removeAt(index) }
                    onDataChanged(data.copy(coreCompetencies = updated))
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        // WORK EXPERIENCE
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("💼 Work Experience Entries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    OutlinedTextField(
                        value = newJobTitle, onValueChange = { newJobTitle = it },
                        label = { Text("Job Title") }, placeholder = { Text("e.g. Tech Lead") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newJobCompany, onValueChange = { newJobCompany = it },
                        label = { Text("Company Name") }, placeholder = { Text("e.g. Acme Corp") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newJobDates, onValueChange = { newJobDates = it },
                        label = { Text("Dates of Tenancy") }, placeholder = { Text("e.g. Jan 2024 - Present") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newJobLocation, onValueChange = { newJobLocation = it },
                        label = { Text("Location (Optional)") }, placeholder = { Text("e.g. Remote") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newJobBullet, onValueChange = { newJobBullet = it },
                        label = { Text("Achievements / Bullet Points") }, placeholder = { Text("Add one bullet point details") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (newJobTitle.isNotBlank() && newJobCompany.isNotBlank()) {
                                val bulletList = if (newJobBullet.isNotBlank()) listOf(newJobBullet) else emptyList()
                                onDataChanged(data.copy(
                                    workExperiences = data.workExperiences + CvData.WorkItem(
                                        newJobTitle, newJobCompany, newJobDates, newJobLocation, bulletList
                                    )
                                ))
                                newJobTitle = ""
                                newJobCompany = ""
                                newJobDates = ""
                                newJobLocation = ""
                                newJobBullet = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Add Job Entry")
                    }
                }
            }
        }

        itemsIndexed(data.workExperiences) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${item.jobTitle} at ${item.companyName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(item.dates, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    item.bullets.forEach { b ->
                        Text("• $b", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                IconButton(onClick = {
                    val updated = data.workExperiences.toMutableList().apply { removeAt(index) }
                    onDataChanged(data.copy(workExperiences = updated))
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        // EDUCATION
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("🎓 Education Entries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    OutlinedTextField(
                        value = newEduDegree, onValueChange = { newEduDegree = it },
                        label = { Text("Degree") }, placeholder = { Text("e.g. Master in CS") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newEduInst, onValueChange = { newEduInst = it },
                        label = { Text("Institution") }, placeholder = { Text("e.g. Oxford University") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newEduYear, onValueChange = { newEduYear = it },
                        label = { Text("Year of Graduation") }, placeholder = { Text("e.g. 2022") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newEduGpa, onValueChange = { newEduGpa = it },
                        label = { Text("GPA / Honours (Optional)") }, placeholder = { Text("e.g. 3.9/4.0") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (newEduDegree.isNotBlank() && newEduInst.isNotBlank()) {
                                onDataChanged(data.copy(
                                    educations = data.educations + CvData.EducationItem(
                                        newEduDegree, newEduInst, newEduYear, newEduGpa
                                    )
                                ))
                                newEduDegree = ""
                                newEduInst = ""
                                newEduYear = ""
                                newEduGpa = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Add Academic Credential")
                    }
                }
            }
        }

        itemsIndexed(data.educations) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${item.degree} - ${item.institution}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("Class of ${item.year}" + if (item.gpa.isNotBlank()) " | GPA: ${item.gpa}" else "", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = {
                    val updated = data.educations.toMutableList().apply { removeAt(index) }
                    onDataChanged(data.copy(educations = updated))
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        // FOOTER TEXT STAMP
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Footer Date stamp auto-inserted based on device clock for maximum consistency.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun CoverLetterForm(
    data: CoverLetterData,
    onDataChanged: (CoverLetterData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("📬 Recipient Address block", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            HighlightField(
                value = data.hiringManager,
                onValueChange = { onDataChanged(data.copy(hiringManager = it)) },
                label = "Hiring Manager",
                promptText = "The specific contact name or selection panel head if known, else 'Hiring Manager'.",
                placeholder = "e.g. Dr. Arthur Pendelton",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "hiringManager"
            )
            HighlightField(
                value = data.companyName,
                onValueChange = { onDataChanged(data.copy(companyName = it)) },
                label = "Company Name",
                promptText = "Full brand or enterprise identity of target firm.",
                placeholder = "e.g. Spotify Ltd",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "companyName"
            )
            HighlightField(
                value = data.companyAddress,
                onValueChange = { onDataChanged(data.copy(companyAddress = it)) },
                label = "Company Address",
                promptText = "Physical office, HQ location, or target hiring branch.",
                placeholder = "e.g. Stockholm, Sweden",
                highlightIfEmpty = false,
                focusedField = focusedField,
                fieldName = "companyAddress"
            )
            HighlightField(
                value = data.jobTitle,
                onValueChange = { onDataChanged(data.copy(jobTitle = it)) },
                label = "Job Title Target",
                promptText = "The explicit requisition job title you are applying to.",
                placeholder = "e.g. Lead Mobile Designer",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "jobTitle"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        item {
            Text("⚖️ Letter Body Content (Blank Slate)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            
            HighlightField(
                value = data.paragraph1,
                onValueChange = { onDataChanged(data.copy(paragraph1 = it)) },
                label = "Paragraph 1 (Suggested Opening sentence: 2-3 lines)",
                promptText = "State the target position, how you discovered the job posting, and pitch why you fit.",
                placeholder = "Write paragraph 1 here...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "paragraph1"
            )
            HighlightField(
                value = data.paragraph2,
                onValueChange = { onDataChanged(data.copy(paragraph2 = it)) },
                label = "Paragraph 2 (Suggested Core Project highlights)",
                promptText = "Mention a specific milestone, project metrics, or deep competency showing you solve problem.",
                placeholder = "Write paragraph 2 here...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "paragraph2"
            )
            HighlightField(
                value = data.paragraph3,
                onValueChange = { onDataChanged(data.copy(paragraph3 = it)) },
                label = "Paragraph 3 (Suggested closing statement & CTA)",
                promptText = "Conclude your letter, thank them for review, and request an interview or callback.",
                placeholder = "Write paragraph 3 here...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "paragraph3"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        item {
            Text("✍️ Sign Off Sender details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HighlightField(
                value = data.yourName,
                onValueChange = { onDataChanged(data.copy(yourName = it)) },
                label = "Your Signature Name",
                promptText = "Enter your full name at bottom sign-off block.",
                placeholder = "e.g. Liam Sterling",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "yourName"
            )
            HighlightField(
                value = data.yourContact,
                onValueChange = { onDataChanged(data.copy(yourContact = it)) },
                label = "Your Contact Address/Info",
                promptText = "Email / phone footer text inside sign-off section.",
                placeholder = "e.g. liam.sterling@nexturn.com",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "yourContact"
            )
        }
    }
}

@Composable
fun EmailForm(
    data: EmailData,
    onDataChanged: (EmailData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("✉️ Email Transmission Fields", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            HighlightField(
                value = data.recipientEmail,
                onValueChange = { onDataChanged(data.copy(recipientEmail = it)) },
                label = "To: Recipient Email",
                promptText = "The corporate target inbox address.",
                placeholder = "e.g. HR@acmeglobal.com",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "recipientEmail",
                leadingIcon = Icons.Default.Mail
            )
            HighlightField(
                value = data.subjectLine,
                onValueChange = { onDataChanged(data.copy(subjectLine = it)) },
                label = "Subject Line",
                promptText = "A clean subject line with clear, identifiable scope.",
                placeholder = "e.g. Follow-up: Application for Lead Web Designer",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "subjectLine"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("📝 Email Body (Markdown friendly)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                // Helper insertions (phrase click inserters ONLY if clicked)
                var expandedHelpers by remember { mutableStateOf(false) }
                Box {
                    TextButton(onClick = { expandedHelpers = true }) {
                        Icon(Icons.Default.Bolt, contentDescription = null)
                        Text("Suggested Phrases")
                    }
                    DropdownMenu(expanded = expandedHelpers, onDismissRequest = { expandedHelpers = false }) {
                        DropdownMenuItem(
                            text = { Text("Add Polite Greeting") },
                            onClick = {
                                val added = "Dear Hiring Manager,\n\nI hope this email finds you well."
                                onDataChanged(data.copy(bodyMarkdown = added + "\n" + data.bodyMarkdown))
                                expandedHelpers = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Add Application Inquiry") },
                            onClick = {
                                val added = "I am writing to politely check the status of my candidacy for the target role."
                                onDataChanged(data.copy(bodyMarkdown = data.bodyMarkdown + "\n" + added))
                                expandedHelpers = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Add Call to Action") },
                            onClick = {
                                val added = "Please let me know if there are any supplemental portfolio items or records I can provide. Looking forward to your response."
                                onDataChanged(data.copy(bodyMarkdown = data.bodyMarkdown + "\n\n" + added))
                                expandedHelpers = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            HighlightField(
                value = data.bodyMarkdown,
                onValueChange = { onDataChanged(data.copy(bodyMarkdown = it)) },
                label = "Body Draft",
                promptText = "Suggested structure as comments (Opening: Greeting, Middle: reminder of proposal, Closing: CTA & signatures).",
                placeholder = "<!-- Opening: polite greeting -->\n\n<!-- Middle: key value proposal -->\n\n<!-- Closing: calendar availability -->",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "bodyMarkdown"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        item {
            Text("🔏 Professional Signature block", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HighlightField(
                value = data.signatureName,
                onValueChange = { onDataChanged(data.copy(signatureName = it)) },
                label = "Full Name",
                promptText = "Your name centered at the bottom signature strip.",
                placeholder = "Liam Vance",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "sigName"
            )
            HighlightField(
                value = data.signatureTitle,
                onValueChange = { onDataChanged(data.copy(signatureTitle = it)) },
                label = "Current Job Title",
                promptText = "Current professional title for footers.",
                placeholder = "e.g. Lead Designer",
                highlightIfEmpty = false,
                focusedField = focusedField,
                fieldName = "sigTitle"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.signaturePhone,
                        onValueChange = { onDataChanged(data.copy(signaturePhone = it)) },
                        label = "Contact Phone",
                        promptText = "Direct callback telephone.",
                        placeholder = "+1 022 301",
                        highlightIfEmpty = false,
                        focusedField = focusedField,
                        fieldName = "sigPhone"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.signatureWebsite,
                        onValueChange = { onDataChanged(data.copy(signatureWebsite = it)) },
                        label = "Site URL",
                        promptText = "Personal corporate bio site.",
                        placeholder = "vance.me",
                        highlightIfEmpty = false,
                        focusedField = focusedField,
                        fieldName = "sigWeb"
                    )
                }
            }
        }
    }
}

@Composable
fun InvoiceForm(
    data: InvoiceData,
    onDataChanged: (InvoiceData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    var newItemDesc by remember { mutableStateOf("") }
    var newItemQty by remember { mutableStateOf("1") }
    var newItemPrice by remember { mutableStateOf("0") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("🧾 Invoice Header Ledger", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            HighlightField(
                value = data.invoiceNumber,
                onValueChange = { onDataChanged(data.copy(invoiceNumber = it)) },
                label = "Invoice Number",
                promptText = "Unique identifier format like INV-0001.",
                placeholder = "INV-0001",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "invoiceNum"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.invoiceDate,
                        onValueChange = { onDataChanged(data.copy(invoiceDate = it)) },
                        label = "Issue Date",
                        promptText = "Day of generating this fee ledger.",
                        placeholder = "2026-06-05",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "invoiceDate"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.dueDate,
                        onValueChange = { onDataChanged(data.copy(dueDate = it)) },
                        label = "Due Date",
                        promptText = "Maturity day of payment terms.",
                        placeholder = "2025-07-05",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "dueDate"
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        item {
            Text("🏢 From (Vendor Ledger)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HighlightField(
                value = data.myBusinessName,
                onValueChange = { onDataChanged(data.copy(myBusinessName = it)) },
                label = "My Business/Full Name",
                promptText = "Your sole proprietorship business brand designation.",
                placeholder = "e.g. Vance Labs",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "myBusinessName"
            )
            HighlightField(
                value = data.myAddress,
                onValueChange = { onDataChanged(data.copy(myAddress = it)) },
                label = "My Business Address",
                promptText = "Street, zip code, and billing registered location.",
                placeholder = "e.g. San Francisco, California",
                highlightIfEmpty = false,
                focusedField = focusedField,
                fieldName = "myAddress"
            )
            HighlightField(
                value = data.myTaxId,
                onValueChange = { onDataChanged(data.copy(myTaxId = it)) },
                label = "My Corporate Tax ID",
                promptText = "Official taxation identifier.",
                placeholder = "US-993-91",
                highlightIfEmpty = false,
                focusedField = focusedField,
                fieldName = "myTaxId"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        item {
            Text("👥 Bill To (Client Ledger)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HighlightField(
                value = data.clientName,
                onValueChange = { onDataChanged(data.copy(clientName = it)) },
                label = "Client Name",
                promptText = "Target contact person or client coordinator.",
                placeholder = "e.g. Arthur Pendelton",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "clientName"
            )
            HighlightField(
                value = data.clientCompany,
                onValueChange = { onDataChanged(data.copy(clientCompany = it)) },
                label = "Client Company name",
                promptText = "Entity purchasing service.",
                placeholder = "Spotify Ltd",
                highlightIfEmpty = false,
                focusedField = focusedField,
                fieldName = "clientCompany"
            )
            HighlightField(
                value = data.clientAddress,
                onValueChange = { onDataChanged(data.copy(clientAddress = it)) },
                label = "Client Billing Address",
                promptText = "Client physical corporate dispatch desk.",
                placeholder = "Stockholm, Sweden",
                highlightIfEmpty = false,
                focusedField = focusedField,
                fieldName = "clientAddress"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        item {
            Text("📊 Fee Line Items (Total: $${String.format("%.2f", data.lineItems.sumOf { (it.quantity * it.unitPrice).toDouble() })})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    OutlinedTextField(
                        value = newItemDesc, onValueChange = { newItemDesc = it },
                        label = { Text("Line Item Description") }, placeholder = { Text("e.g. Custom Web Development") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = newItemQty, onValueChange = { newItemQty = it },
                                label = { Text("Quantity") }, placeholder = { Text("1") },
                                singleLine = true
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = newItemPrice, onValueChange = { newItemPrice = it },
                                label = { Text("Unit Price ($)") }, placeholder = { Text("0") },
                                singleLine = true
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            val qty = newItemQty.toFloatOrNull() ?: 1.0f
                            val price = newItemPrice.toFloatOrNull() ?: 0.0f
                            if (newItemDesc.isNotBlank()) {
                                val updated = data.lineItems + InvoiceData.InvoiceItem(newItemDesc, qty, price)
                                onDataChanged(data.copy(lineItems = updated))
                                newItemDesc = ""
                                newItemQty = "1"
                                newItemPrice = "0"
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Add Line Item")
                    }
                }
            }
        }

        itemsIndexed(data.lineItems) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("Qty: ${item.quantity} | Unit Price: $${String.format("%.2f", item.unitPrice)}", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = {
                    val updated = data.lineItems.toMutableList().apply { removeAt(index) }
                    onDataChanged(data.copy(lineItems = updated))
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("📈 Taxation & Discounts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (data.taxRate == 0f) "" else data.taxRate.toString(),
                        onValueChange = { onDataChanged(data.copy(taxRate = it.toFloatOrNull() ?: 0f)) },
                        label = { Text("Tax Rate (%)") },
                        placeholder = { Text("0") },
                        singleLine = true
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (data.discount == 0f) "" else data.discount.toString(),
                        onValueChange = { onDataChanged(data.copy(discount = it.toFloatOrNull() ?: 0f)) },
                        label = { Text("Discount (%)") },
                        placeholder = { Text("0") },
                        singleLine = true
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        item {
            Text("💵 Terms & Instructions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HighlightField(
                value = data.paymentInstructions,
                onValueChange = { onDataChanged(data.copy(paymentInstructions = it)) },
                label = "Payment Instructions",
                promptText = "Wire transfer details, IBAN, bank swift coordinates.",
                placeholder = "Credit card or swift bank routing info...",
                highlightIfEmpty = false,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "payInstructions"
            )
            HighlightField(
                value = data.notesTerms,
                onValueChange = { onDataChanged(data.copy(notesTerms = it)) },
                label = "Notes / Terms",
                promptText = "Net-30, penalty clauses, or delivery warranties.",
                placeholder = "All accounts payable net-30 days...",
                highlightIfEmpty = false,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "notesTerms"
            )
        }
    }
}

@Composable
fun ProposalForm(
    data: ProposalData,
    onDataChanged: (ProposalData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    var newScopePhase by remember { mutableStateOf("") }
    var newDeliverable by remember { mutableStateOf("") }
    var newTimelinePhase by remember { mutableStateOf("") }
    var newTimelineDesc by remember { mutableStateOf("") }
    var newNextStep by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("📄 Proposal Banner Setup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            HighlightField(
                value = data.title,
                onValueChange = { onDataChanged(data.copy(title = it)) },
                label = "Proposal Campaign Title",
                promptText = "Enter project descriptive banner (e.g. Acme Website Overhaul).",
                placeholder = "e.g. Website Overhaul",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "proposalTitle"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        item {
            Text("1️⃣ Section 1: Executive Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HighlightField(
                value = data.executiveSummary,
                onValueChange = { onDataChanged(data.copy(executiveSummary = it)) },
                label = "Executive Summary Narrative",
                promptText = "Outline pain points, strategic solutions, and overall business motivations.",
                placeholder = "Input executive executive summary outline...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "execSummary"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        // Section 2: Scope & Approach
        item {
            Text("2️⃣ Section 2: Scope & Approach", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newScopePhase,
                        onValueChange = { newScopePhase = it },
                        label = { Text("Define Phase Scope") },
                        placeholder = { Text("e.g. Phase 1: User Discovery and Mapping") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = {
                        if (newScopePhase.isNotBlank()) {
                            onDataChanged(data.copy(scope = data.scope + newScopePhase))
                            newScopePhase = ""
                        }
                    }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Scope Item")
                    }
                }
            }
        }

        itemsIndexed(data.scope) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    val updated = data.scope.toMutableList().apply { removeAt(index) }
                    onDataChanged(data.copy(scope = updated))
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        // Section 3: Deliverables Checklist
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("3️⃣ Section 3: Deliverables Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newDeliverable,
                        onValueChange = { newDeliverable = it },
                        label = { Text("Deliverable Item") },
                        placeholder = { Text("e.g. 5x Figma design dashboards") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = {
                        if (newDeliverable.isNotBlank()) {
                            onDataChanged(data.copy(deliverables = data.deliverables + ProposalData.DeliverableItem(newDeliverable, false)))
                            newDeliverable = ""
                        }
                    }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Deliverable")
                    }
                }
            }
        }

        itemsIndexed(data.deliverables) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { chk ->
                            val updated = data.deliverables.toMutableList().apply {
                                set(index, item.copy(isChecked = chk))
                            }
                            onDataChanged(data.copy(deliverables = updated))
                        }
                    )
                    Text(item.title, style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = {
                    val updated = data.deliverables.toMutableList().apply { removeAt(index) }
                    onDataChanged(data.copy(deliverables = updated))
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        // Section 4: Timeline
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("4️⃣ Section 4: Project Timeline Milestones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    OutlinedTextField(
                        value = newTimelinePhase, onValueChange = { newTimelinePhase = it },
                        label = { Text("Timeline Milestone Phase") }, placeholder = { Text("e.g. Week 1-2") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTimelineDesc, onValueChange = { newTimelineDesc = it },
                        label = { Text("Phase Description") }, placeholder = { Text("e.g. Client discovery interviews and wireframes") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (newTimelinePhase.isNotBlank() && newTimelineDesc.isNotBlank()) {
                                onDataChanged(data.copy(
                                    timeline = data.timeline + ProposalData.TimelineItem(newTimelinePhase, newTimelineDesc)
                                ))
                                newTimelinePhase = ""
                                newTimelineDesc = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Add Timeline Phase")
                    }
                }
            }
        }

        itemsIndexed(data.timeline) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.phase, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(item.description, style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = {
                    val updated = data.timeline.toMutableList().apply { removeAt(index) }
                    onDataChanged(data.copy(timeline = updated))
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        // Section 5: Investment
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("5️⃣ Section 5: Financial Investment Detail", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HighlightField(
                value = data.investmentDetails,
                onValueChange = { onDataChanged(data.copy(investmentDetails = it)) },
                label = "Investment Summary (Empty pricing table links)",
                promptText = "Outline milestones payments, hourly/flat rates structures cleanly.",
                placeholder = "Input pricing summary, e.g. Flat Fee of $5,000 paid 50% upfront...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "investDetails"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        // Section 6: Why Us
        item {
            Text("6️⃣ Section 6: Why Choose Me / Credentials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HighlightField(
                value = data.whyMe,
                onValueChange = { onDataChanged(data.copy(whyMe = it)) },
                label = "My Background Pitch",
                promptText = "Unique experience, technology certifications, past successful cases.",
                placeholder = "Enter references, credential stats...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "whyMe"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        // Section 7: Next Steps
        item {
            Text("7️⃣ Section 7: Acceptance & Next Steps", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newNextStep,
                        onValueChange = { newNextStep = it },
                        label = { Text("Required Next Step Action") },
                        placeholder = { Text("e.g. Sign proposal contract and deposit 50% retainer") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = {
                        if (newNextStep.isNotBlank()) {
                            onDataChanged(data.copy(nextSteps = data.nextSteps + newNextStep))
                            newNextStep = ""
                        }
                    }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Step")
                    }
                }
            }
        }

        itemsIndexed(data.nextSteps) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${index + 1}. $item", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    val updated = data.nextSteps.toMutableList().apply { removeAt(index) }
                    onDataChanged(data.copy(nextSteps = updated))
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun OfferLetterForm(
    data: OfferLetterData,
    onDataChanged: (OfferLetterData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("💼 Offer Letter (Job Offer) Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            HighlightField(
                value = data.candidateName,
                onValueChange = { onDataChanged(data.copy(candidateName = it)) },
                label = "Candidate Full Name",
                promptText = "The complete name of the candidate who is receiving this job offer.",
                placeholder = "e.g. Sarah Connor",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "candidateName",
                leadingIcon = Icons.Default.Person
            )
            
            HighlightField(
                value = data.jobTitle,
                onValueChange = { onDataChanged(data.copy(jobTitle = it)) },
                label = "Proposed Job Title",
                promptText = "The exact name of the role the candidate is being hired into.",
                placeholder = "e.g. Senior Staff Security Specialist",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "jobTitle",
                leadingIcon = Icons.Default.Badge
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.salary,
                        onValueChange = { onDataChanged(data.copy(salary = it)) },
                        label = "Proposed Compensation/Salary",
                        promptText = "Annual or monthly payment rate details.",
                        placeholder = "e.g. $140,000 / year",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "salary",
                        leadingIcon = Icons.Default.AttachMoney
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.startDate,
                        onValueChange = { onDataChanged(data.copy(startDate = it)) },
                        label = "Proposed Start Date",
                        promptText = "Calendar date or timeline of commencement.",
                        placeholder = "e.g. July 1st, 2026",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "startDate",
                        leadingIcon = Icons.Default.CalendarToday
                    )
                }
            }

            HighlightField(
                value = data.companyName,
                onValueChange = { onDataChanged(data.copy(companyName = it)) },
                label = "Company Name",
                promptText = "The full legal name of the organization.",
                placeholder = "e.g. Cyberdyne Systems Corp.",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "companyName",
                leadingIcon = Icons.Default.Business
            )

            HighlightField(
                value = data.companyAddress,
                onValueChange = { onDataChanged(data.copy(companyAddress = it)) },
                label = "Company Office Address",
                promptText = "Street, Suite number, and State region locations.",
                placeholder = "e.g. 18111 Nordhoff St, Northridge, CA",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "companyAddress",
                leadingIcon = Icons.Default.Home
            )

            HighlightField(
                value = data.signatoryName,
                onValueChange = { onDataChanged(data.copy(signatoryName = it)) },
                label = "Authorized Signatory Name",
                promptText = "Who is offering this letter? (The hire manager or HR director)",
                placeholder = "e.g. John Miller",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "signatoryName",
                leadingIcon = Icons.Default.Person
            )

            HighlightField(
                value = data.signatoryTitle,
                onValueChange = { onDataChanged(data.copy(signatoryTitle = it)) },
                label = "Signatory Title",
                promptText = "Designation of position of the authorized representative.",
                placeholder = "e.g. VP of Intellectual Property",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "signatoryTitle",
                leadingIcon = Icons.Default.Star
            )

            HighlightField(
                value = data.offerDetails,
                onValueChange = { onDataChanged(data.copy(offerDetails = it)) },
                label = "Additional Offer Details & Benefits",
                promptText = "Health coverages, equity shares, stock option schedules, remote stipulations, etc.",
                placeholder = "Describe medical coverages, annual leaves, bonus structures...",
                highlightIfEmpty = false,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "offerDetails"
            )
        }
    }
}

@Composable
fun ResignationLetterForm(
    data: ResignationLetterData,
    onDataChanged: (ResignationLetterData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("👋 Resignation Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            HighlightField(
                value = data.employeeName,
                onValueChange = { onDataChanged(data.copy(employeeName = it)) },
                label = "My Full Name",
                promptText = "Your name as registered on corporate payroll.",
                placeholder = "e.g. Marcus Wright",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "employeeName",
                leadingIcon = Icons.Default.Person
            )

            HighlightField(
                value = data.managerName,
                onValueChange = { onDataChanged(data.copy(managerName = it)) },
                label = "Manager Name / Recipient",
                promptText = "The full name of your reporting supervisor.",
                placeholder = "e.g. Dr. Serena Kogan",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "managerName",
                leadingIcon = Icons.Default.Person
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1.3f)) {
                    HighlightField(
                        value = data.companyName,
                        onValueChange = { onDataChanged(data.copy(companyName = it)) },
                        label = "Company Name",
                        promptText = "The company you are resigning from.",
                        placeholder = "e.g. Skynet Research",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "companyName",
                        leadingIcon = Icons.Default.Business
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.lastWorkingDay,
                        onValueChange = { onDataChanged(data.copy(lastWorkingDay = it)) },
                        label = "Last Working Day",
                        promptText = "Your final date of employment coverage.",
                        placeholder = "e.g. June 20, 2026",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "lastWorkingDay",
                        leadingIcon = Icons.Default.CalendarToday
                    )
                }
            }

            HighlightField(
                value = data.resignationReason,
                onValueChange = { onDataChanged(data.copy(resignationReason = it)) },
                label = "Primary Reason for Departure",
                promptText = "Brief indicator of intent. Best kept polite and constructive.",
                placeholder = "e.g. Pursuing a new, challenging career development path...",
                highlightIfEmpty = false,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "resignationReason"
            )

            HighlightField(
                value = data.personalNote,
                onValueChange = { onDataChanged(data.copy(personalNote = it)) },
                label = "Gratitude statement or personal message",
                promptText = "Thanking colleagues for mentoring opportunities.",
                placeholder = "e.g. Thank you so much for your mentorship over the past years. I am proud of what the system achieved...",
                highlightIfEmpty = false,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "personalNote"
            )

            HighlightField(
                value = data.signatureName,
                onValueChange = { onDataChanged(data.copy(signatureName = it)) },
                label = "Sign-off Signature Block",
                promptText = "The final name printed at bottom of letter.",
                placeholder = "e.g. Marcus",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "signatureName",
                leadingIcon = Icons.Default.Edit
            )
        }
    }
}

@Composable
fun ServiceContractForm(
    data: ServiceContractData,
    onDataChanged: (ServiceContractData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("🤝 Service Contract / Agreement details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            HighlightField(
                value = data.contractorName,
                onValueChange = { onDataChanged(data.copy(contractorName = it)) },
                label = "Service Provider / Contractor Name",
                promptText = "Individual or agency supplying professional expertise.",
                placeholder = "e.g. John Connor Consulting",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "contractorName",
                leadingIcon = Icons.Default.Group
            )

            HighlightField(
                value = data.clientName,
                onValueChange = { onDataChanged(data.copy(clientName = it)) },
                label = "Client / Contracting Entity Name",
                promptText = "Customer paying for execution deliverables.",
                placeholder = "e.g. Resistance Command HQ",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "clientName",
                leadingIcon = Icons.Default.Person
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.agreementDate,
                        onValueChange = { onDataChanged(data.copy(agreementDate = it)) },
                        label = "Agreement Effective Date",
                        promptText = "Starting date when conditions apply.",
                        placeholder = "e.g. August 29, 2026",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "agreementDate",
                        leadingIcon = Icons.Default.CalendarToday
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.governingLaw,
                        onValueChange = { onDataChanged(data.copy(governingLaw = it)) },
                        label = "Governing Jurisdiction",
                        promptText = "The legal region settling potential contract disputes.",
                        placeholder = "e.g. California",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "governingLaw",
                        leadingIcon = Icons.Default.Lock
                    )
                }
            }

            HighlightField(
                value = data.compensation,
                onValueChange = { onDataChanged(data.copy(compensation = it)) },
                label = "Contract Project Budget or Rate",
                promptText = "Specify flat retainers or billable hours details.",
                placeholder = "e.g. Fixed lump-sum payment of $45,000",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "compensation",
                leadingIcon = Icons.Default.AttachMoney
            )

            HighlightField(
                value = data.paymentTerms,
                onValueChange = { onDataChanged(data.copy(paymentTerms = it)) },
                label = "Invoice / Milestone Payment Terms",
                promptText = "When should invoice balances be cleared?",
                placeholder = "e.g. Net 15 days upon milestone submission approval",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "paymentTerms"
            )

            HighlightField(
                value = data.scopeOfWork,
                onValueChange = { onDataChanged(data.copy(scopeOfWork = it)) },
                label = "Scope of Work Description",
                promptText = "An exhaustive checklist of specific deliverables to complete.",
                placeholder = "Identify exact technical tasks, reporting structures, code migrations...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "scopeOfWork"
            )
        }
    }
}

@Composable
fun CertificateForm(
    data: CertificateData,
    onDataChanged: (CertificateData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("🏆 Certificate of Achievement Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            HighlightField(
                value = data.recipientName,
                onValueChange = { onDataChanged(data.copy(recipientName = it)) },
                label = "Recipient Name",
                promptText = "The recipient of the certificate.",
                placeholder = "e.g. Katherine Brewster",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "recipientName",
                leadingIcon = Icons.Default.Person
            )

            HighlightField(
                value = data.achievementTitle,
                onValueChange = { onDataChanged(data.copy(achievementTitle = it)) },
                label = "Award/Achievement Title",
                promptText = "Name of the qualification or achievement milestone.",
                placeholder = "e.g. Advanced Battlefield Logistics & Defense",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "achievementTitle",
                leadingIcon = Icons.Default.Star
            )

            HighlightField(
                value = data.awardingOrg,
                onValueChange = { onDataChanged(data.copy(awardingOrg = it)) },
                label = "Awarding Entity / Company Name",
                promptText = "The granting institution or enterprise sponsor.",
                placeholder = "e.g. Division 2 Tactical Logistics Association",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "awardingOrg",
                leadingIcon = Icons.Default.Business
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.dateOfIssue,
                        onValueChange = { onDataChanged(data.copy(dateOfIssue = it)) },
                        label = "Date of Issue",
                        promptText = "The official validation stamp date.",
                        placeholder = "e.g. November 28, 2026",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "dateOfIssue",
                        leadingIcon = Icons.Default.CalendarToday
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.authoritySignatory,
                        onValueChange = { onDataChanged(data.copy(authoritySignatory = it)) },
                        label = "Authorized Signatory",
                        promptText = "Full name and title of certifying official.",
                        placeholder = "e.g. General Robert Brewster, US Air Force",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "authoritySignatory",
                        leadingIcon = Icons.Default.Edit
                    )
                }
            }

            HighlightField(
                value = data.certificateDescription,
                onValueChange = { onDataChanged(data.copy(certificateDescription = it)) },
                label = "Certificate Description Summary",
                promptText = "Brief body paragraph highlighting the recipient's exceptional accomplishments.",
                placeholder = "Describe their exemplary participation, technical leadership, and strategic execution skills...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "certificateDescription"
            )
        }
    }
}

@Composable
fun MeetingMinutesForm(
    data: MeetingMinutesData,
    onDataChanged: (MeetingMinutesData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    var newActionItem by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("📝 Meeting Minutes Detail Board", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))

            HighlightField(
                value = data.meetingTitle,
                onValueChange = { onDataChanged(data.copy(meetingTitle = it)) },
                label = "Meeting Agenda / Title",
                promptText = "The header subject of discussion.",
                placeholder = "e.g. Strategic Defense Software Synch",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "meetingTitle",
                leadingIcon = Icons.Default.Edit
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.meetingDate,
                        onValueChange = { onDataChanged(data.copy(meetingDate = it)) },
                        label = "Date & Time",
                        promptText = "When the session took place.",
                        placeholder = "e.g. Sept 4, 2026, 14:00 EST",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "meetingDate",
                        leadingIcon = Icons.Default.CalendarToday
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.facilitator,
                        onValueChange = { onDataChanged(data.copy(facilitator = it)) },
                        label = "Facilitator / Chair",
                        promptText = "Who directed proceedings.",
                        placeholder = "e.g. Lt. Commander Kate Brewster",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "facilitator",
                        leadingIcon = Icons.Default.Person
                    )
                }
            }

            HighlightField(
                value = data.attendees,
                onValueChange = { onDataChanged(data.copy(attendees = it)) },
                label = "Attendees List",
                promptText = "Comma-delimited full names of participants.",
                placeholder = "e.g. John C., Katherine B., Marcus W., Serena K.",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "attendees",
                leadingIcon = Icons.Default.Group
            )

            HighlightField(
                value = data.discussionSummary,
                onValueChange = { onDataChanged(data.copy(discussionSummary = it)) },
                label = "Core Discussion Summary",
                promptText = "Key debate nodes, project concerns, strategic updates.",
                placeholder = "Summarize the primary discussions, technology choices, action roadblocks...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "discussionSummary"
            )

            HighlightField(
                value = data.nextMeetingDate,
                onValueChange = { onDataChanged(data.copy(nextMeetingDate = it)) },
                label = "Next Meeting Date target",
                promptText = "Next tracking session milestone.",
                placeholder = "e.g. October 1, 2026, 10:00 EST",
                highlightIfEmpty = false,
                focusedField = focusedField,
                fieldName = "nextMeetingDate",
                leadingIcon = Icons.Default.CalendarToday
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("✅ Action Items Checklist", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newActionItem,
                        onValueChange = { newActionItem = it },
                        label = { Text("New Action Item Task") },
                        placeholder = { Text("e.g. Review firewall configuration files") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = {
                        if (newActionItem.isNotBlank()) {
                            onDataChanged(data.copy(actionItems = data.actionItems + newActionItem))
                            newActionItem = ""
                        }
                    }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Item")
                    }
                }
            }
        }

        itemsIndexed(data.actionItems) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${index + 1}. $item", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    val updated = data.actionItems.toMutableList().apply { removeAt(index) }
                    onDataChanged(data.copy(actionItems = updated))
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun BusinessLetterForm(
    data: BusinessLetterData,
    onDataChanged: (BusinessLetterData) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("✉️ General Business Letter details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            HighlightField(
                value = data.senderAddress,
                onValueChange = { onDataChanged(data.copy(senderAddress = it)) },
                label = "Sender Contact / Address Block",
                promptText = "Your name, title, and organization's mailing address.",
                placeholder = "e.g. John Connor Consulting\n120 Resistance Way, Suite 4\nLos Angeles, CA 90025",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "senderAddress"
            )

            HighlightField(
                value = data.recipientAddress,
                onValueChange = { onDataChanged(data.copy(recipientAddress = it)) },
                label = "Recipient Mailing Address Block",
                promptText = "The full receiving organization address.",
                placeholder = "e.g. Cyberdyne Systems Corp\nAttention: Legal Affairs\n18111 Nordhoff St, Northridge, CA",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "recipientAddress"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.date,
                        onValueChange = { onDataChanged(data.copy(date = it)) },
                        label = "Document Date",
                        promptText = "Official letter delivery date.",
                        placeholder = "e.g. August 29, 2026",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "date",
                        leadingIcon = Icons.Default.CalendarToday
                    )
                }
                Box(modifier = Modifier.weight(1.2f)) {
                    HighlightField(
                        value = data.subject,
                        onValueChange = { onDataChanged(data.copy(subject = it)) },
                        label = "Subject Line",
                        promptText = "Indicator of letter's contents.",
                        placeholder = "e.g. NOTICE OF INTENT TO TERMINATE DATA INTEGRATION",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "subject",
                        leadingIcon = Icons.Default.Info
                    )
                }
            }

            HighlightField(
                value = data.salutation,
                onValueChange = { onDataChanged(data.copy(salutation = it)) },
                label = "Salutation Greeting",
                promptText = "Polite greeting to open the text.",
                placeholder = "e.g. Dear Sir or Madam,",
                highlightIfEmpty = highlightIfEmpty,
                focusedField = focusedField,
                fieldName = "salutation",
                leadingIcon = Icons.Default.Edit
            )

            HighlightField(
                value = data.paragraph1,
                onValueChange = { onDataChanged(data.copy(paragraph1 = it)) },
                label = "Introduction Paragraph 1",
                promptText = "State the main purpose of your letter immediately.",
                placeholder = "Writing to formally notify you regarding...",
                highlightIfEmpty = highlightIfEmpty,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "paragraph1"
            )

            HighlightField(
                value = data.paragraph2,
                onValueChange = { onDataChanged(data.copy(paragraph2 = it)) },
                label = "Detail Body Paragraph 2",
                promptText = "Elaborate with specific legal, financial, or engineering detail blocks.",
                placeholder = "Over consecutive iterations, our tech stack detected...",
                highlightIfEmpty = false,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "paragraph2"
            )

            HighlightField(
                value = data.paragraph3,
                onValueChange = { onDataChanged(data.copy(paragraph3 = it)) },
                label = "Conclusion Paragraph 3 & Call to action",
                promptText = "A final wrap-up statement indicating required next steps.",
                placeholder = "Kindly respond within Net 15 business days to clear the query...",
                highlightIfEmpty = false,
                isMultiline = true,
                focusedField = focusedField,
                fieldName = "paragraph3"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    HighlightField(
                        value = data.valediction,
                        onValueChange = { onDataChanged(data.copy(valediction = it)) },
                        label = "Closing Valediction",
                        promptText = "Standard formal closing phrase.",
                        placeholder = "e.g. Respectfully yours,",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "valediction",
                        leadingIcon = Icons.Default.Edit
                    )
                }
                Box(modifier = Modifier.weight(1.2f)) {
                    HighlightField(
                        value = data.senderName,
                        onValueChange = { onDataChanged(data.copy(senderName = it)) },
                        label = "Sender Printed Name",
                        promptText = "Your name for the signature line.",
                        placeholder = "e.g. John Connor",
                        highlightIfEmpty = highlightIfEmpty,
                        focusedField = focusedField,
                        fieldName = "senderName",
                        leadingIcon = Icons.Default.Person
                    )
                }
            }
        }
    }
}

