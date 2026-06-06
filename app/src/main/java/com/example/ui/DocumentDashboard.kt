package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    val offerLetterData by viewModel.activeOfferLetter.collectAsStateWithLifecycle()
    val offerLetterTemplates by viewModel.savedOfferLetterTemplates.collectAsStateWithLifecycle()
    val curOfferLetterTemplateName by viewModel.curOfferLetterTemplateName.collectAsStateWithLifecycle()

    val resignationLetterData by viewModel.activeResignationLetter.collectAsStateWithLifecycle()
    val resignationLetterTemplates by viewModel.savedResignationLetterTemplates.collectAsStateWithLifecycle()
    val curResignationLetterTemplateName by viewModel.curResignationLetterTemplateName.collectAsStateWithLifecycle()

    val serviceContractData by viewModel.activeServiceContract.collectAsStateWithLifecycle()
    val serviceContractTemplates by viewModel.savedServiceContractTemplates.collectAsStateWithLifecycle()
    val curServiceContractTemplateName by viewModel.curServiceContractTemplateName.collectAsStateWithLifecycle()

    val certificateData by viewModel.activeCertificate.collectAsStateWithLifecycle()
    val certificateTemplates by viewModel.savedCertificateTemplates.collectAsStateWithLifecycle()
    val curCertificateTemplateName by viewModel.curCertificateTemplateName.collectAsStateWithLifecycle()

    val meetingMinutesData by viewModel.activeMeetingMinutes.collectAsStateWithLifecycle()
    val meetingMinutesTemplates by viewModel.savedMeetingMinutesTemplates.collectAsStateWithLifecycle()
    val curMeetingMinutesTemplateName by viewModel.curMeetingMinutesTemplateName.collectAsStateWithLifecycle()

    val businessLetterData by viewModel.activeBusinessLetter.collectAsStateWithLifecycle()
    val businessLetterTemplates by viewModel.savedBusinessLetterTemplates.collectAsStateWithLifecycle()
    val curBusinessLetterTemplateName by viewModel.curBusinessLetterTemplateName.collectAsStateWithLifecycle()

    val useSignatureState by viewModel.useSignature.collectAsStateWithLifecycle()
    val signatureTextState by viewModel.signatureText.collectAsStateWithLifecycle()
    val signatureStyleState by viewModel.signatureStyle.collectAsStateWithLifecycle()
    val signatureBitmapState by viewModel.signatureBitmapBase64.collectAsStateWithLifecycle()

    val useWatermarkState by viewModel.useWatermark.collectAsStateWithLifecycle()
    val watermarkTextState by viewModel.watermarkText.collectAsStateWithLifecycle()
    val watermarkImageUriState by viewModel.watermarkImageUri.collectAsStateWithLifecycle()

    val universalSizeState by viewModel.universalSizeIndex.collectAsStateWithLifecycle()
    val spacingMultiplier = when(universalSizeState) {
        0 -> 0.8f
        2 -> 1.3f
        else -> 1.0f
    }

    val styleConfig = DocumentStyleConfig(
        useWatermark = useWatermarkState,
        watermarkText = watermarkTextState,
        watermarkImageUri = watermarkImageUriState,
        useSignature = useSignatureState,
        signatureText = signatureTextState,
        signatureStyle = signatureStyleState,
        signatureBitmapBase64 = signatureBitmapState,
        spacingMultiplier = spacingMultiplier
    )

    // Required fields check helper
    fun validateRequiredFields(): Boolean {
        return when (selectedTab) {
            0 -> cvData.fullName.isNotBlank() && cvData.jobTitle.isNotBlank()
            1 -> coverLetterData.companyName.isNotBlank() && coverLetterData.jobTitle.isNotBlank() && coverLetterData.yourName.isNotBlank()
            2 -> emailData.recipientEmail.isNotBlank() && emailData.subjectLine.isNotBlank() && emailData.bodyMarkdown.isNotBlank()
            3 -> invoiceData.invoiceNumber.isNotBlank() && invoiceData.clientName.isNotBlank() && invoiceData.myBusinessName.isNotBlank()
            4 -> proposalData.title.isNotBlank() && proposalData.executiveSummary.isNotBlank()
            5 -> offerLetterData.candidateName.isNotBlank() && offerLetterData.jobTitle.isNotBlank() && offerLetterData.companyName.isNotBlank()
            6 -> resignationLetterData.employeeName.isNotBlank() && resignationLetterData.managerName.isNotBlank() && resignationLetterData.companyName.isNotBlank()
            7 -> serviceContractData.contractorName.isNotBlank() && serviceContractData.clientName.isNotBlank() && serviceContractData.agreementDate.isNotBlank()
            8 -> certificateData.recipientName.isNotBlank() && certificateData.achievementTitle.isNotBlank() && certificateData.dateOfIssue.isNotBlank()
            9 -> meetingMinutesData.meetingTitle.isNotBlank() && meetingMinutesData.facilitator.isNotBlank() && meetingMinutesData.meetingDate.isNotBlank()
            10 -> businessLetterData.senderAddress.isNotBlank() && businessLetterData.recipientAddress.isNotBlank() && businessLetterData.subject.isNotBlank()
            else -> true
        }
    }

    // High performance offline file exporter router
    fun triggerFileAction(format: String, rawActionType: String) { // format: "PDF", "TXT", "print", "share"
        val isCompleted = validateRequiredFields()
        val actionType = if (rawActionType == "email") {
            DocExporter.isEmailOverride = true
            "share"
        } else {
            DocExporter.isEmailOverride = false
            rawActionType
        }
        
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
                    5 -> { // Offer Letter
                        if (format == "TXT") {
                            val txt = DocExporter.generateOfferLetterTxt(offerLetterData)
                            val file = DocExporter.writeTextToFile(context, txt, "offer_letter.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateOfferLetterPdf(context, offerLetterData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                    6 -> { // Resignation Letter
                        if (format == "TXT") {
                            val txt = DocExporter.generateResignationLetterTxt(resignationLetterData)
                            val file = DocExporter.writeTextToFile(context, txt, "resignation_letter.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateResignationLetterPdf(context, resignationLetterData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                    7 -> { // Service Contract
                        if (format == "TXT") {
                            val txt = DocExporter.generateServiceContractTxt(serviceContractData)
                            val file = DocExporter.writeTextToFile(context, txt, "service_contract.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateServiceContractPdf(context, serviceContractData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                    8 -> { // Certificate
                        if (format == "TXT") {
                            val txt = DocExporter.generateCertificateTxt(certificateData)
                            val file = DocExporter.writeTextToFile(context, txt, "certificate_of_achievement.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateCertificatePdf(context, certificateData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                    9 -> { // Meeting Minutes
                        if (format == "TXT") {
                            val txt = DocExporter.generateMeetingMinutesTxt(meetingMinutesData)
                            val file = DocExporter.writeTextToFile(context, txt, "meeting_minutes.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateMeetingMinutesPdf(context, meetingMinutesData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                    10 -> { // Business Letter
                        if (format == "TXT") {
                            val txt = DocExporter.generateBusinessLetterTxt(businessLetterData)
                            val file = DocExporter.writeTextToFile(context, txt, "business_letter.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateBusinessLetterPdf(context, businessLetterData)
                            if (actionType == "print") DocExporter.printPdf(context, file)
                            else if (actionType == "share") DocExporter.shareFile(context, file, "application/pdf")
                            else Toast.makeText(context, "Saved PDF to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    }
                    else -> {
                        val json = viewModel.activeDynamicJsons.value[selectedTab] ?: ""
                        if (format == "TXT") {
                            val txt = DocExporter.generateDynamicTxt(selectedTab, json)
                            val file = DocExporter.writeTextToFile(context, txt, "dynamic_doc.txt")
                            if (actionType == "share") DocExporter.shareFile(context, file, "text/plain")
                            else Toast.makeText(context, "Saved TXT to cache: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            val file = DocExporter.generateDynamicPdf(context, selectedTab, json)
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
        "candidateName" -> "Step Prompt: Selected Candidate"
        "employeeName" -> "Step Prompt: Formal Employee Name"
        "managerName" -> "Step Prompt: Receiving Manager"
        "contractorName" -> "Step Prompt: Contractor Legal Entity"
        "recipientName" -> "Step Prompt: Recipient / Awardee"
        "meetingTitle" -> "Step Prompt: Meeting Objective"
        "senderAddress" -> "Step Prompt: Sender Physical Address"
        "recipientAddress" -> "Step Prompt: Recipient Mailing Address"
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
        "candidateName" -> "Enter the full name of the job seeker being selected for employment."
        "employeeName" -> "Your formal full name as registered in the company employee records database."
        "managerName" -> "The primary supervisor, department lead, or HR manager responsible for coordinating your contract status."
        "contractorName" -> "Your formal trade name, business registration number, or individual freelance developer prefix."
        "recipientName" -> "Specify the recipient or awardee. Ensure middle initials are correct for legal or decorative credentials."
        "meetingTitle" -> "Establish a clear meeting focus so review committees can instantly scan and register past discussions."
        "senderAddress" -> "A complete return address block including ZIP/Postal codes to make reply postage seamless."
        "recipientAddress" -> "Double check recipient building suites and office branch layout variables to avoid courier returns."
        else -> "Select or click any empty field in the editor below or tap a bracketed placeholder [Click to Fill] on the document preview to begin real-time wizard entry."
    }

    val isDarkByState by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val themeIndexByState by viewModel.selectedThemeIndex.collectAsStateWithLifecycle()

    val paperIndex by viewModel.selectedPaperIndex.collectAsStateWithLifecycle()
    val paperColors = listOf(
        Pair("Classic White", Color(0xFFFFFFFF)),
        Pair("Warm Cream", Color(0xFFFAF5EB)),
        Pair("Retro Manila", Color(0xFFFAF0D7)),
        Pair("Cool Blue Ash", Color(0xFFF2F4F7)),
        Pair("Sage Mint", Color(0xFFEDF6F0)),
        Pair("Romantic Rose", Color(0xFFFFF5F7)),
        Pair("Pencil Graphite", Color(0xFFF7F7F8))
    )
    val activePaperColor = paperColors.getOrElse(paperIndex) { Pair("Classic White", Color(0xFFFFFFFF)) }.second

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DashboardDrawerContent(
                viewModel = viewModel,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
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
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Open Settings Settings Menu", tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        actions = {
                            var showProfileDialog by remember { mutableStateOf(false) }
                            if (showProfileDialog) {
                                UserProfileDialog(viewModel) { showProfileDialog = false }
                            }
                            IconButton(onClick = { showProfileDialog = true }) {
                                Icon(Icons.Default.Person, contentDescription = "User Profile", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Palette, contentDescription = "Palette Customizer", tint = MaterialTheme.colorScheme.primary)
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
                            if (focusedField.value.isNotEmpty()) {
                                PromptBox(title = activePromptTitle, prompt = activePromptText)
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            // Render Selected Form
                            Box(modifier = Modifier.weight(1f)) {
                                if (selectedTab < 11) {
                                    RenderActiveForm(selectedTab, viewModel, cvData, coverLetterData, emailData, invoiceData, proposalData, offerLetterData, resignationLetterData, serviceContractData, certificateData, meetingMinutesData, businessLetterData, focusedField, highlightIfEmpty)
                                } else {
                                    RenderDynamicForm(selectedTab, viewModel, focusedField, highlightIfEmpty)
                                }
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
                                TextButton(onClick = { triggerFileAction("PDF", "email") }) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Send Email", color = MaterialTheme.colorScheme.onSurface)
                                }
                                TextButton(onClick = { triggerFileAction("TXT", "share") }) {
                                    Icon(Icons.Default.Save, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Export TXT", color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(modifier = Modifier.weight(1f)) {
                                CompositionLocalProvider(LocalDocumentStyleConfig provides styleConfig) {
                                if (selectedTab < 11) {
                                    RenderActivePreview(selectedTab, cvData, coverLetterData, emailData, invoiceData, proposalData, offerLetterData, resignationLetterData, serviceContractData, certificateData, meetingMinutesData, businessLetterData, activePaperColor) { clickedFieldName ->
                                        focusedField.value = clickedFieldName
                                    }
                                } else {
                                    RenderDynamicPreview(selectedTab, viewModel, activePaperColor)
                                }
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
                            if (focusedField.value.isNotEmpty()) {
                                PromptBox(title = activePromptTitle, prompt = activePromptText)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                if (selectedTab < 11) {
                                    RenderActiveForm(selectedTab, viewModel, cvData, coverLetterData, emailData, invoiceData, proposalData, offerLetterData, resignationLetterData, serviceContractData, certificateData, meetingMinutesData, businessLetterData, focusedField, highlightIfEmpty)
                                } else {
                                    RenderDynamicForm(selectedTab, viewModel, focusedField, highlightIfEmpty)
                                }
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
                                    Text("Share", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                                }
                                TextButton(onClick = { triggerFileAction("PDF", "print") }) {
                                    Icon(Icons.Default.Print, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text("Print", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                                }
                                TextButton(onClick = { triggerFileAction("PDF", "email") }) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text("Email", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                                }
                                TextButton(onClick = { triggerFileAction("TXT", "share") }) {
                                    Icon(Icons.Default.Save, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text("Export TXT", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                CompositionLocalProvider(LocalDocumentStyleConfig provides styleConfig) {
                                    if (selectedTab < 11) {
                                        RenderActivePreview(selectedTab, cvData, coverLetterData, emailData, invoiceData, proposalData, offerLetterData, resignationLetterData, serviceContractData, certificateData, meetingMinutesData, businessLetterData, activePaperColor) { clickedFieldName ->
                                            focusedField.value = clickedFieldName
                                            compactModePane = 0 // Switch back to form so they can type instantly!
                                        }
                                    } else {
                                        RenderDynamicPreview(selectedTab, viewModel, activePaperColor)
                                    }
                                }
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
    offerLetterData: OfferLetterData,
    resignationLetterData: ResignationLetterData,
    serviceContractData: ServiceContractData,
    certificateData: CertificateData,
    meetingMinutesData: MeetingMinutesData,
    businessLetterData: BusinessLetterData,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean
) {
    when (selectedTab) {
        0 -> CvForm(cvData, { viewModel.updateCv { _ -> it } }, focusedField, highlightIfEmpty)
        1 -> CoverLetterForm(coverLetterData, { viewModel.updateCoverLetter { _ -> it } }, focusedField, highlightIfEmpty)
        2 -> EmailForm(emailData, { viewModel.updateEmail { _ -> it } }, focusedField, highlightIfEmpty)
        3 -> InvoiceForm(invoiceData, { viewModel.updateInvoice { _ -> it } }, focusedField, highlightIfEmpty)
        4 -> ProposalForm(proposalData, { viewModel.updateProposal { _ -> it } }, focusedField, highlightIfEmpty)
        5 -> OfferLetterForm(offerLetterData, { viewModel.updateOfferLetter { _ -> it } }, focusedField, highlightIfEmpty)
        6 -> ResignationLetterForm(resignationLetterData, { viewModel.updateResignationLetter { _ -> it } }, focusedField, highlightIfEmpty)
        7 -> ServiceContractForm(serviceContractData, { viewModel.updateServiceContract { _ -> it } }, focusedField, highlightIfEmpty)
        8 -> CertificateForm(certificateData, { viewModel.updateCertificate { _ -> it } }, focusedField, highlightIfEmpty)
        9 -> MeetingMinutesForm(meetingMinutesData, { viewModel.updateMeetingMinutes { _ -> it } }, focusedField, highlightIfEmpty)
        10 -> BusinessLetterForm(businessLetterData, { viewModel.updateBusinessLetter { _ -> it } }, focusedField, highlightIfEmpty)
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
    offerLetterData: OfferLetterData,
    resignationLetterData: ResignationLetterData,
    serviceContractData: ServiceContractData,
    certificateData: CertificateData,
    meetingMinutesData: MeetingMinutesData,
    businessLetterData: BusinessLetterData,
    paperColor: Color,
    onNavigateToField: (String) -> Unit
) {
    when (selectedTab) {
        0 -> CvDocumentPreview(cvData, paperColor, onNavigateToField)
        1 -> CoverLetterDocumentPreview(coverLetterData, paperColor, onNavigateToField)
        2 -> EmailDocumentPreview(emailData, paperColor, onNavigateToField)
        3 -> InvoiceDocumentPreview(invoiceData, paperColor, onNavigateToField)
        4 -> ProposalDocumentPreview(proposalData, paperColor, onNavigateToField)
        5 -> OfferLetterDocumentPreview(offerLetterData, paperColor, onNavigateToField)
        6 -> ResignationLetterDocumentPreview(resignationLetterData, paperColor, onNavigateToField)
        7 -> ServiceContractDocumentPreview(serviceContractData, paperColor, onNavigateToField)
        8 -> CertificateDocumentPreview(certificateData, paperColor, onNavigateToField)
        9 -> MeetingMinutesDocumentPreview(meetingMinutesData, paperColor, onNavigateToField)
        10 -> BusinessLetterDocumentPreview(businessLetterData, paperColor, onNavigateToField)
    }
}

@Composable
fun UnifiedCustomizerHubCard(
    viewModel: DocumentViewModel,
    selectedTab: Int,
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
    var activeTab by remember { mutableIntStateOf(0) } // 0: Templates, 1: Theme Customizer
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Elegant M3 Sliding / Segmented Cross Toggle row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Templates tab selector
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (activeTab == 0) MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { activeTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = if (activeTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Templates Presets",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Themes tab selector
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (activeTab == 1) MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { activeTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = if (activeTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Theme Palette",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Smooth cross-fade transition on panel toggles
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally { width -> if (targetState > initialState) width else -width })
                        .togetherWith(fadeOut() + slideOutHorizontally { width -> if (targetState > initialState) -width else width })
                },
                label = "CustomizerPaneTransition"
            ) { targetPane ->
                if (targetPane == 0) {
                    // Document Template Options
                    val curDocTypeName = when (selectedTab) {
                        0 -> "CV"
                        1 -> "COVER_LETTER"
                        2 -> "EMAIL"
                        3 -> "INVOICE"
                        else -> "PROPOSAL"
                    }
                    val curTemplateName = when (selectedTab) {
                        0 -> curCvTemplateName
                        1 -> curCoverLetterTemplateName
                        2 -> curEmailTemplateName
                        3 -> curInvoiceTemplateName
                        else -> curProposalTemplateName
                    }
                    val currentTypeList = when (selectedTab) {
                        0 -> cvTemplates
                        1 -> coverLetterTemplates
                        2 -> emailTemplates
                        3 -> invoiceTemplates
                        else -> proposalTemplates
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "📁 Document Templates",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Active: ${curTemplateName ?: "Draft (Autosaved)"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                var dropdownExpanded by remember { mutableStateOf(false) }
                                var showSaveDialog by remember { mutableStateOf(false) }
                                var saveName by remember { mutableStateOf("") }

                                IconButton(onClick = { dropdownExpanded = true }) {
                                    Icon(
                                        Icons.Default.FolderOpen,
                                        contentDescription = "Load Presets",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                DropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("📄 Create New / Reset Blank", color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            viewModel.resetDocument(curDocTypeName)
                                            dropdownExpanded = false
                                        },
                                        leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                    )
                                    if (currentTypeList.isNotEmpty()) {
                                        HorizontalDivider()
                                        Text(
                                            text = " Saved (Offline)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                        currentTypeList.forEach { template ->
                                            DropdownMenuItem(
                                                text = { Text(template.name, color = MaterialTheme.colorScheme.onSurface) },
                                                onClick = {
                                                    viewModel.loadTemplate(template)
                                                    dropdownExpanded = false
                                                },
                                                trailingIcon = {
                                                    IconButton(onClick = { viewModel.deleteTemplate(template.id) }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            )
                                        }
                                    } else {
                                        HorizontalDivider()
                                        DropdownMenuItem(
                                            text = { Text("[No Saved Presets]", color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
                                    Text("Save Preset", style = MaterialTheme.typography.bodySmall)
                                }

                                if (showSaveDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showSaveDialog = false },
                                        title = { Text("Save Document Preset") },
                                        text = {
                                            Column {
                                                Text("Give this template a name (e.g. 'Software Engineer Resume'):")
                                                Spacer(modifier = Modifier.height(10.dp))
                                                OutlinedTextField(
                                                    value = saveName,
                                                    onValueChange = { saveName = it },
                                                    label = { Text("Preset Name") },
                                                    singleLine = true,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        },
                                        confirmButton = {
                                            TextButton(
                                                onClick = {
                                                    if (saveName.isNotBlank()) {
                                                        viewModel.saveAsTemplate(curDocTypeName, saveName) {}
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
                        }
                    }
                } else {
                    // Palette Customizer Options
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎨 Space Palette Customizer",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Light / Dark mode Switch
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isDark) "Dark Mode" else "Light Mode",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        Spacer(modifier = Modifier.height(8.dp))

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

@Composable
fun DashboardDrawerContent(
    viewModel: DocumentViewModel,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val selectedThemeIndex by viewModel.selectedThemeIndex.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val paperIndex by viewModel.selectedPaperIndex.collectAsStateWithLifecycle()

    val activeCv by viewModel.activeCv.collectAsStateWithLifecycle()

    val useSignatureState by viewModel.useSignature.collectAsStateWithLifecycle()
    val signatureTextState by viewModel.signatureText.collectAsStateWithLifecycle()
    val signatureStyleState by viewModel.signatureStyle.collectAsStateWithLifecycle()

    val useWatermarkState by viewModel.useWatermark.collectAsStateWithLifecycle()
    val watermarkTextState by viewModel.watermarkText.collectAsStateWithLifecycle()
    val watermarkImageUriState by viewModel.watermarkImageUri.collectAsStateWithLifecycle()

    var showDigitalSignatureDialog by remember { mutableStateOf(false) }

    val watermarkLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.setWatermarkImageUri(uri.toString())
        }
    }

    val universalSizeState by viewModel.universalSizeIndex.collectAsStateWithLifecycle()

    // Collect all template flows inside the drawer dynamically matching MVVM
    val cvTemplates by viewModel.savedCvTemplates.collectAsStateWithLifecycle()
    val curCvTemplateName by viewModel.curCvTemplateName.collectAsStateWithLifecycle()

    val coverLetterTemplates by viewModel.savedCoverLetterTemplates.collectAsStateWithLifecycle()
    val curCoverLetterTemplateName by viewModel.curCoverLetterTemplateName.collectAsStateWithLifecycle()

    val emailTemplates by viewModel.savedEmailTemplates.collectAsStateWithLifecycle()
    val curEmailTemplateName by viewModel.curEmailTemplateName.collectAsStateWithLifecycle()

    val invoiceTemplates by viewModel.savedInvoiceTemplates.collectAsStateWithLifecycle()
    val curInvoiceTemplateName by viewModel.curInvoiceTemplateName.collectAsStateWithLifecycle()

    val proposalTemplates by viewModel.savedProposalTemplates.collectAsStateWithLifecycle()
    val curProposalTemplateName by viewModel.curProposalTemplateName.collectAsStateWithLifecycle()

    val offerLetterTemplates by viewModel.savedOfferLetterTemplates.collectAsStateWithLifecycle()
    val curOfferLetterTemplateName by viewModel.curOfferLetterTemplateName.collectAsStateWithLifecycle()

    val resignationLetterTemplates by viewModel.savedResignationLetterTemplates.collectAsStateWithLifecycle()
    val curResignationLetterTemplateName by viewModel.curResignationLetterTemplateName.collectAsStateWithLifecycle()

    val serviceContractTemplates by viewModel.savedServiceContractTemplates.collectAsStateWithLifecycle()
    val curServiceContractTemplateName by viewModel.curServiceContractTemplateName.collectAsStateWithLifecycle()

    val certificateTemplates by viewModel.savedCertificateTemplates.collectAsStateWithLifecycle()
    val curCertificateTemplateName by viewModel.curCertificateTemplateName.collectAsStateWithLifecycle()

    val meetingMinutesTemplates by viewModel.savedMeetingMinutesTemplates.collectAsStateWithLifecycle()
    val curMeetingMinutesTemplateName by viewModel.curMeetingMinutesTemplateName.collectAsStateWithLifecycle()

    val businessLetterTemplates by viewModel.savedBusinessLetterTemplates.collectAsStateWithLifecycle()
    val curBusinessLetterTemplateName by viewModel.curBusinessLetterTemplateName.collectAsStateWithLifecycle()

    val context = androidx.compose.ui.platform.LocalContext.current

    val themes = listOf(
        Triple("Lavender Mint", "🌸", if (isDark) LavenderDarkPrimary else LavenderLightPrimary),
        Triple("Peach Cream", "🍑", if (isDark) PeachDarkPrimary else PeachLightPrimary),
        Triple("Cherry Blossom", "🍒", if (isDark) CherryDarkPrimary else CherryLightPrimary),
        Triple("Ocean Breeze", "🌊", if (isDark) OceanDarkPrimary else OceanLightPrimary),
        Triple("Sage Garden", "🌿", if (isDark) SageDarkPrimary else SageLightPrimary)
    )

    val paperColors = listOf(
        Triple("Classic White", "📄", Color(0xFFFFFFFF)),
        Triple("Warm Cream", "🌾", Color(0xFFFAF5EB)),
        Triple("Retro Manila", "📜", Color(0xFFFAF0D7)),
        Triple("Cool Blue Ash", "❄️", Color(0xFFF2F4F7)),
        Triple("Sage Mint", "🍃", Color(0xFFEDF6F0)),
        Triple("Romantic Rose", "🌸", Color(0xFFFFF5F7)),
        Triple("Pencil Graphite", "✏️", Color(0xFFF7F7F8))
    )

    ModalDrawerSheet(
        modifier = Modifier.width(310.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Elegant Workspace Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                     imageVector = Icons.Default.Dashboard,
                     contentDescription = null,
                     tint = MaterialTheme.colorScheme.primary,
                     modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Nexturn Workspace",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Clean Offline Document Suite",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // Developer Credit - Made with love by Rahul Shah
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text("✨", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Made with ❤️ by Rahul Shah",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // SECTION 1: Active Document Type Selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Select Document Type",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            val docTypes = listOf(
                Triple(0, "CV / Resume", Icons.Default.AccountBox),
                Triple(1, "Cover Letter", Icons.Default.Description),
                Triple(2, "Follow-up Email", Icons.Default.Mail),
                Triple(3, "Invoice", Icons.Default.Receipt),
                Triple(4, "Project Proposal", Icons.Default.Assessment),
                Triple(5, "Offer Letter (Job Offer)", Icons.Default.Work),
                Triple(6, "Resignation Letter", Icons.Default.ExitToApp),
                Triple(7, "Service Contract", Icons.Default.Edit),
                Triple(8, "Certificate of Completion", Icons.Default.Star),
                Triple(9, "Meeting Minutes", Icons.Default.List),
                Triple(10, "Business Letter", Icons.Default.Business),
                Triple(11, "Reference Letter", Icons.Default.Person),
                Triple(12, "Purchase Order", Icons.Default.ShoppingCart),
                Triple(13, "Quote / Estimate", Icons.Default.RequestQuote),
                Triple(14, "Non-Disclosure Agreement", Icons.Default.Security),
                Triple(15, "Timesheet", Icons.Default.AccessTime),
                Triple(16, "Expense Report", Icons.Default.AttachMoney),
                Triple(17, "Press Release", Icons.Default.Campaign),
                Triple(18, "Memo (Internal)", Icons.Default.Message),
                Triple(19, "Thank-You Letter", Icons.Default.ThumbUp),
                Triple(20, "Acceptance Letter", Icons.Default.TaskAlt),
                Triple(21, "Termination Letter", Icons.Default.Cancel),
                Triple(22, "Performance Review", Icons.Default.TrendingUp),
                Triple(23, "Custom Layout", Icons.Default.Build)
            )

            docTypes.forEach { (index, title, icon) ->
                val isSelected = selectedTab == index
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            onTabSelected(index)
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // SECTION 2: Document Template Presets
            val curDocTypeName = when (selectedTab) {
                0 -> "CV"
                1 -> "COVER_LETTER"
                2 -> "EMAIL"
                3 -> "INVOICE"
                4 -> "PROPOSAL"
                5 -> "OFFER_LETTER"
                6 -> "RESIGNATION_LETTER"
                7 -> "SERVICE_CONTRACT"
                8 -> "CERTIFICATE"
                9 -> "MEETING_MINUTES"
                10 -> "BUSINESS_LETTER"
                else -> "CV"
            }
            val curTemplateName = when (selectedTab) {
                0 -> curCvTemplateName
                1 -> curCoverLetterTemplateName
                2 -> curEmailTemplateName
                3 -> curInvoiceTemplateName
                4 -> curProposalTemplateName
                5 -> curOfferLetterTemplateName
                6 -> curResignationLetterTemplateName
                7 -> curServiceContractTemplateName
                8 -> curCertificateTemplateName
                9 -> curMeetingMinutesTemplateName
                10 -> curBusinessLetterTemplateName
                else -> null
            }
            val currentTypeList = when (selectedTab) {
                0 -> cvTemplates
                1 -> coverLetterTemplates
                2 -> emailTemplates
                3 -> invoiceTemplates
                4 -> proposalTemplates
                5 -> offerLetterTemplates
                6 -> resignationLetterTemplates
                7 -> serviceContractTemplates
                8 -> certificateTemplates
                9 -> meetingMinutesTemplates
                10 -> businessLetterTemplates
                else -> emptyList()
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Template Presets ($curDocTypeName)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Active: ${curTemplateName ?: "Draft (Autosaved)"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                var showSaveDialog by remember { mutableStateOf(false) }
                var saveName by remember { mutableStateOf("") }

                // Quick buttons: Reset & Save Preset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.resetDocument(curDocTypeName)
                            onCloseDrawer()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Blank", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Preset", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (showSaveDialog) {
                    AlertDialog(
                        onDismissRequest = { showSaveDialog = false },
                        title = { Text("Save Document Preset") },
                        text = {
                            Column {
                                Text("Give this template a name (e.g. 'Software Engineer Resume'):")
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = saveName,
                                    onValueChange = { saveName = it },
                                    label = { Text("Preset Name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    if (saveName.isNotBlank()) {
                                        viewModel.saveAsTemplate(curDocTypeName, saveName) {}
                                        showSaveDialog = false
                                        saveName = ""
                                        onCloseDrawer()
                                    }
                                }
                            ) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showSaveDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List of saved templates
                var searchQuery by remember { mutableStateOf("") }
                
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Saved $curDocTypeName Presets:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 6.dp).weight(1f)
                    )
                    if (currentTypeList.isNotEmpty()) {
                        IconButton(onClick = { DocExporter.batchExport(context, currentTypeList, "PDF") }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Archive, contentDescription = "Batch Export", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search & filter...", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.fillMaxWidth().height(48.dp).padding(bottom = 6.dp),
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )

                val filteredList = currentTypeList.filter { it.name.contains(searchQuery, ignoreCase = true) }

                if (filteredList.isEmpty()) {
                    Text(
                        text = "No saved presets yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                } else {
                    filteredList.forEach { template ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .background(
                                    if (curTemplateName == template.name) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (curTemplateName == template.name) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                    else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    viewModel.loadTemplate(template)
                                    onCloseDrawer()
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = template.name,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.deleteTemplate(template.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // SECTION 3: Space Theme Palette Customizer
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Theme Palette",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Theme Light/Dark switch
                    Switch(
                        checked = isDark,
                        onCheckedChange = { viewModel.toggleThemeMode() },
                        thumbContent = {
                            Icon(
                                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Vertical column list of themes for easy sidebar clicking
                themes.forEachIndexed { index, (name, emoji, color) ->
                    val isSelected = selectedThemeIndex == index
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .background(
                                if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) color.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.selectTheme(index) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // SECTION 4: Workspace Paper Backgrounds Customizer
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Paper Textures",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Applies real-time organic background hues",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Render paper background options
                paperColors.forEachIndexed { index, (name, emoji, color) ->
                    val isSelected = paperIndex == index
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .background(
                                if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.selectPaperIndex(index) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Colored box previewing actual color
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(color, RoundedCornerShape(4.dp))
                                .border(0.5.dp, Color.Gray, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Shifted CV Layout & Style Settings
            if (selectedTab == 0) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CV Layout & Style Settings",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Theme Style:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf("Modern", "Minimal", "Professional", "Creative").forEach { style ->
                        val isStyleSelected = activeCv.stylePreference == style
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.activeCv.value = activeCv.copy(stylePreference = style) }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = isStyleSelected,
                                onClick = { viewModel.activeCv.value = activeCv.copy(stylePreference = style) }
                            )
                            Text(style, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Column Layout:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf("Sidebar Layout", "Single Column").forEach { ly ->
                        val isLySelected = activeCv.layout == ly
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.activeCv.value = activeCv.copy(layout = ly) }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = isLySelected,
                                onClick = { viewModel.activeCv.value = activeCv.copy(layout = ly) }
                            )
                            Text(ly, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }

            // Universal Digital Signature
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Create, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Digital Signature Seal",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enable Signature", style = MaterialTheme.typography.bodySmall)
                    Switch(
                        checked = useSignatureState,
                        onCheckedChange = { viewModel.setUseSignature(it) }
                    )
                }
                if (useSignatureState) {
                    OutlinedTextField(
                        value = signatureTextState,
                        onValueChange = { viewModel.setSignatureText(it) },
                        label = { Text("Signatory Full Name") },
                        placeholder = { Text("e.g. Johnathan Doe") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Choose Style Choice:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Row {
                        listOf("Cursive", "Official Bold").forEach { style ->
                            val isStyleSelected = signatureStyleState == style
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setSignatureStyle(style) }
                            ) {
                                RadioButton(
                                    selected = isStyleSelected,
                                    onClick = { viewModel.setSignatureStyle(style) }
                                )
                                Text(style, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { showDigitalSignatureDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Create, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Draw Digital Signature", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                    Button(
                        onClick = {
                            viewModel.setSignatureText("Verified Seal " + SimpleDateFormat("yyMMdd-HH", Locale.getDefault()).format(Date()))
                            viewModel.setSignatureStyle("Official Bold")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import Verified Seal Stamp", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Organic Watermarks
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Organic Watermark Overlay",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enable Watermark overlay", style = MaterialTheme.typography.bodySmall)
                    Switch(
                        checked = useWatermarkState,
                        onCheckedChange = { viewModel.setUseWatermark(it) }
                    )
                }
                if (useWatermarkState) {
                    OutlinedTextField(
                        value = watermarkTextState,
                        onValueChange = { viewModel.setWatermarkText(it) },
                        label = { Text("Watermark Title Text") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Import Watermark Presets:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("DRAFT", "CONFIDENTIAL", "SAMPLE", "APPROVED").forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                    .clickable { viewModel.setWatermarkText(preset) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(preset, fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { watermarkLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import Custom Watermark", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                    if (watermarkImageUriState.isNotEmpty()) {
                        TextButton(onClick = { viewModel.setWatermarkImageUri("") }) {
                            Text("Clear Custom Image Watermark", style = MaterialTheme.typography.labelSmall, color = Color.Red)
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Universal Spacing
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Dashboard, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Universal Layout Spacing",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("Compact", "Balanced", "Spacious").forEachIndexed { index, label ->
                        val isSelected = universalSizeState == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.setUniversalSizeIndex(index) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onCloseDrawer() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Close Settings")
            }
        }
    }

    if (showDigitalSignatureDialog) {
        DigitalSignatureDialog(
            onDismiss = { showDigitalSignatureDialog = false },
            onSave = { base64 ->
                viewModel.setSignatureBitmapBase64(base64)
                showDigitalSignatureDialog = false
            }
        )
    }
}
