package com.example.ui

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import com.example.util.DocExporter
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexturnDashboard(viewModel: DocumentViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: CV, 1: Cover Letter, 2: Email, 3: Invoice, 4: Proposal

    // Dual-pane view tracker for compact phones: 0 -> View Form, 1 -> View Document Preview Page
    var compactModePane by remember { mutableIntStateOf(0) }

    // Active focused input text (triggers help prompt explanations)
    val focusedField = remember { mutableStateOf("") }

    // Validation warning triggers
    var highlightIfEmpty by remember { mutableStateOf(false) }
    var showExportWarningDialog by remember { mutableStateOf(false) }
    var pendingExportAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Active collected documents data
    val cvData by viewModel.activeCv.collectAsStateWithLifecycle()
    val cvTemplates by viewModel.savedCvTemplates.collectAsStateWithLifecycle()
    val curCvTemplateName by viewModel.curCvTemplateName.collectAsStateWithLifecycle()

    val coverLetterData by viewModel.activeCoverLetter.collectAsStateWithLifecycle()
    val coverLetterTemplates by viewModel.savedCoverLetterTemplates.collectAsStateWithLifecycle()
    val curCoverLetterTemplateName by viewModel.curCoverLetterTemplateName.collectAsStateWithLifecycle()

    val emailData by viewModel.activeEmail.collectAsStateWithLifecycle()
    val emailTemplates by viewModel.savedEmailTemplates.collectAsStateWithLifecycle()
    val curEmailTemplateName by viewModel.curEmailTemplateName.collectAsStateWithLifecycle()

    val invoiceData by viewModel.activeInvoice.collectAsStateWithLifecycle()
    val invoiceTemplates by viewModel.savedInvoiceTemplates.collectAsStateWithLifecycle()
    val curInvoiceTemplateName by viewModel.curInvoiceTemplateName.collectAsStateWithLifecycle()

    val proposalData by viewModel.activeProposal.collectAsStateWithLifecycle()
    val proposalTemplates by viewModel.savedProposalTemplates.collectAsStateWithLifecycle()
    val curProposalTemplateName by viewModel.curProposalTemplateName.collectAsStateWithLifecycle()

    // Required fields check helper
    fun validateRequiredFields(): Boolean {
        return when (selectedTab) {
            0 -> cvData.fullName.isNotBlank() && cvData.jobTitle.isNotBlank()
            1 -> coverLetterData.companyName.isNotBlank() && coverLetterData.jobTitle.isNotBlank() && coverLetterData.yourName.isNotBlank()
            2 -> emailData.recipientEmail.isNotBlank() && emailData.subjectLine.isNotBlank() && emailData.bodyMarkdown.isNotBlank()
            3 -> invoiceData.invoiceNumber.isNotBlank() && invoiceData.clientName.isNotBlank() && invoiceData.myBusinessName.isNotBlank()
            4 -> proposalData.title.isNotBlank() && proposalData.executiveSummary.isNotBlank()
            else -> true
        }
    }

    // High performance offline file exporter router
    fun triggerFileAction(format: String, actionType: String) { // format: "PDF", "TXT", "print", "share"
        val isCompleted = validateRequiredFields()
        
        val performAction = {
            try {
                when (selectedTab) {
                    0 -> { // CV
                        if (format == "TXT") {
                            val txt = DocExporter.generateCvTxt(cvData)
                            val file = DocExporter.writeTextToFile(context, txt, "cv_resume.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateCvPdf(context, cvData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                    1 -> { // Cover Letter
                        if (format == "TXT") {
                            val txt = DocExporter.generateCoverLetterTxt(coverLetterData)
                            val file = DocExporter.writeTextToFile(context, txt, "cover_letter.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateCoverLetterPdf(context, coverLetterData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                    2 -> { // Email
                        if (format == "TXT") {
                            val txt = DocExporter.generateEmailTxt(emailData)
                            val file = DocExporter.writeTextToFile(context, txt, "email.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateEmailPdf(context, emailData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                    3 -> { // Invoice
                        if (format == "TXT") {
                            val txt = DocExporter.generateInvoiceTxt(invoiceData)
                            val file = DocExporter.writeTextToFile(context, txt, "invoice.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateInvoicePdf(context, invoiceData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                    4 -> { // Proposal
                        if (format == "TXT") {
                            val txt = DocExporter.generateProposalTxt(proposalData)
                            val file = DocExporter.writeTextToFile(context, txt, "project_proposal.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateProposalPdf(context, proposalData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Offline export error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        if (!isCompleted) {
            highlightIfEmpty = true
            pendingExportAction = performAction
            showExportWarningDialog = true
        } else {
            performAction()
        }
    }

    // Dynamic prompt titles corresponding to selected field to prompt user step-by-step
    val activePromptTitle = when (focusedField.value) {
        "fullName" -> "Step Prompt: Personal Name"
        "jobTitle" -> "Step Prompt: Target Role Requisition"
        "professionalSummary" -> "Step Prompt: Character Summary Pitch"
        "hiringManager" -> "Step Prompt: Letter Recipient Address"
        "companyName" -> "Step Prompt: Target Enterprise Hiring Body"
        "paragraph1" -> "Step Prompt: Letter Opening sentences"
        "paragraph2" -> "Step Prompt: Key tech certifications / case achievements"
        "paragraph3" -> "Step Prompt: Call to Action interview availability"
        "recipientEmail" -> "Step Prompt: Destination Mail inbox address"
        "subjectLine" -> "Step Prompt: Email clear subject topic line"
        "bodyMarkdown" -> "Step Prompt: Message narrative markdown grids"
        "invoiceNum" -> "Step Prompt: Ledger ID Reference code"
        "clientName" -> "Step Prompt: Client accounts coordination name"
        "myBusinessName" -> "Step Prompt: Vendor sole proprietorship designation"
        "proposalTitle" -> "Step Prompt: Project Overhaul Campaign Label"
        "execSummary" -> "Step Prompt: Project Executive Motivations summary"
        else -> "Nexturn Master Assistant"
    }

    val activePromptText = when (focusedField.value) {
        "fullName" -> "Input your official full name. It will render in elegant headline bold typography at the top of the CV."
        "jobTitle" -> "State the target position you seek. Good role customization doubles callback conversion rates!"
        "professionalSummary" -> "Draft 3-4 sentences outlining your cumulative senior years, specialty stacks, and commercial value."
        "hiringManager" -> "If known, enter the manager's name (e.g. Dr. Arthur Pendelton) or department head to create personal warmth from word-one."
        "companyName" -> "Write the official trade brand of the company. Look up precise spelling to show premium care."
        "paragraph1" -> "Draft your opening pitch: express high interest, state where you discovered the vacancy, and state why your track record fits."
        "paragraph2" -> "Highlight 1 or 2 past projects. Include clean numbers/gains (e.g. 'boosted speeds by 40%') which instantly prove expertise."
        "paragraph3" -> "Express thankfulness for their hiring process. Proactively prompt a meeting call or callback schedule with warm regards."
        "recipientEmail" -> "Specify the exact recruiter contact inbox. This keeps the transaction clean."
        "subjectLine" -> "A clear, concise topic subject ensures higher open ratios. Mention job reference codes if available."
        "bodyMarkdown" -> "Input empty narrative blocks following our guides: polite greeting, proposal reminder, value points, CTA."
        "invoiceNum" -> "Establish a professional invoicing numbering scheme, e.g. INV-0001, and keep track locally."
        "clientName" -> "The primary stakeholder responsible for billing clearance. Add Client Company name & billing locations below."
        "myBusinessName" -> "Your commercial brand trading identity. Displays under your vendor payment address block."
        "proposalTitle" -> "A strong proposal title focuses the strategic scope (e.g. Spotify iOS Application Overhaul)."
        "execSummary" -> "Motivate why you are pitch-aligned. Summarize user pain points, the strategic approach, and cumulative business outcomes."
        else -> "Select or click any empty field in the editor below or tap a bracketed placeholder [Click to Fill] on the document preview to begin real-time wizard entry."
    }

    val isDarkByState by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val themeIndexByState by viewModel.selectedThemeIndex.collectAsStateWithLifecycle()

    // Material 3 dynamic theme colors applied directly
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Nexturn Workspace", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Real-Time Zero-Data Clean Templates Room", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        // Custom dynamic theme buttons
                        ThemeDropdownSelector(viewModel)

                        IconButton(onClick = { viewModel.toggleThemeMode() }) {
                            Icon(
                                imageVector = if (isDarkByState) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Dark/Light Mode",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Quick Action Buttons
                        IconButton(onClick = { viewModel.resetDocument(when(selectedTab) {
                            0->"CV" ; 1->"COVER_LETTER" ; 2->"EMAIL" ; 3->"INVOICE" ; else->"PROPOSAL"
                        }) }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Blank", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Column {
                        // Compact mobile layout pane flip buttons ("Forms" or "Preview Sheet")
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            if (maxWidth < 720.dp) {
                                TabRow(
                                    selectedTabIndex = compactModePane,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.height(42.dp)
                                ) {
                                    Tab(
                                        selected = compactModePane == 0,
                                        onClick = { compactModePane = 0 },
                                        text = { Text("📝 Form Editor", style = MaterialTheme.typography.titleSmall) }
                                    )
                                    Tab(
                                        selected = compactModePane == 1,
                                        onClick = { compactModePane = 1 },
                                        text = { Text("👁️ Paper Preview", style = MaterialTheme.typography.titleSmall) }
                                    )
                                }
                            }
                        }

                        // Bottom primary navigation: 5 document tabs (M3 consistent pill indicators)
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = {
                                    selectedTab = 0
                                    focusedField.value = ""
                                },
                                icon = { Icon(Icons.Default.Badge, contentDescription = "CV") },
                                label = { Text("CV", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = {
                                    selectedTab = 1
                                    focusedField.value = ""
                                },
                                icon = { Icon(Icons.Default.Description, contentDescription = "Cover Letter") },
                                label = { Text("Letter", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = {
                                    selectedTab = 2
                                    focusedField.value = ""
                                },
                                icon = { Icon(Icons.Default.Mail, contentDescription = "Email") },
                                label = { Text("Email", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = selectedTab == 3,
                                onClick = {
                                    selectedTab = 3
                                    focusedField.value = ""
                                },
                                icon = { Icon(Icons.Default.Receipt, contentDescription = "Invoice") },
                                label = { Text("Invoice", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = selectedTab == 4,
                                onClick = {
                                    selectedTab = 4
                                    focusedField.value = ""
                                },
                                icon = { Icon(Icons.Default.Assessment, contentDescription = "Proposal") },
                                label = { Text("Proposal", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val isWideLayout = maxWidth >= 720.dp

                if (isWideLayout) {
                    // WIDE SCREEN: Elegant Landscape Side-By-Side layout (Form left, preview right)
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left Pan: Form inputs & wizards (60% width)
                        Column(
                            modifier = Modifier
                                .weight(1.05f)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            // Dynamic customize theme switcher visual card
                            ThemeSelectorCard(viewModel)

                            // Prompt Guidance wizard box
                            PromptBox(title = activePromptTitle, prompt = activePromptText)

                            // Local Templates manager block
                            TemplateHeaderSection(selectedTab, viewModel, cvTemplates, curCvTemplateName, coverLetterTemplates, curCoverLetterTemplateName, emailTemplates, curEmailTemplateName, invoiceTemplates, curInvoiceTemplateName, proposalTemplates, curProposalTemplateName)

                            Spacer(modifier = Modifier.height(10.dp))

                            // Render Selected Form
                            Box(modifier = Modifier.weight(1f)) {
                                RenderActiveForm(selectedTab, viewModel, cvData, coverLetterData, emailData, invoiceData, proposalData, focusedField, highlightIfEmpty)
                            }
                        }

                        // Right Pan: Light paper document mockup preview (40% width) + export utility bar
                        Column(
                            modifier = Modifier
                                .weight(0.95f)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { triggerFileAction("PDF", "share") }) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Share Document", color = MaterialTheme.colorScheme.onSurface)
                                }
                                TextButton(onClick = { triggerFileAction("PDF", "print") }) {
                                    Icon(Icons.Default.Print, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Print Layout", color = MaterialTheme.colorScheme.onSurface)
                                }
                                TextButton(onClick = { triggerFileAction("TXT", "share") }) {
                                    Icon(Icons.Default.Save, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Export TXT", color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(modifier = Modifier.weight(1f)) {
                                RenderActivePreview(selectedTab, cvData, coverLetterData, emailData, invoiceData, proposalData) { clickedFieldName ->
                                    focusedField.value = clickedFieldName
                                }
                            }
                        }
                    }
                } else {
                    // COMPACT MOBILE: Swipe / tab layouts to focus perfectly on screen estate
                    if (compactModePane == 0) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            // Dynamic customize theme switcher visual card for compact mobile screens
                            ThemeSelectorCard(viewModel)

                            PromptBox(title = activePromptTitle, prompt = activePromptText)
                            TemplateHeaderSection(selectedTab, viewModel, cvTemplates, curCvTemplateName, coverLetterTemplates, curCoverLetterTemplateName, emailTemplates, curEmailTemplateName, invoiceTemplates, curInvoiceTemplateName, proposalTemplates, curProposalTemplateName)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                RenderActiveForm(selectedTab, viewModel, cvData, coverLetterData, emailData, invoiceData, proposalData, focusedField, highlightIfEmpty)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { triggerFileAction("PDF", "share") }) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text("Share Document", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                                }
                                TextButton(onClick = { triggerFileAction("PDF", "print") }) {
                                    Icon(Icons.Default.Print, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text("Print", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                                }
                                TextButton(onClick = { triggerFileAction("TXT", "share") }) {
                                    Icon(Icons.Default.Save, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text("Export TXT", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                RenderActivePreview(selectedTab, cvData, coverLetterData, emailData, invoiceData, proposalData) { clickedFieldName ->
                                    focusedField.value = clickedFieldName
                                    compactModePane = 0 // Switch back to form so they can type instantly!
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Export Warning Alert (Gentle highlights on empty required inputs)
    if (showExportWarningDialog) {
        AlertDialog(
            onDismissRequest = { showExportWarningDialog = false },
            title = { Text("⚠️ Empty Required Fields Found") },
            text = {
                Text("Under Nexturn security guidelines, we noticed your document template contains empty values. We have highlighted them gently. You can still force generate with bracketed placeholders, or fill them in now.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExportWarningDialog = false
                        pendingExportAction?.invoke()
                    }
                ) {
                    Text("Force Export with Blanks")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportWarningDialog = false }) {
                    Text("Cancel & Fill Form")
                }
            }
        )
    }
}

@Composable
fun TemplateHeaderSection(
    selectedTab: Int,
    viewModel: DocumentViewModel,
    cvTemplates: List<SavedDocument>,
    curCvTemplateName: String?,
    coverLetterTemplates: List<SavedDocument>,
    curCoverLetterTemplateName: String?,
    emailTemplates: List<SavedDocument>,
    curEmailTemplateName: String?,
    invoiceTemplates: List<SavedDocument>,
    curInvoiceTemplateName: String?,
    proposalTemplates: List<SavedDocument>,
    curProposalTemplateName: String?
) {
    when (selectedTab) {
        0 -> TemplateSettingsRow("CV", curCvTemplateName, cvTemplates,
            onSaveRequested = { name -> viewModel.saveAsTemplate("CV", name) {} },
            onLoadRequested = { doc -> viewModel.loadTemplate(doc) },
            onDeleteRequested = { id -> viewModel.deleteTemplate(id) },
            onResetRequested = { viewModel.resetDocument("CV") }
        )
        1 -> TemplateSettingsRow("COVER_LETTER", curCoverLetterTemplateName, coverLetterTemplates,
            onSaveRequested = { name -> viewModel.saveAsTemplate("COVER_LETTER", name) {} },
            onLoadRequested = { doc -> viewModel.loadTemplate(doc) },
            onDeleteRequested = { id -> viewModel.deleteTemplate(id) },
            onResetRequested = { viewModel.resetDocument("COVER_LETTER") }
        )
        2 -> TemplateSettingsRow("EMAIL", curEmailTemplateName, emailTemplates,
            onSaveRequested = { name -> viewModel.saveAsTemplate("EMAIL", name) {} },
            onLoadRequested = { doc -> viewModel.loadTemplate(doc) },
            onDeleteRequested = { id -> viewModel.deleteTemplate(id) },
            onResetRequested = { viewModel.resetDocument("EMAIL") }
        )
        3 -> TemplateSettingsRow("INVOICE", curInvoiceTemplateName, invoiceTemplates,
            onSaveRequested = { name -> viewModel.saveAsTemplate("INVOICE", name) {} },
            onLoadRequested = { doc -> viewModel.loadTemplate(doc) },
            onDeleteRequested = { id -> viewModel.deleteTemplate(id) },
            onResetRequested = { viewModel.resetDocument("INVOICE") }
        )
        4 -> TemplateSettingsRow("PROPOSAL", curProposalTemplateName, proposalTemplates,
            onSaveRequested = { name -> viewModel.saveAsTemplate("PROPOSAL", name) {} },
            onLoadRequested = { doc -> viewModel.loadTemplate(doc) },
            onDeleteRequested = { id -> viewModel.deleteTemplate(id) },
            onResetRequested = { viewModel.resetDocument("PROPOSAL") }
        )
    }
}

@Composable
fun RenderActiveForm(
    selectedTab: Int,
    viewModel: DocumentViewModel,
    cvData: CvData,
    coverLetterData: CoverLetterData,
    emailData: EmailData,
    invoiceData: InvoiceData,
    proposalData: ProposalData,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    when (selectedTab) {
        0 -> CvForm(cvData, { viewModel.updateCv { _ -> it } }, focusedField, highlightIfEmpty)
        1 -> CoverLetterForm(coverLetterData, { viewModel.updateCoverLetter { _ -> it } }, focusedField, highlightIfEmpty)
        2 -> EmailForm(emailData, { viewModel.updateEmail { _ -> it } }, focusedField, highlightIfEmpty)
        3 -> InvoiceForm(invoiceData, { viewModel.updateInvoice { _ -> it } }, focusedField, highlightIfEmpty)
        4 -> ProposalForm(proposalData, { viewModel.updateProposal { _ -> it } }, focusedField, highlightIfEmpty)
    }
}

@Composable
fun RenderActivePreview(
    selectedTab: Int,
    cvData: CvData,
    coverLetterData: CoverLetterData,
    emailData: EmailData,
    invoiceData: InvoiceData,
    proposalData: ProposalData,
    onNavigateToField: (String) -> Unit
) {
    when (selectedTab) {
        0 -> CvDocumentPreview(cvData, onNavigateToField)
        1 -> CoverLetterDocumentPreview(coverLetterData, onNavigateToField)
        2 -> EmailDocumentPreview(emailData, onNavigateToField)
        3 -> InvoiceDocumentPreview(invoiceData, onNavigateToField)
        4 -> ProposalDocumentPreview(proposalData, onNavigateToField)
    }
}

@Composable
fun ThemeSelectorCard(viewModel: DocumentViewModel) {
    val selectedThemeIndex by viewModel.selectedThemeIndex.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    val themes = listOf(
        Triple("Lavender Mint", "🌸", if (isDark) LavenderDarkPrimary else LavenderLightPrimary),
        Triple("Peach Cream", "🍑", if (isDark) PeachDarkPrimary else PeachLightPrimary),
        Triple("Cherry Blossom", "🍒", if (isDark) CherryDarkPrimary else CherryLightPrimary),
        Triple("Ocean Breeze", "🌊", if (isDark) OceanDarkPrimary else OceanLightPrimary),
        Triple("Sage Garden", "🌿", if (isDark) SageDarkPrimary else SageLightPrimary)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Customize Theme Room",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isDark,
                        onCheckedChange = { viewModel.toggleThemeMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        thumbContent = {
                            Icon(
                                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                themes.forEachIndexed { index, (name, emoji, color) ->
                    val isSelected = selectedThemeIndex == index
                    val containerColor = if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent
                    val border = if (isSelected) BorderStroke(1.5.dp, color) else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .background(containerColor, RoundedCornerShape(12.dp))
                            .border(border, RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectTheme(index) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = name.split(" ")[0],
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeDropdownSelector(viewModel: DocumentViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val currentThemeIx by viewModel.selectedThemeIndex.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    val themes = listOf(
        Triple("Lavender Mint", "🌸", if (isDark) LavenderDarkPrimary else LavenderLightPrimary),
        Triple("Peach Cream", "🍑", if (isDark) PeachDarkPrimary else PeachLightPrimary),
        Triple("Cherry Blossom", "🍒", if (isDark) CherryDarkPrimary else CherryLightPrimary),
        Triple("Ocean Breeze", "🌊", if (isDark) OceanDarkPrimary else OceanLightPrimary),
        Triple("Sage Garden", "🌿", if (isDark) SageDarkPrimary else SageLightPrimary)
    )

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.Palette,
                contentDescription = "Select Pastel Theme",
                tint = themes[currentThemeIx].third
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            themes.forEachIndexed { index, (name, emoji, color) ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(color, shape = RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$emoji $name", color = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    onClick = {
                        viewModel.selectTheme(index)
                        expanded = false
                    }
                )
            }
        }
    }
}
