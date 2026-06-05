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
        // STYLE & LAYOUT SELECTORS
        item {
            Text("🎨 CV/Résumé Presentation Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Style Pref
                Column(modifier = Modifier.weight(1f)) {
                    Text("Theme Style", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    listOf("Modern", "Minimal", "Professional", "Creative").forEach { style ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (data.stylePreference == style),
                                onClick = { onDataChanged(data.copy(stylePreference = style)) }
                            )
                            Text(style, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                // Layout Pref
                Column(modifier = Modifier.weight(1f)) {
                    Text("Column Layout", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    listOf("Sidebar Layout", "Single Column").forEach { ly ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (data.layout == ly),
                                onClick = { onDataChanged(data.copy(layout = ly)) }
                            )
                            Text(ly, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

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
