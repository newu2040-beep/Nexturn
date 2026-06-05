package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = DocumentRepository(database.documentDao())

    // Theme settings persistence state
    private val prefs = application.getSharedPreferences("nexturn_theme_prefs", android.content.Context.MODE_PRIVATE)
    val isDarkTheme = MutableStateFlow(prefs.getBoolean("is_dark_theme", false))
    val selectedThemeIndex = MutableStateFlow(prefs.getInt("selected_theme_index", 0)) // 0: Lavender, 1: Peach, 2: Cherry, 3: Ocean, 4: Sage

    fun toggleThemeMode() {
        val nextMode = !isDarkTheme.value
        isDarkTheme.value = nextMode
        prefs.edit().putBoolean("is_dark_theme", nextMode).apply()
    }

    fun selectTheme(index: Int) {
        selectedThemeIndex.value = index
        prefs.edit().putInt("selected_theme_index", index).apply()
    }

    // Active edit state for each document
    val activeCv = MutableStateFlow(CvData())
    val activeCoverLetter = MutableStateFlow(CoverLetterData())
    val activeEmail = MutableStateFlow(EmailData())
    val activeInvoice = MutableStateFlow(InvoiceData())
    val activeProposal = MutableStateFlow(ProposalData())

    // Currently loaded template name/id
    val curCvTemplateName = MutableStateFlow<String?>(null)
    val curCoverLetterTemplateName = MutableStateFlow<String?>(null)
    val curEmailTemplateName = MutableStateFlow<String?>(null)
    val curInvoiceTemplateName = MutableStateFlow<String?>(null)
    val curProposalTemplateName = MutableStateFlow<String?>(null)

    // Lists of saved templates from database
    val savedCvTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("CV")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedCoverLetterTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("COVER_LETTER")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedEmailTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("EMAIL")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedInvoiceTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("INVOICE")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedProposalTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("PROPOSAL")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Restore active drafts from Room on launching
        viewModelScope.launch {
            loadDrafts()
        }
    }

    private suspend fun loadDrafts() {
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "CV")?.let { doc ->
            activeCv.value = CvData.fromJson(doc.contentJson)
        }
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "COVER_LETTER")?.let { doc ->
            activeCoverLetter.value = CoverLetterData.fromJson(doc.contentJson)
        }
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "EMAIL")?.let { doc ->
            activeEmail.value = EmailData.fromJson(doc.contentJson)
        }
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "INVOICE")?.let { doc ->
            activeInvoice.value = InvoiceData.fromJson(doc.contentJson)
        }
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "PROPOSAL")?.let { doc ->
            activeProposal.value = ProposalData.fromJson(doc.contentJson)
        }
    }

    // Debounce state saving
    private var autosaveJobs = HashMap<String, Job>()

    private fun triggerAutosave(docType: String, contentJson: String) {
        autosaveJobs[docType]?.cancel()
        autosaveJobs[docType] = viewModelScope.launch {
            delay(800) // Debounce for 800ms
            repository.saveDocument(
                SavedDocument(
                    type = "ACTIVE_DRAFT",
                    name = docType,
                    contentJson = contentJson
                )
            )
        }
    }

    // General state updater methods
    fun updateCv(updater: (CvData) -> CvData) {
        val next = updater(activeCv.value)
        activeCv.value = next
        triggerAutosave("CV", next.toJson())
    }

    fun updateCoverLetter(updater: (CoverLetterData) -> CoverLetterData) {
        val next = updater(activeCoverLetter.value)
        activeCoverLetter.value = next
        triggerAutosave("COVER_LETTER", next.toJson())
    }

    fun updateEmail(updater: (EmailData) -> EmailData) {
        val next = updater(activeEmail.value)
        activeEmail.value = next
        triggerAutosave("EMAIL", next.toJson())
    }

    fun updateInvoice(updater: (InvoiceData) -> InvoiceData) {
        val next = updater(activeInvoice.value)
        activeInvoice.value = next
        triggerAutosave("INVOICE", next.toJson())
    }

    fun updateProposal(updater: (ProposalData) -> ProposalData) {
        val next = updater(activeProposal.value)
        activeProposal.value = next
        triggerAutosave("PROPOSAL", next.toJson())
    }

    // Reset current active document to clean state
    fun resetDocument(type: String) {
        viewModelScope.launch {
            when (type) {
                "CV" -> {
                    activeCv.value = CvData()
                    curCvTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "CV", contentJson = CvData().toJson()))
                }
                "COVER_LETTER" -> {
                    activeCoverLetter.value = CoverLetterData()
                    curCoverLetterTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "COVER_LETTER", contentJson = CoverLetterData().toJson()))
                }
                "EMAIL" -> {
                    activeEmail.value = EmailData()
                    curEmailTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "EMAIL", contentJson = EmailData().toJson()))
                }
                "INVOICE" -> {
                    activeInvoice.value = InvoiceData()
                    curInvoiceTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "INVOICE", contentJson = InvoiceData().toJson()))
                }
                "PROPOSAL" -> {
                    activeProposal.value = ProposalData()
                    curProposalTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "PROPOSAL", contentJson = ProposalData().toJson()))
                }
            }
        }
    }

    // Save current active edits under a stable, named user-defined template
    fun saveAsTemplate(type: String, templateName: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val contentJson = when (type) {
                "CV" -> {
                    curCvTemplateName.value = templateName
                    activeCv.value.toJson()
                }
                "COVER_LETTER" -> {
                    curCoverLetterTemplateName.value = templateName
                    activeCoverLetter.value.toJson()
                }
                "EMAIL" -> {
                    curEmailTemplateName.value = templateName
                    activeEmail.value.toJson()
                }
                "INVOICE" -> {
                    curInvoiceTemplateName.value = templateName
                    activeInvoice.value.toJson()
                }
                "PROPOSAL" -> {
                    curProposalTemplateName.value = templateName
                    activeProposal.value.toJson()
                }
                else -> ""
            }

            // Check if template of same type & name exists to replace it
            val existing = repository.getDocumentByTypeAndName(type, templateName)
            val newDoc = SavedDocument(
                id = existing?.id ?: 0,
                type = type,
                name = templateName,
                contentJson = contentJson
            )
            repository.saveDocument(newDoc)
            onComplete()
        }
    }

    // Load target saved template into the active sheet
    fun loadTemplate(templateDoc: SavedDocument) {
        viewModelScope.launch {
            when (templateDoc.type) {
                "CV" -> {
                    activeCv.value = CvData.fromJson(templateDoc.contentJson)
                    curCvTemplateName.value = templateDoc.name
                    triggerAutosave("CV", templateDoc.contentJson)
                }
                "COVER_LETTER" -> {
                    activeCoverLetter.value = CoverLetterData.fromJson(templateDoc.contentJson)
                    curCoverLetterTemplateName.value = templateDoc.name
                    triggerAutosave("COVER_LETTER", templateDoc.contentJson)
                }
                "EMAIL" -> {
                    activeEmail.value = EmailData.fromJson(templateDoc.contentJson)
                    curEmailTemplateName.value = templateDoc.name
                    triggerAutosave("EMAIL", templateDoc.contentJson)
                }
                "INVOICE" -> {
                    activeInvoice.value = InvoiceData.fromJson(templateDoc.contentJson)
                    curInvoiceTemplateName.value = templateDoc.name
                    triggerAutosave("INVOICE", templateDoc.contentJson)
                }
                "PROPOSAL" -> {
                    activeProposal.value = ProposalData.fromJson(templateDoc.contentJson)
                    curProposalTemplateName.value = templateDoc.name
                    triggerAutosave("PROPOSAL", templateDoc.contentJson)
                }
            }
        }
    }

    fun deleteTemplate(templateDocId: Long) {
        viewModelScope.launch {
            repository.deleteDocument(templateDocId)
        }
    }
}
