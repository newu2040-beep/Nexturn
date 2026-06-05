package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import com.example.data.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DocExporter {

    // Helper: word-wrap drawer for PDF Canvas
    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        width: Float,
        paint: Paint,
        lineSpacingMultiplier: Float = 1.3f
    ): Float {
        if (text.isBlank()) return startY
        var currentY = startY
        val words = text.split(Regex("\\s+"))
        var line = StringBuilder()
        
        for (word in words) {
            val testLine = if (line.isEmpty()) word else "${line} $word"
            val testWidth = paint.measureText(testLine)
            if (testWidth > width) {
                canvas.drawText(line.toString(), x, currentY, paint)
                currentY += paint.textSize * lineSpacingMultiplier
                line = StringBuilder(word)
            } else {
                line.append(if (line.isEmpty()) word else " $word")
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line.toString(), x, currentY, paint)
            currentY += paint.textSize * lineSpacingMultiplier
        }
        return currentY
    }

    // ==========================================
    // 1. CV EXPORTERS
    // ==========================================
    fun generateCvTxt(data: CvData): String {
        val sb = StringBuilder()
        sb.append("==================================================\n")
        sb.append("                 CURRICULUM VITAE                 \n")
        sb.append("==================================================\n\n")
        sb.append("NAME: ${data.fullName.ifBlank { "[Your Full Name]" }}\n")
        if (data.jobTitle.isNotBlank()) sb.append("TITLE: ${data.jobTitle}\n")
        if (data.email.isNotBlank()) sb.append("EMAIL: ${data.email}\n")
        if (data.phone.isNotBlank()) sb.append("PHONE: ${data.phone}\n")
        if (data.location.isNotBlank()) sb.append("LOCATION: ${data.location}\n")
        if (data.portfolioUrl.isNotBlank()) sb.append("PORTFOLIO: ${data.portfolioUrl}\n")
        sb.append("\n--------------------------------------------------\n")
        sb.append("PROFESSIONAL SUMMARY\n")
        sb.append("--------------------------------------------------\n")
        sb.append(data.professionalSummary.ifBlank { "[Summary Empty]" }).append("\n\n")

        if (data.coreCompetencies.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("CORE COMPETENCIES\n")
            sb.append("--------------------------------------------------\n")
            data.coreCompetencies.forEach {
                sb.append("• ${it.name}: ${it.description}\n")
            }
            sb.append("\n")
        }

        if (data.workExperiences.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("WORK EXPERIENCE\n")
            sb.append("--------------------------------------------------\n")
            data.workExperiences.forEach {
                sb.append("• ${it.jobTitle} at ${it.companyName} (${it.dates})\n")
                if (it.location.isNotBlank()) sb.append("  Location: ${it.location}\n")
                it.bullets.forEach { b -> if (b.isNotBlank()) sb.append("    - $b\n") }
                sb.append("\n")
            }
        }

        if (data.educations.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("EDUCATION\n")
            sb.append("--------------------------------------------------\n")
            data.educations.forEach {
                sb.append("• ${it.degree} - ${it.institution} (${it.year})")
                if (it.gpa.isNotBlank()) sb.append(" (GPA: ${it.gpa})")
                sb.append("\n")
            }
            sb.append("\n")
        }

        if (data.certifications.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("CERTIFICATIONS & AWARDS\n")
            sb.append("--------------------------------------------------\n")
            data.certifications.forEach { sb.append("• $it\n") }
            sb.append("\n")
        }

        if (data.languages.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("LANGUAGES\n")
            sb.append("--------------------------------------------------\n")
            data.languages.forEach { sb.append("• ${it.language} (${it.proficiency})\n") }
            sb.append("\n")
        }

        if (data.technicalSkills.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("TECHNICAL SKILLS\n")
            sb.append("--------------------------------------------------\n")
            data.technicalSkills.forEach { sb.append("• ${it.name}: ${it.proficiency}\n") }
            sb.append("\n")
        }

        if (data.portfolioProjects.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("PORTFOLIO PROJECTS\n")
            sb.append("--------------------------------------------------\n")
            data.portfolioProjects.forEach {
                sb.append("• ${it.name}: ${it.description} | ${it.metric}\n")
            }
            sb.append("\n")
        }

        sb.append("Updated: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}\n")
        return sb.toString()
    }

    fun generateCvPdf(context: Context, data: CvData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            isAntiAlias = true
        }
        val paintTitle = Paint().apply {
            color = Color.rgb(33, 33, 33)
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val paintHeader = Paint().apply {
            color = Color.rgb(63, 81, 181)
            textSize = 12f
            isFakeBoldText = true
            isAntiAlias = true
        }

        var y = 50f
        val marginX = 40f
        val contentWidth = 595f - 80f

        // Draw Name & Title
        canvas.drawText(data.fullName.ifBlank { "CURRICULUM VITAE" }, marginX, y, paintTitle)
        y += 24f
        if (data.jobTitle.isNotBlank()) {
            paintText.textSize = 12f
            paintText.isFakeBoldText = true
            canvas.drawText(data.jobTitle, marginX, y, paintText)
            y += 18f
        }

        // Contact info horizontal strip
        paintText.textSize = 8.5f
        paintText.isFakeBoldText = false
        val contactList = listOfNotNull(
            data.email.takeIf { it.isNotBlank() },
            data.phone.takeIf { it.isNotBlank() },
            data.location.takeIf { it.isNotBlank() },
            data.portfolioUrl.takeIf { it.isNotBlank() }
        )
        if (contactList.isNotEmpty()) {
            val contactStr = contactList.joinToString("  |  ")
            canvas.drawText(contactStr, marginX, y, paintText)
            y += 15f
        }

        // Divider
        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.GRAY; strokeWidth = 1f })
        y += 20f

        // Summary
        canvas.drawText("PROFESSIONAL SUMMARY", marginX, y, paintHeader)
        y += 14f
        paintText.textSize = 9.5f
        y = drawWrappedText(canvas, data.professionalSummary.ifBlank { "Professional summary empty or pending entry." }, marginX, y, contentWidth, paintText)
        y += 15f

        // Core Competencies
        if (data.coreCompetencies.isNotEmpty()) {
            canvas.drawText("CORE COMPETENCIES", marginX, y, paintHeader)
            y += 14f
            data.coreCompetencies.forEach {
                paintText.isFakeBoldText = true
                val competencyHeader = "${it.name}: "
                canvas.drawText("• $competencyHeader", marginX, y, paintText)
                val textOffset = paintText.measureText("• $competencyHeader")
                paintText.isFakeBoldText = false
                y = drawWrappedText(canvas, it.description, marginX + textOffset, y, contentWidth - textOffset, paintText)
            }
            y += 15f
        }

        // Work Experience
        if (data.workExperiences.isNotEmpty()) {
            canvas.drawText("WORK EXPERIENCE", marginX, y, paintHeader)
            y += 14f
            data.workExperiences.forEach {
                paintText.isFakeBoldText = true
                canvas.drawText("• ${it.jobTitle} at ${it.companyName}", marginX, y, paintText)
                val dateSize = paintText.measureText(it.dates)
                canvas.drawText(it.dates, marginX + contentWidth - dateSize, y, paintText)
                paintText.isFakeBoldText = false
                y += 12f
                if (it.location.isNotBlank()) {
                    canvas.drawText("  Location: ${it.location}", marginX, y, paintText)
                    y += 12f
                }
                it.bullets.forEach { bullet ->
                    if (bullet.isNotBlank()) {
                        y = drawWrappedText(canvas, "  - $bullet", marginX, y, contentWidth, paintText)
                    }
                }
                y += 6f
            }
            y += 15f
        }

        // Education
        if (data.educations.isNotEmpty()) {
            canvas.drawText("EDUCATION", marginX, y, paintHeader)
            y += 14f
            data.educations.forEach {
                paintText.isFakeBoldText = true
                canvas.drawText("• ${it.degree} - ${it.institution}", marginX, y, paintText)
                paintText.isFakeBoldText = false
                val yearSize = paintText.measureText(it.year)
                canvas.drawText(it.year, marginX + contentWidth - yearSize, y, paintText)
                y += 12f
                if (it.gpa.isNotBlank()) {
                    canvas.drawText("  GPA: ${it.gpa}", marginX, y, paintText)
                    y += 12f
                }
            }
            y += 15f
        }

        // Footer
        val stamp = "Updated: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}"
        paintText.textSize = 8f
        paintText.color = Color.GRAY
        val stampWidth = paintText.measureText(stamp)
        canvas.drawText(stamp, marginX + contentWidth - stampWidth, 800f, paintText)

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "curriculum_vitae.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // 2. COVER LETTER EXPORTERS
    // ==========================================
    fun generateCoverLetterTxt(data: CoverLetterData): String {
        return """
${data.date.ifBlank { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }}

${data.hiringManager.ifBlank { "Hiring Manager" }}
${data.companyName.ifBlank { "Company Name" }}
${data.companyAddress.ifBlank { "Company Address" }}

Subject: Application for ${data.jobTitle.ifBlank { "[Job Title]" }} – ${data.yourName.ifBlank { "[Your Name]" }}

Dear ${data.hiringManager.ifBlank { "Hiring Manager" }},

${data.paragraph1.ifBlank { "Opening details..." }}

${data.paragraph2.ifBlank { "Details about why me..." }}

${data.paragraph3.ifBlank { "Closing details..." }}

Sincerely,

${data.yourName.ifBlank { "[Your Name]" }}
${data.yourContact.ifBlank { "[Your Contact details]" }}
        """.trimIndent()
    }

    fun generateCoverLetterPdf(context: Context, data: CoverLetterData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 10.5f
            isAntiAlias = true
        }
        val paintTitle = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }

        var y = 60f
        val marginX = 50f
        val contentWidth = 595f - 100f

        canvas.drawText("COVER LETTER", marginX, y, paintTitle)
        y += 40f

        // Date
        val activeDate = data.date.ifBlank { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
        canvas.drawText(activeDate, marginX, y, paintText)
        y += 24f

        // Recipient Address
        canvas.drawText(data.hiringManager.ifBlank { "[Hiring Manager Name]" }, marginX, y, paintText)
        y += 14f
        canvas.drawText(data.companyName.ifBlank { "[Company Name]" }, marginX, y, paintText)
        y += 14f
        canvas.drawText(data.companyAddress.ifBlank { "[Company Address]" }, marginX, y, paintText)
        y += 30f

        // Subject Line
        paintText.isFakeBoldText = true
        val jobT = data.jobTitle.ifBlank { "[Job Title]" }
        val nameT = data.yourName.ifBlank { "[Your Name]" }
        canvas.drawText("Subject: Application for $jobT – $nameT", marginX, y, paintText)
        y += 24f
        paintText.isFakeBoldText = false

        // Salutation
        val salutation = "Dear ${data.hiringManager.ifBlank { "Hiring Manager" }},"
        canvas.drawText(salutation, marginX, y, paintText)
        y += 24f

        // Paragraphs
        y = drawWrappedText(canvas, data.paragraph1.ifBlank { "Paragraph 1 empty." }, marginX, y, contentWidth, paintText)
        y += 16f
        y = drawWrappedText(canvas, data.paragraph2.ifBlank { "Paragraph 2 empty." }, marginX, y, contentWidth, paintText)
        y += 16f
        y = drawWrappedText(canvas, data.paragraph3.ifBlank { "Paragraph 3 empty." }, marginX, y, contentWidth, paintText)
        y += 30f

        // Closing
        canvas.drawText("Sincerely,", marginX, y, paintText)
        y += 30f
        paintText.isFakeBoldText = true
        canvas.drawText(data.yourName.ifBlank { "[Your Name]" }, marginX, y, paintText)
        paintText.isFakeBoldText = false
        y += 14f
        canvas.drawText(data.yourContact.ifBlank { "[Your Contact Info]" }, marginX, y, paintText)

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "cover_letter.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // 3. EMAIL EXPORTERS
    // ==========================================
    fun generateEmailTxt(data: EmailData): String {
        return """
To: ${data.recipientEmail.ifBlank { "[Recipient Email]" }}
Subject: ${data.subjectLine.ifBlank { "[Subject Line]" }}

${data.bodyMarkdown.ifBlank { "<!-- Body empty -->" }}

---
Signature:
${data.signatureName.ifBlank { "[Full Name]" }}
${data.signatureTitle.ifBlank { "[Title]" }}
${data.signaturePhone.ifBlank { "[Phone]" }}
${data.signatureWebsite.ifBlank { "[Website]" }}
${data.signatureLinkedIn.ifBlank { "[LinkedIn]" }}
        """.trimIndent()
    }

    fun generateEmailPdf(context: Context, data: EmailData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            isAntiAlias = true
        }
        val paintTitle = Paint().apply {
            color = Color.rgb(33, 150, 243)
            textSize = 16f
            isFakeBoldText = true
            isAntiAlias = true
        }

        var y = 60f
        val marginX = 50f
        val contentWidth = 595f - 100f

        canvas.drawText("EMAIL TRANSMISSION", marginX, y, paintTitle)
        y += 40f

        paintText.isFakeBoldText = true
        canvas.drawText("TO:  ${data.recipientEmail.ifBlank { "[Recipient Email]" }}", marginX, y, paintText)
        y += 18f
        canvas.drawText("SUB: ${data.subjectLine.ifBlank { "[Subject Line]" }}", marginX, y, paintText)
        y += 15f
        paintText.isFakeBoldText = false

        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.LTGRAY; strokeWidth = 1f })
        y += 24f

        y = drawWrappedText(canvas, data.bodyMarkdown.ifBlank { "Email body has not been inputted." }, marginX, y, contentWidth, paintText)
        y += 30f

        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.rgb(230, 230, 230); strokeWidth = 0.5f })
        y += 20f

        // Signature block
        paintText.isFakeBoldText = true
        canvas.drawText(data.signatureName.ifBlank { "[Full Name]" }, marginX, y, paintText)
        paintText.isFakeBoldText = false
        y += 14f
        canvas.drawText(data.signatureTitle.ifBlank { "[Title]" }, marginX, y, paintText)
        y += 14f
        canvas.drawText("Ph: " + data.signaturePhone.ifBlank { "[Phone]" }, marginX, y, paintText)
        y += 14f
        canvas.drawText("Web: " + data.signatureWebsite.ifBlank { "[Website]" }, marginX, y, paintText)
        y += 14f
        canvas.drawText("In: " + data.signatureLinkedIn.ifBlank { "[LinkedIn]" }, marginX, y, paintText)

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "email_followup.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // 4. INVOICE EXPORTERS
    // ==========================================
    fun generateInvoiceTxt(data: InvoiceData): String {
        val totalMap = calcInvoiceTotals(data)
        
        val sb = StringBuilder()
        sb.append("==================================================\n")
        sb.append("                     INVOICE                      \n")
        sb.append("==================================================\n\n")
        sb.append("Invoice No: ${data.invoiceNumber.ifBlank { "[INV-0000]" }}\n")
        sb.append("Date: ${data.invoiceDate.ifBlank { "[Select Date]" }}\n")
        sb.append("Due Date: ${data.dueDate.ifBlank { "[Select Due Date]" }}\n\n")

        sb.append("FROM:\n")
        sb.append("  ${data.myBusinessName.ifBlank { "[My Business Name]" }}\n")
        if (data.myAddress.isNotBlank()) sb.append("  Address: ${data.myAddress}\n")
        if (data.myTaxId.isNotBlank()) sb.append("  Tax ID: ${data.myTaxId}\n")
        sb.append("\n")

        sb.append("BILL TO:\n")
        sb.append("  ${data.clientName.ifBlank { "[Client Name]" }}\n")
        if (data.clientCompany.isNotBlank()) sb.append("  Company: ${data.clientCompany}\n")
        if (data.clientAddress.isNotBlank()) sb.append("  Address: ${data.clientAddress}\n")
        sb.append("\n")

        sb.append("--------------------------------------------------\n")
        sb.append(String.format("%-25s %4s %8s %10s\n", "Description", "Qty", "Price", "Subtotal"))
        sb.append("--------------------------------------------------\n")
        
        if (data.lineItems.isEmpty()) {
            sb.append("  [No line items added yet - Total invoice is empty]\n")
        } else {
            data.lineItems.forEach {
                val rowSub = it.quantity * it.unitPrice
                sb.append(String.format("%-25s %4.1f %8.2f %10.2f\n", 
                    if(it.description.length > 24) it.description.substring(0, 21) + "..." else it.description,
                    it.quantity, it.unitPrice, rowSub))
            }
        }
        sb.append("--------------------------------------------------\n")
        sb.append(String.format("%38s: %10.2f\n", "Subtotal", totalMap["subtotal"] ?: 0.0))
        sb.append(String.format("%38s: %10.2f\n", "Discount (-${data.discount}%)", totalMap["discountAmount"] ?: 0.0))
        sb.append(String.format("%38s: %10.2f\n", "Tax (+${data.taxRate}%)", totalMap["taxAmount"] ?: 0.0))
        sb.append("--------------------------------------------------\n")
        sb.append(String.format("%38s: %10.2f\n", "TOTAL DUE", totalMap["total"] ?: 0.0))
        sb.append("--------------------------------------------------\n\n")

        if (data.paymentInstructions.isNotBlank()) {
            sb.append("PAYMENT INSTRUCTIONS:\n  ${data.paymentInstructions}\n\n")
        }
        if (data.notesTerms.isNotBlank()) {
            sb.append("NOTES / TERMS:\n  ${data.notesTerms}\n")
        }
        return sb.toString()
    }

    fun calcInvoiceTotals(data: InvoiceData): Map<String, Double> {
        val subtotal = data.lineItems.sumOf { (it.quantity * it.unitPrice).toDouble() }
        val discountAmount = subtotal * (data.discount / 100.0)
        val taxableAmount = subtotal - discountAmount
        val taxAmount = taxableAmount * (data.taxRate / 100.0)
        val total = taxableAmount + taxAmount
        return mapOf(
            "subtotal" to subtotal,
            "discountAmount" to discountAmount,
            "taxAmount" to taxAmount,
            "total" to total
        )
    }

    fun generateInvoicePdf(context: Context, data: InvoiceData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 9.5f
            isAntiAlias = true
        }
        val paintTitle = Paint().apply {
            color = Color.rgb(233, 30, 99)
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }

        var y = 60f
        val marginX = 40f
        val contentWidth = 595f - 80f

        canvas.drawText("INVOICE", marginX, y, paintTitle)
        
        paintText.isFakeBoldText = true
        val numStr = "No: " + data.invoiceNumber.ifBlank { "[INV-0000]" }
        val numWidth = paintText.measureText(numStr)
        canvas.drawText(numStr, marginX + contentWidth - numWidth, y, paintText)
        paintText.isFakeBoldText = false
        y += 24f

        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.GRAY; strokeWidth = 1f })
        y += 20f

        // Dates Block
        canvas.drawText("Date: ${data.invoiceDate.ifBlank { "[No Date Selected]" }}", marginX, y, paintText)
        val dueStr = "Due Date: ${data.dueDate.ifBlank { "[No Due Date]" }}"
        val dueWidth = paintText.measureText(dueStr)
        canvas.drawText(dueStr, marginX + contentWidth - dueWidth, y, paintText)
        y += 24f

        // From vs To column block
        val colW = contentWidth / 2f
        canvas.drawText("FROM:", marginX, y, Paint(paintText).apply { isFakeBoldText = true })
        canvas.drawText("BILL TO:", marginX + colW, y, Paint(paintText).apply { isFakeBoldText = true })
        y += 15f

        canvas.drawText(data.myBusinessName.ifBlank { "[My Business Name]" }, marginX, y, paintText)
        canvas.drawText(data.clientName.ifBlank { "[Client Name]" }, marginX + colW, y, paintText)
        y += 14f

        canvas.drawText(data.myAddress.ifBlank { "[My Business Address]" }, marginX, y, paintText)
        canvas.drawText(data.clientCompany.ifBlank { "[Client Company]" }, marginX + colW, y, paintText)
        y += 14f

        canvas.drawText("Tax ID: " + data.myTaxId.ifBlank { "[My Tax ID]" }, marginX, y, paintText)
        canvas.drawText(data.clientAddress.ifBlank { "[Client Address]" }, marginX + colW, y, paintText)
        y += 30f

        // Table Header
        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.BLACK; strokeWidth = 1f })
        y += 14f
        paintText.isFakeBoldText = true
        canvas.drawText("Item Description", marginX, y, paintText)
        canvas.drawText("Qty", marginX + 260f, y, paintText)
        canvas.drawText("Unit Price", marginX + 320f, y, paintText)
        canvas.drawText("Subtotal", marginX + 410f, y, paintText)
        y += 6f
        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.LTGRAY; strokeWidth = 0.5f })
        y += 14f
        paintText.isFakeBoldText = false

        // Rows
        if (data.lineItems.isEmpty()) {
            canvas.drawText("[No line items added yet. Click Add Line Item to build your fee entries.]", marginX, y, paintText)
            y += 20f
        } else {
            data.lineItems.forEach {
                canvas.drawText(it.description.ifBlank { "[Unlabeled Item]" }, marginX, y, paintText)
                canvas.drawText(String.format("%.1f", it.quantity), marginX + 260f, y, paintText)
                canvas.drawText(String.format("$%.2f", it.unitPrice), marginX + 320f, y, paintText)
                canvas.drawText(String.format("$%.2f", it.quantity * it.unitPrice), marginX + 410f, y, paintText)
                y += 16f
            }
        }

        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.GRAY; strokeWidth = 0.5f })
        y += 20f

        val totals = calcInvoiceTotals(data)

        // Totals column
        val totalsLabelX = marginX + 280f
        val totalsValX = marginX + 410f
        
        canvas.drawText("Subtotal:", totalsLabelX, y, paintText)
        canvas.drawText(String.format("$%.2f", totals["subtotal"] ?: 0.0), totalsValX, y, paintText)
        y += 14f

        canvas.drawText("Discount (-${data.discount}%):", totalsLabelX, y, paintText)
        canvas.drawText(String.format("-$%.2f", totals["discountAmount"] ?: 0.0), totalsValX, y, paintText)
        y += 14f

        canvas.drawText("Tax (+${data.taxRate}%):", totalsLabelX, y, paintText)
        canvas.drawText(String.format("+$%.2f", totals["taxAmount"] ?: 0.0), totalsValX, y, paintText)
        y += 16f

        canvas.drawLine(totalsLabelX, y, marginX + contentWidth, y, Paint().apply { color = Color.BLACK; strokeWidth = 1f })
        y += 16f

        paintText.isFakeBoldText = true
        canvas.drawText("TOTAL DUE:", totalsLabelX, y, paintText)
        canvas.drawText(String.format("$%.2f", totals["total"] ?: 0.0), totalsValX, y, paintText)
        paintText.isFakeBoldText = false
        y += 30f

        // Payment / Terms
        if (data.paymentInstructions.isNotBlank()) {
            canvas.drawText("PAYMENT INSTRUCTIONS:", marginX, y, Paint(paintText).apply { isFakeBoldText = true })
            y += 12f
            y = drawWrappedText(canvas, data.paymentInstructions, marginX, y, contentWidth, paintText)
            y += 16f
        }

        if (data.notesTerms.isNotBlank()) {
            canvas.drawText("NOTES / TERMS:", marginX, y, Paint(paintText).apply { isFakeBoldText = true })
            y += 12f
            y = drawWrappedText(canvas, data.notesTerms, marginX, y, contentWidth, paintText)
        }

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "invoice.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // 5. PROJECT PROPOSAL EXPORTERS
    // ==========================================
    fun generateProposalTxt(data: ProposalData): String {
        val sb = StringBuilder()
        sb.append("==================================================\n")
        sb.append("                 PROJECT PROPOSAL                 \n")
        sb.append("==================================================\n\n")
        sb.append("Title: ${data.title.ifBlank { "[Proposal Title]" }}\n")
        sb.append("Date: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}\n")
        sb.append("\n--------------------------------------------------\n")
        sb.append("1. EXECUTIVE SUMMARY\n")
        sb.append("--------------------------------------------------\n")
        sb.append(data.executiveSummary.ifBlank { "[Executive Summary Empty]" }).append("\n\n")

        if (data.scope.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("2. PROJECT SCOPE & APPROACH\n")
            sb.append("--------------------------------------------------\n")
            data.scope.forEachIndexed { i, s ->
                sb.append("Phase ${i + 1}: $s\n")
            }
            sb.append("\n")
        }

        if (data.deliverables.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("3. DELIVERABLES CHECKLIST\n")
            sb.append("--------------------------------------------------\n")
            data.deliverables.forEach {
                val check = if (it.isChecked) "[X]" else "[ ]"
                sb.append("$check ${it.title}\n")
            }
            sb.append("\n")
        }

        if (data.timeline.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("4. TIMELINE\n")
            sb.append("--------------------------------------------------\n")
            data.timeline.forEach {
                sb.append("• ${it.phase}: ${it.description}\n")
            }
            sb.append("\n")
        }

        sb.append("--------------------------------------------------\n")
        sb.append("5. INVESTMENT DETAILS\n")
        sb.append("--------------------------------------------------\n")
        sb.append(data.investmentDetails.ifBlank { "[Investment details not populated]" }).append("\n\n")

        sb.append("--------------------------------------------------\n")
        sb.append("6. WHY OUR TEAM / ME\n")
        sb.append("--------------------------------------------------\n")
        sb.append(data.whyMe.ifBlank { "[Team details blank]" }).append("\n\n")

        if (data.nextSteps.isNotEmpty()) {
            sb.append("--------------------------------------------------\n")
            sb.append("7. NEXT STEPS\n")
            sb.append("--------------------------------------------------\n")
            data.nextSteps.forEachIndexed { i, s ->
                sb.append("${i + 1}. $s\n")
            }
            sb.append("\n")
        }

        return sb.toString()
    }

    fun generateProposalPdf(context: Context, data: ProposalData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            isAntiAlias = true
        }
        val paintTitle = Paint().apply {
            color = Color.rgb(0, 150, 136)
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val paintHeader = Paint().apply {
            color = Color.rgb(0, 121, 107)
            textSize = 12f
            isFakeBoldText = true
            isAntiAlias = true
        }

        var y = 60f
        val marginX = 40f
        val contentWidth = 595f - 80f

        // Document Title
        canvas.drawText("PROJECT PROPOSAL", marginX, y, paintTitle)
        y += 18f
        paintText.isFakeBoldText = true
        canvas.drawText(data.title.ifBlank { "[Click to add proposal title]" }, marginX, y, paintText)
        paintText.isFakeBoldText = false
        y += 24f

        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.GRAY; strokeWidth = 1f })
        y += 20f

        // Exec Summary
        canvas.drawText("1. Executive Summary", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, data.executiveSummary.ifBlank { "Executive summary paragraph is empty." }, marginX, y, contentWidth, paintText)
        y += 16f

        // Scope
        if (data.scope.isNotEmpty()) {
            canvas.drawText("2. Project Scope & Approach", marginX, y, paintHeader)
            y += 14f
            data.scope.forEachIndexed { i, item ->
                y = drawWrappedText(canvas, "Phase ${i + 1}: $item", marginX, y, contentWidth, paintText)
            }
            y += 16f
        }

        // Deliverables
        if (data.deliverables.isNotEmpty()) {
            canvas.drawText("3. Deliverables Checklist", marginX, y, paintHeader)
            y += 14f
            data.deliverables.forEach {
                val box = if (it.isChecked) "[x] " else "[  ] "
                y = drawWrappedText(canvas, box + it.title, marginX, y, contentWidth, paintText)
            }
            y += 16f
        }

        // Timeline
        if (data.timeline.isNotEmpty()) {
            canvas.drawText("4. Projected Timeline", marginX, y, paintHeader)
            y += 14f
            data.timeline.forEach {
                paintText.isFakeBoldText = true
                canvas.drawText("• ${it.phase}: ", marginX, y, paintText)
                val tw = paintText.measureText("• ${it.phase}: ")
                paintText.isFakeBoldText = false
                y = drawWrappedText(canvas, it.description, marginX + tw, y, contentWidth - tw, paintText)
            }
            y += 16f
        }

        // Investment
        canvas.drawText("5. Financial Investment", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, data.investmentDetails.ifBlank { "Investment structure empty." }, marginX, y, contentWidth, paintText)
        y += 16f

        // Why Us
        canvas.drawText("6. Why Me / Our Credentials", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, data.whyMe.ifBlank { "Credentials empty." }, marginX, y, contentWidth, paintText)
        y += 16f

        // Next Steps
        if (data.nextSteps.isNotEmpty()) {
            canvas.drawText("7. Acceptance & Next Steps", marginX, y, paintHeader)
            y += 14f
            data.nextSteps.forEachIndexed { idx, step ->
                y = drawWrappedText(canvas, "${idx + 1}. $step", marginX, y, contentWidth, paintText)
            }
        }

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "project_proposal.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // SYSTEM AND EXPORT PLUMBING
    // ==========================================
    fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Share Document via:"))
    }

    fun printPdf(context: Context, pdfFile: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "Nexturn Document - ${pdfFile.name}"
        
        printManager.print(jobName, object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }
                val info = PrintDocumentInfo.Builder(pdfFile.name)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1) // Usually we keep 1 page in this scope
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out android.print.PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                var input: FileInputStream? = null
                var output: FileOutputStream? = null
                try {
                    input = FileInputStream(pdfFile)
                    output = FileOutputStream(destination?.fileDescriptor)
                    val buf = ByteArray(1024)
                    var bytesRead: Int
                    while (input.read(buf).also { bytesRead = it } > 0) {
                        output.write(buf, 0, bytesRead)
                    }
                    callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                } finally {
                    input?.close()
                    output?.close()
                }
            }
        }, null)
    }

    // Helper: Saves text to file for sharing
    fun writeTextToFile(context: Context, text: String, filename: String): File {
        val file = File(context.cacheDir, filename)
        FileOutputStream(file).use { out ->
            out.write(text.toByteArray())
        }
        return file
    }
}
