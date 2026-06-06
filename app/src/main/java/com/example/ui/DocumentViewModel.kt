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
    val selectedPaperIndex = MutableStateFlow(prefs.getInt("selected_paper_index", 0)) // 0: White, 1: Cream, 2: Manila, 3: Ash, 4: Mint, 5: Rose

    val useSignature = MutableStateFlow(prefs.getBoolean("use_signature", false))
    val signatureText = MutableStateFlow(prefs.getString("signature_text", "") ?: "")
    val signatureStyle = MutableStateFlow(prefs.getString("signature_style", "Cursive") ?: "Cursive")
    val signaturePaths = MutableStateFlow(prefs.getString("signature_paths_json", "") ?: "")
    val signatureBitmapBase64 = MutableStateFlow(prefs.getString("signature_bitmap_base64", "") ?: "")
    val watermarkImageUri = MutableStateFlow(prefs.getString("watermark_image_uri", "") ?: "")

    val useWatermark = MutableStateFlow(prefs.getBoolean("use_watermark", false))
    val watermarkText = MutableStateFlow(prefs.getString("watermark_text", "CONFIDENTIAL") ?: "CONFIDENTIAL")

    val universalSizeIndex = MutableStateFlow(prefs.getInt("universal_size_index", 1)) // 0: Compact, 1: Balanced, 2: Spacious

    fun toggleThemeMode() {
        val nextMode = !isDarkTheme.value
        isDarkTheme.value = nextMode
        prefs.edit().putBoolean("is_dark_theme", nextMode).apply()
    }

    fun selectTheme(index: Int) {
        selectedThemeIndex.value = index
        prefs.edit().putInt("selected_theme_index", index).apply()
    }

    fun selectPaperIndex(index: Int) {
        selectedPaperIndex.value = index
        prefs.edit().putInt("selected_paper_index", index).apply()
    }

    fun setUseSignature(value: Boolean) {
        useSignature.value = value
        prefs.edit().putBoolean("use_signature", value).apply()
    }

    fun setSignatureText(value: String) {
        signatureText.value = value
        prefs.edit().putString("signature_text", value).apply()
    }

    fun setSignatureStyle(value: String) {
        signatureStyle.value = value
        prefs.edit().putString("signature_style", value).apply()
    }

    fun setSignatureBitmapBase64(value: String) {
        signatureBitmapBase64.value = value
        prefs.edit().putString("signature_bitmap_base64", value).apply()
    }

    fun setWatermarkImageUri(value: String) {
        watermarkImageUri.value = value
        prefs.edit().putString("watermark_image_uri", value).apply()
    }

    fun setSignaturePaths(value: String) {
        signaturePaths.value = value
        prefs.edit().putString("signature_paths_json", value).apply()
    }

    fun setUseWatermark(value: Boolean) {
        useWatermark.value = value
        prefs.edit().putBoolean("use_watermark", value).apply()
    }

    fun setWatermarkText(value: String) {
        watermarkText.value = value
        prefs.edit().putString("watermark_text", value).apply()
    }

    fun setUniversalSizeIndex(value: Int) {
        universalSizeIndex.value = value
        prefs.edit().putInt("universal_size_index", value).apply()
    }

    // User Profile persistent auto-save state
    val userProfile = MutableStateFlow(UserProfileData.fromJson(prefs.getString("user_profile_json", "") ?: ""))

    fun saveUserProfile(profile: UserProfileData) {
        userProfile.value = profile
        prefs.edit().putString("user_profile_json", profile.toJson()).apply()
    }

    // Active edit state for each document
    val activeCv = MutableStateFlow(CvData())
    val activeCoverLetter = MutableStateFlow(CoverLetterData())
    val activeEmail = MutableStateFlow(EmailData())
    val activeInvoice = MutableStateFlow(InvoiceData())
    val activeProposal = MutableStateFlow(ProposalData())
    val activeOfferLetter = MutableStateFlow(OfferLetterData())
    val activeResignationLetter = MutableStateFlow(ResignationLetterData())
    val activeServiceContract = MutableStateFlow(ServiceContractData())
    val activeCertificate = MutableStateFlow(CertificateData())
    val activeMeetingMinutes = MutableStateFlow(MeetingMinutesData())
    val activeBusinessLetter = MutableStateFlow(BusinessLetterData())

    // Map of active custom templates (11 to 23)
    val activeCustomDocs = MutableStateFlow<Map<Int, CustomDocumentData>>(emptyMap())

    // Currently loaded template name/id
    val curCvTemplateName = MutableStateFlow<String?>(null)
    val curCoverLetterTemplateName = MutableStateFlow<String?>(null)
    val curEmailTemplateName = MutableStateFlow<String?>(null)
    val curInvoiceTemplateName = MutableStateFlow<String?>(null)
    val curProposalTemplateName = MutableStateFlow<String?>(null)
    val curOfferLetterTemplateName = MutableStateFlow<String?>(null)
    val curResignationLetterTemplateName = MutableStateFlow<String?>(null)
    val curServiceContractTemplateName = MutableStateFlow<String?>(null)
    val curCertificateTemplateName = MutableStateFlow<String?>(null)
    val curMeetingMinutesTemplateName = MutableStateFlow<String?>(null)
    val curBusinessLetterTemplateName = MutableStateFlow<String?>(null)

    // Map of currently loaded custom template names (11 to 23)
    val curCustomTemplateNames = MutableStateFlow<Map<Int, String?>>(emptyMap())

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

    val savedOfferLetterTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("OFFER_LETTER")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedResignationLetterTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("RESIGNATION_LETTER")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedServiceContractTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("SERVICE_CONTRACT")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedCertificateTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("CERTIFICATE")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedMeetingMinutesTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("MEETING_MINUTES")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedBusinessLetterTemplates: StateFlow<List<SavedDocument>> = repository.getTemplatesByType("BUSINESS_LETTER")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 11: Reference Letter, 12: PO, 13: Quote, 14: NDA, 15: Timesheet, 16: Expense, 17: Press Release
    // 18: Memo, 19: Thank You, 20: Acceptance, 21: Termination, 22: Performance, 23: Custom
    val activeDynamicJsons = MutableStateFlow<Map<Int, String>>(emptyMap())
    val curDynamicTemplateNames = MutableStateFlow<Map<Int, String?>>(emptyMap())

    val allSavedDocuments: StateFlow<List<SavedDocument>> = repository.getAllDocuments()
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
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "OFFER_LETTER")?.let { doc ->
            activeOfferLetter.value = OfferLetterData.fromJson(doc.contentJson)
        }
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "RESIGNATION_LETTER")?.let { doc ->
            activeResignationLetter.value = ResignationLetterData.fromJson(doc.contentJson)
        }
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "SERVICE_CONTRACT")?.let { doc ->
            activeServiceContract.value = ServiceContractData.fromJson(doc.contentJson)
        }
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "CERTIFICATE")?.let { doc ->
            activeCertificate.value = CertificateData.fromJson(doc.contentJson)
        }
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "MEETING_MINUTES")?.let { doc ->
            activeMeetingMinutes.value = MeetingMinutesData.fromJson(doc.contentJson)
        }
        repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "BUSINESS_LETTER")?.let { doc ->
            activeBusinessLetter.value = BusinessLetterData.fromJson(doc.contentJson)
        }
        for (index in 11..23) {
            repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "DYNAMIC_$index")?.let { doc ->
                activeDynamicJsons.value = activeDynamicJsons.value.toMutableMap().apply {
                    put(index, doc.contentJson)
                }
            }
        }
        for (index in 11..23) {
            repository.getDocumentByTypeAndName("ACTIVE_DRAFT", "CUSTOM_$index")?.let { doc ->
                activeCustomDocs.value = activeCustomDocs.value.toMutableMap().apply {
                    put(index, CustomDocumentData.fromJson(doc.contentJson))
                }
            }
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

    fun updateDynamicJson(index: Int, rawJson: String) {
        activeDynamicJsons.value = activeDynamicJsons.value.toMutableMap().apply {
            put(index, rawJson)
        }
        triggerAutosave("DYNAMIC_$index", rawJson)
    }

    fun updateCustomDoc(index: Int, updater: (CustomDocumentData) -> CustomDocumentData) {
        val current = activeCustomDocs.value[index] ?: CustomDocumentData(templateId = index)
        val next = updater(current)
        activeCustomDocs.value = activeCustomDocs.value.toMutableMap().apply {
            put(index, next)
        }
        triggerAutosave("CUSTOM_$index", next.toJson())
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

    fun updateOfferLetter(updater: (OfferLetterData) -> OfferLetterData) {
        val next = updater(activeOfferLetter.value)
        activeOfferLetter.value = next
        triggerAutosave("OFFER_LETTER", next.toJson())
    }

    fun updateResignationLetter(updater: (ResignationLetterData) -> ResignationLetterData) {
        val next = updater(activeResignationLetter.value)
        activeResignationLetter.value = next
        triggerAutosave("RESIGNATION_LETTER", next.toJson())
    }

    fun updateServiceContract(updater: (ServiceContractData) -> ServiceContractData) {
        val next = updater(activeServiceContract.value)
        activeServiceContract.value = next
        triggerAutosave("SERVICE_CONTRACT", next.toJson())
    }

    fun updateCertificate(updater: (CertificateData) -> CertificateData) {
        val next = updater(activeCertificate.value)
        activeCertificate.value = next
        triggerAutosave("CERTIFICATE", next.toJson())
    }

    fun updateMeetingMinutes(updater: (MeetingMinutesData) -> MeetingMinutesData) {
        val next = updater(activeMeetingMinutes.value)
        activeMeetingMinutes.value = next
        triggerAutosave("MEETING_MINUTES", next.toJson())
    }

    fun updateBusinessLetter(updater: (BusinessLetterData) -> BusinessLetterData) {
        val next = updater(activeBusinessLetter.value)
        activeBusinessLetter.value = next
        triggerAutosave("BUSINESS_LETTER", next.toJson())
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
                "OFFER_LETTER" -> {
                    activeOfferLetter.value = OfferLetterData()
                    curOfferLetterTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "OFFER_LETTER", contentJson = OfferLetterData().toJson()))
                }
                "RESIGNATION_LETTER" -> {
                    activeResignationLetter.value = ResignationLetterData()
                    curResignationLetterTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "RESIGNATION_LETTER", contentJson = ResignationLetterData().toJson()))
                }
                "SERVICE_CONTRACT" -> {
                    activeServiceContract.value = ServiceContractData()
                    curServiceContractTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "SERVICE_CONTRACT", contentJson = ServiceContractData().toJson()))
                }
                "CERTIFICATE" -> {
                    activeCertificate.value = CertificateData()
                    curCertificateTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "CERTIFICATE", contentJson = CertificateData().toJson()))
                }
                "MEETING_MINUTES" -> {
                    activeMeetingMinutes.value = MeetingMinutesData()
                    curMeetingMinutesTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "MEETING_MINUTES", contentJson = MeetingMinutesData().toJson()))
                }
                "BUSINESS_LETTER" -> {
                    activeBusinessLetter.value = BusinessLetterData()
                    curBusinessLetterTemplateName.value = null
                    repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "BUSINESS_LETTER", contentJson = BusinessLetterData().toJson()))
                }
                else -> {
                    if (type.startsWith("DYNAMIC_")) {
                        val idx = type.substringAfter("DYNAMIC_").toIntOrNull() ?: 11
                        activeDynamicJsons.value = activeDynamicJsons.value.toMutableMap().apply {
                            put(idx, "")
                        }
                        curDynamicTemplateNames.value = curDynamicTemplateNames.value.toMutableMap().apply {
                            put(idx, null)
                        }
                        repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "DYNAMIC_$idx", contentJson = ""))
                    } else if (type.startsWith("CUSTOM_")) {
                        val idx = type.substringAfter("CUSTOM_").toIntOrNull() ?: 11
                        activeCustomDocs.value = activeCustomDocs.value.toMutableMap().apply {
                            put(idx, CustomDocumentData(templateId = idx))
                        }
                        curCustomTemplateNames.value = curCustomTemplateNames.value.toMutableMap().apply {
                            put(idx, null)
                        }
                        repository.saveDocument(SavedDocument(type = "ACTIVE_DRAFT", name = "CUSTOM_$idx", contentJson = CustomDocumentData(templateId = idx).toJson()))
                    }
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
                "OFFER_LETTER" -> {
                    curOfferLetterTemplateName.value = templateName
                    activeOfferLetter.value.toJson()
                }
                "RESIGNATION_LETTER" -> {
                    curResignationLetterTemplateName.value = templateName
                    activeResignationLetter.value.toJson()
                }
                "SERVICE_CONTRACT" -> {
                    curServiceContractTemplateName.value = templateName
                    activeServiceContract.value.toJson()
                }
                "CERTIFICATE" -> {
                    curCertificateTemplateName.value = templateName
                    activeCertificate.value.toJson()
                }
                "MEETING_MINUTES" -> {
                    curMeetingMinutesTemplateName.value = templateName
                    activeMeetingMinutes.value.toJson()
                }
                "BUSINESS_LETTER" -> {
                    curBusinessLetterTemplateName.value = templateName
                    activeBusinessLetter.value.toJson()
                }
                else -> {
                    if (type.startsWith("DYNAMIC_")) {
                        val idx = type.substringAfter("DYNAMIC_").toIntOrNull() ?: 11
                        curDynamicTemplateNames.value = curDynamicTemplateNames.value.toMutableMap().apply {
                            put(idx, templateName)
                        }
                        activeDynamicJsons.value[idx] ?: ""
                    } else if (type.startsWith("CUSTOM_")) {
                        val idx = type.substringAfter("CUSTOM_").toIntOrNull() ?: 11
                        curCustomTemplateNames.value = curCustomTemplateNames.value.toMutableMap().apply {
                            put(idx, templateName)
                        }
                        activeCustomDocs.value[idx]?.toJson() ?: ""
                    } else ""
                }
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
                "OFFER_LETTER" -> {
                    activeOfferLetter.value = OfferLetterData.fromJson(templateDoc.contentJson)
                    curOfferLetterTemplateName.value = templateDoc.name
                    triggerAutosave("OFFER_LETTER", templateDoc.contentJson)
                }
                "RESIGNATION_LETTER" -> {
                    activeResignationLetter.value = ResignationLetterData.fromJson(templateDoc.contentJson)
                    curResignationLetterTemplateName.value = templateDoc.name
                    triggerAutosave("RESIGNATION_LETTER", templateDoc.contentJson)
                }
                "SERVICE_CONTRACT" -> {
                    activeServiceContract.value = ServiceContractData.fromJson(templateDoc.contentJson)
                    curServiceContractTemplateName.value = templateDoc.name
                    triggerAutosave("SERVICE_CONTRACT", templateDoc.contentJson)
                }
                "CERTIFICATE" -> {
                    activeCertificate.value = CertificateData.fromJson(templateDoc.contentJson)
                    curCertificateTemplateName.value = templateDoc.name
                    triggerAutosave("CERTIFICATE", templateDoc.contentJson)
                }
                "MEETING_MINUTES" -> {
                    activeMeetingMinutes.value = MeetingMinutesData.fromJson(templateDoc.contentJson)
                    curMeetingMinutesTemplateName.value = templateDoc.name
                    triggerAutosave("MEETING_MINUTES", templateDoc.contentJson)
                }
                "BUSINESS_LETTER" -> {
                    activeBusinessLetter.value = BusinessLetterData.fromJson(templateDoc.contentJson)
                    curBusinessLetterTemplateName.value = templateDoc.name
                    triggerAutosave("BUSINESS_LETTER", templateDoc.contentJson)
                }
                else -> {
                    if (templateDoc.type.startsWith("DYNAMIC_")) {
                        val idx = templateDoc.type.substringAfter("DYNAMIC_").toIntOrNull() ?: 11
                        activeDynamicJsons.value = activeDynamicJsons.value.toMutableMap().apply {
                            put(idx, templateDoc.contentJson)
                        }
                        curDynamicTemplateNames.value = curDynamicTemplateNames.value.toMutableMap().apply {
                            put(idx, templateDoc.name)
                        }
                        triggerAutosave("DYNAMIC_$idx", templateDoc.contentJson)
                    } else if (templateDoc.type.startsWith("CUSTOM_")) {
                        val idx = templateDoc.type.substringAfter("CUSTOM_").toIntOrNull() ?: 11
                        activeCustomDocs.value = activeCustomDocs.value.toMutableMap().apply {
                            put(idx, CustomDocumentData.fromJson(templateDoc.contentJson))
                        }
                        curCustomTemplateNames.value = curCustomTemplateNames.value.toMutableMap().apply {
                            put(idx, templateDoc.name)
                        }
                        triggerAutosave("CUSTOM_$idx", templateDoc.contentJson)
                    }
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
