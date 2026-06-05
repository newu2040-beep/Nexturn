package com.example.data

import org.json.JSONArray
import org.json.JSONObject

data class CvData(
    val fullName: String = "",
    val jobTitle: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val portfolioUrl: String = "",
    val professionalSummary: String = "",
    val coreCompetencies: List<CompetencyItem> = emptyList(),
    val workExperiences: List<WorkItem> = emptyList(),
    val educations: List<EducationItem> = emptyList(),
    val certifications: List<String> = emptyList(),
    val languages: List<LanguageItem> = emptyList(),
    val technicalSkills: List<TechnicalSkillItem> = emptyList(),
    val portfolioProjects: List<ProjectItem> = emptyList(),
    val stylePreference: String = "Modern", // Modern, Minimal, Professional, Creative
    val layout: String = "Single Column", // Side-by-Side Sidebar, Single Column
    val headshotUri: String? = null
) {
    data class CompetencyItem(val name: String = "", val description: String = "")
    data class WorkItem(
        val jobTitle: String = "",
        val companyName: String = "",
        val dates: String = "",
        val location: String = "",
        val bullets: List<String> = emptyList()
    )
    data class EducationItem(
        val degree: String = "",
        val institution: String = "",
        val year: String = "",
        val gpa: String = ""
    )
    data class LanguageItem(val language: String = "", val proficiency: String = "")
    data class TechnicalSkillItem(val name: String = "", val proficiency: String = "")
    data class ProjectItem(val name: String = "", val description: String = "", val metric: String = "")

    fun toJson(): String {
        val json = JSONObject()
        json.put("fullName", fullName)
        json.put("jobTitle", jobTitle)
        json.put("email", email)
        json.put("phone", phone)
        json.put("location", location)
        json.put("portfolioUrl", portfolioUrl)
        json.put("professionalSummary", professionalSummary)
        
        val competenciesArray = JSONArray()
        coreCompetencies.forEach {
            val item = JSONObject()
            item.put("name", it.name)
            item.put("description", it.description)
            competenciesArray.put(item)
        }
        json.put("coreCompetencies", competenciesArray)

        val workArray = JSONArray()
        workExperiences.forEach {
            val item = JSONObject()
            item.put("jobTitle", it.jobTitle)
            item.put("companyName", it.companyName)
            item.put("dates", it.dates)
            item.put("location", it.location)
            val bulletsArray = JSONArray()
            it.bullets.forEach { b -> bulletsArray.put(b) }
            item.put("bullets", bulletsArray)
            workArray.put(item)
        }
        json.put("workExperiences", workArray)

        val eduArray = JSONArray()
        educations.forEach {
            val item = JSONObject()
            item.put("degree", it.degree)
            item.put("institution", it.institution)
            item.put("year", it.year)
            item.put("gpa", it.gpa)
            eduArray.put(item)
        }
        json.put("educations", eduArray)

        val certsArray = JSONArray()
        certifications.forEach { certsArray.put(it) }
        json.put("certifications", certsArray)

        val langArray = JSONArray()
        languages.forEach {
            val item = JSONObject()
            item.put("language", it.language)
            item.put("proficiency", it.proficiency)
            langArray.put(item)
        }
        json.put("languages", langArray)

        val techArray = JSONArray()
        technicalSkills.forEach {
            val item = JSONObject()
            item.put("name", it.name)
            item.put("proficiency", it.proficiency)
            techArray.put(item)
        }
        json.put("technicalSkills", techArray)

        val projArray = JSONArray()
        portfolioProjects.forEach {
            val item = JSONObject()
            item.put("name", it.name)
            item.put("description", it.description)
            item.put("metric", it.metric)
            projArray.put(item)
        }
        json.put("portfolioProjects", projArray)

        json.put("stylePreference", stylePreference)
        json.put("layout", layout)
        json.put("headshotUri", headshotUri ?: "")
        return json.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): CvData {
            if (jsonStr.isEmpty()) return CvData()
            val json = JSONObject(jsonStr)
            
            val competencies = mutableListOf<CompetencyItem>()
            val compArray = json.optJSONArray("coreCompetencies")
            if (compArray != null) {
                for (i in 0 until compArray.length()) {
                    val obj = compArray.optJSONObject(i) ?: continue
                    competencies.add(CompetencyItem(obj.optString("name", ""), obj.optString("description", "")))
                }
            }

            val work = mutableListOf<WorkItem>()
            val workArray = json.optJSONArray("workExperiences")
            if (workArray != null) {
                for (i in 0 until workArray.length()) {
                    val obj = workArray.optJSONObject(i) ?: continue
                    val bullets = mutableListOf<String>()
                    val bArr = obj.optJSONArray("bullets")
                    if (bArr != null) {
                        for (j in 0 until bArr.length()) {
                            bullets.add(bArr.optString(j, ""))
                        }
                    }
                    work.add(WorkItem(
                        obj.optString("jobTitle", ""),
                        obj.optString("companyName", ""),
                        obj.optString("dates", ""),
                        obj.optString("location", ""),
                        bullets
                    ))
                }
            }

            val edu = mutableListOf<EducationItem>()
            val eduArray = json.optJSONArray("educations")
            if (eduArray != null) {
                for (i in 0 until eduArray.length()) {
                    val obj = eduArray.optJSONObject(i) ?: continue
                    edu.add(EducationItem(
                        obj.optString("degree", ""),
                        obj.optString("institution", ""),
                        obj.optString("year", ""),
                        obj.optString("gpa", "")
                    ))
                }
            }

            val certs = mutableListOf<String>()
            val certsArray = json.optJSONArray("certifications")
            if (certsArray != null) {
                for (i in 0 until certsArray.length()) {
                    certs.add(certsArray.optString(i, ""))
                }
            }

            val lang = mutableListOf<LanguageItem>()
            val langArray = json.optJSONArray("languages")
            if (langArray != null) {
                for (i in 0 until langArray.length()) {
                    val obj = langArray.optJSONObject(i) ?: continue
                    lang.add(LanguageItem(obj.optString("language", ""), obj.optString("proficiency", "")))
                }
            }

            val tech = mutableListOf<TechnicalSkillItem>()
            val techArray = json.optJSONArray("technicalSkills")
            if (techArray != null) {
                for (i in 0 until techArray.length()) {
                    val obj = techArray.optJSONObject(i) ?: continue
                    tech.add(TechnicalSkillItem(obj.optString("name", ""), obj.optString("proficiency", "")))
                }
            }

            val proj = mutableListOf<ProjectItem>()
            val projArray = json.optJSONArray("portfolioProjects")
            if (projArray != null) {
                for (i in 0 until projArray.length()) {
                    val obj = projArray.optJSONObject(i) ?: continue
                    proj.add(ProjectItem(
                        obj.optString("name", ""),
                        obj.optString("description", ""),
                        obj.optString("metric", "")
                    ))
                }
            }

            return CvData(
                fullName = json.optString("fullName", ""),
                jobTitle = json.optString("jobTitle", ""),
                email = json.optString("email", ""),
                phone = json.optString("phone", ""),
                location = json.optString("location", ""),
                portfolioUrl = json.optString("portfolioUrl", ""),
                professionalSummary = json.optString("professionalSummary", ""),
                coreCompetencies = competencies,
                workExperiences = work,
                educations = edu,
                certifications = certs,
                languages = lang,
                technicalSkills = tech,
                portfolioProjects = proj,
                stylePreference = json.optString("stylePreference", "Modern"),
                layout = json.optString("layout", "Single Column"),
                headshotUri = json.optString("headshotUri", "").ifBlank { null }
            )
        }
    }
}

data class CoverLetterData(
    val date: String = "",
    val hiringManager: String = "",
    val companyName: String = "",
    val companyAddress: String = "",
    val jobTitle: String = "",
    val yourName: String = "",
    val yourContact: String = "",
    val paragraph1: String = "",
    val paragraph2: String = "",
    val paragraph3: String = ""
) {
    fun toJson(): String {
        return JSONObject().apply {
            put("date", date)
            put("hiringManager", hiringManager)
            put("companyName", companyName)
            put("companyAddress", companyAddress)
            put("jobTitle", jobTitle)
            put("yourName", yourName)
            put("yourContact", yourContact)
            put("paragraph1", paragraph1)
            put("paragraph2", paragraph2)
            put("paragraph3", paragraph3)
        }.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): CoverLetterData {
            if (jsonStr.isEmpty()) return CoverLetterData()
            val json = JSONObject(jsonStr)
            return CoverLetterData(
                date = json.optString("date", ""),
                hiringManager = json.optString("hiringManager", ""),
                companyName = json.optString("companyName", ""),
                companyAddress = json.optString("companyAddress", ""),
                jobTitle = json.optString("jobTitle", ""),
                yourName = json.optString("yourName", ""),
                yourContact = json.optString("yourContact", ""),
                paragraph1 = json.optString("paragraph1", ""),
                paragraph2 = json.optString("paragraph2", ""),
                paragraph3 = json.optString("paragraph3", "")
            )
        }
    }
}

data class EmailData(
    val recipientEmail: String = "",
    val subjectLine: String = "",
    val bodyMarkdown: String = "",
    val signatureName: String = "",
    val signatureTitle: String = "",
    val signaturePhone: String = "",
    val signatureWebsite: String = "",
    val signatureLinkedIn: String = ""
) {
    fun toJson(): String {
        return JSONObject().apply {
            put("recipientEmail", recipientEmail)
            put("subjectLine", subjectLine)
            put("bodyMarkdown", bodyMarkdown)
            put("signatureName", signatureName)
            put("signatureTitle", signatureTitle)
            put("signaturePhone", signaturePhone)
            put("signatureWebsite", signatureWebsite)
            put("signatureLinkedIn", signatureLinkedIn)
        }.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): EmailData {
            if (jsonStr.isEmpty()) return EmailData()
            val json = JSONObject(jsonStr)
            return EmailData(
                recipientEmail = json.optString("recipientEmail", ""),
                subjectLine = json.optString("subjectLine", ""),
                bodyMarkdown = json.optString("bodyMarkdown", ""),
                signatureName = json.optString("signatureName", ""),
                signatureTitle = json.optString("signatureTitle", ""),
                signaturePhone = json.optString("signaturePhone", ""),
                signatureWebsite = json.optString("signatureWebsite", ""),
                signatureLinkedIn = json.optString("signatureLinkedIn", "")
            )
        }
    }
}

data class InvoiceData(
    val invoiceNumber: String = "",
    val invoiceDate: String = "",
    val dueDate: String = "",
    val clientName: String = "",
    val clientCompany: String = "",
    val clientAddress: String = "",
    val myBusinessName: String = "",
    val myAddress: String = "",
    val myTaxId: String = "",
    val lineItems: List<InvoiceItem> = emptyList(),
    val taxRate: Float = 0f,
    val discount: Float = 0f,
    val paymentInstructions: String = "",
    val notesTerms: String = ""
) {
    data class InvoiceItem(
        val description: String = "",
        val quantity: Float = 1f,
        val unitPrice: Float = 0f
    )

    fun toJson(): String {
        val json = JSONObject()
        json.put("invoiceNumber", invoiceNumber)
        json.put("invoiceDate", invoiceDate)
        json.put("dueDate", dueDate)
        json.put("clientName", clientName)
        json.put("clientCompany", clientCompany)
        json.put("clientAddress", clientAddress)
        json.put("myBusinessName", myBusinessName)
        json.put("myAddress", myAddress)
        json.put("myTaxId", myTaxId)
        json.put("taxRate", taxRate.toDouble())
        json.put("discount", discount.toDouble())
        json.put("paymentInstructions", paymentInstructions)
        json.put("notesTerms", notesTerms)

        val itemsArray = JSONArray()
        lineItems.forEach {
            val item = JSONObject()
            item.put("description", it.description)
            item.put("quantity", it.quantity.toDouble())
            item.put("unitPrice", it.unitPrice.toDouble())
            itemsArray.put(item)
        }
        json.put("lineItems", itemsArray)
        return json.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): InvoiceData {
            if (jsonStr.isEmpty()) return InvoiceData()
            val json = JSONObject(jsonStr)
            
            val items = mutableListOf<InvoiceItem>()
            val itemsArray = json.optJSONArray("lineItems")
            if (itemsArray != null) {
                for (i in 0 until itemsArray.length()) {
                    val obj = itemsArray.optJSONObject(i) ?: continue
                    items.add(InvoiceItem(
                        obj.optString("description", ""),
                        obj.optDouble("quantity", 1.0).toFloat(),
                        obj.optDouble("unitPrice", 0.0).toFloat()
                    ))
                }
            }

            return InvoiceData(
                invoiceNumber = json.optString("invoiceNumber", ""),
                invoiceDate = json.optString("invoiceDate", ""),
                dueDate = json.optString("dueDate", ""),
                clientName = json.optString("clientName", ""),
                clientCompany = json.optString("clientCompany", ""),
                clientAddress = json.optString("clientAddress", ""),
                myBusinessName = json.optString("myBusinessName", ""),
                myAddress = json.optString("myAddress", ""),
                myTaxId = json.optString("myTaxId", ""),
                lineItems = items,
                taxRate = json.optDouble("taxRate", 0.0).toFloat(),
                discount = json.optDouble("discount", 0.0).toFloat(),
                paymentInstructions = json.optString("paymentInstructions", ""),
                notesTerms = json.optString("notesTerms", "")
            )
        }
    }
}

data class ProposalData(
    val title: String = "",
    val executiveSummary: String = "",
    val scope: List<String> = emptyList(),
    val deliverables: List<DeliverableItem> = emptyList(),
    val timeline: List<TimelineItem> = emptyList(),
    val investmentDetails: String = "",
    val whyMe: String = "",
    val nextSteps: List<String> = emptyList()
) {
    data class DeliverableItem(val title: String = "", val isChecked: Boolean = false)
    data class TimelineItem(val phase: String = "", val description: String = "")

    fun toJson(): String {
        val json = JSONObject()
        json.put("title", title)
        json.put("executiveSummary", executiveSummary)
        json.put("investmentDetails", investmentDetails)
        json.put("whyMe", whyMe)

        val scopeArray = JSONArray()
        scope.forEach { scopeArray.put(it) }
        json.put("scope", scopeArray)

        val delArray = JSONArray()
        deliverables.forEach {
            val item = JSONObject()
            item.put("title", it.title)
            item.put("isChecked", it.isChecked)
            delArray.put(item)
        }
        json.put("deliverables", delArray)

        val timeArray = JSONArray()
        timeline.forEach {
            val item = JSONObject()
            item.put("phase", it.phase)
            item.put("description", it.description)
            timeArray.put(item)
        }
        json.put("timeline", timeArray)

        val nextArray = JSONArray()
        nextSteps.forEach { nextArray.put(it) }
        json.put("nextSteps", nextArray)

        return json.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): ProposalData {
            if (jsonStr.isEmpty()) return ProposalData()
            val json = JSONObject(jsonStr)

            val scope = mutableListOf<String>()
            val scopeArray = json.optJSONArray("scope")
            if (scopeArray != null) {
                for (i in 0 until scopeArray.length()) {
                    scope.add(scopeArray.optString(i, ""))
                }
            }

            val deliverables = mutableListOf<DeliverableItem>()
            val delArray = json.optJSONArray("deliverables")
            if (delArray != null) {
                for (i in 0 until delArray.length()) {
                    val obj = delArray.optJSONObject(i) ?: continue
                    deliverables.add(DeliverableItem(
                        obj.optString("title", ""),
                        obj.optBoolean("isChecked", false)
                    ))
                }
            }

            val timeline = mutableListOf<TimelineItem>()
            val timeArray = json.optJSONArray("timeline")
            if (timeArray != null) {
                for (i in 0 until timeArray.length()) {
                    val obj = timeArray.optJSONObject(i) ?: continue
                    timeline.add(TimelineItem(
                        obj.optString("phase", ""),
                        obj.optString("description", "")
                    ))
                }
            }

            val nextSteps = mutableListOf<String>()
            val nextArray = json.optJSONArray("nextSteps")
            if (nextArray != null) {
                for (i in 0 until nextArray.length()) {
                    nextSteps.add(nextArray.optString(i, ""))
                }
            }

            return ProposalData(
                title = json.optString("title", ""),
                executiveSummary = json.optString("executiveSummary", ""),
                investmentDetails = json.optString("investmentDetails", ""),
                whyMe = json.optString("whyMe", ""),
                scope = scope,
                deliverables = deliverables,
                timeline = timeline,
                nextSteps = nextSteps
            )
        }
    }
}
