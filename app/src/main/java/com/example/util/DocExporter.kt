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
    // 6. OFFER LETTER EXPORTERS
    // ==========================================
    fun generateOfferLetterTxt(data: OfferLetterData): String {
        return """
            OFFER OF EMPLOYMENT
            Start Date: ${data.startDate}
            
            Company: ${data.companyName}
            To: ${data.candidateName}
            
            Dear ${data.candidateName},
            
            We are pleased to offer you the position of ${data.jobTitle} starting on ${data.startDate}.
            
            Compensation and Benefits:
            - Salary: ${data.salary}
            
            Duties and Details:
            ${data.offerDetails}
            
            We are excited about the prospect of you joining our team. Please sign below to acknowledge acceptance of this offer.
            
            Sincerely,
            ${data.signatoryName}
            ${data.signatoryTitle}
            ${data.companyName}
        """.trimIndent()
    }

    fun generateOfferLetterPdf(context: Context, data: OfferLetterData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply { color = Color.BLACK; textSize = 11f; isAntiAlias = true }
        val paintTitle = Paint().apply { color = Color.rgb(33, 150, 243); textSize = 20f; isFakeBoldText = true; isAntiAlias = true }
        val paintHeader = Paint().apply { color = Color.rgb(25, 118, 210); textSize = 13f; isFakeBoldText = true; isAntiAlias = true }

        var y = 60f
        val marginX = 45f
        val contentWidth = 595f - 90f

        canvas.drawText("OFFER OF EMPLOYMENT", marginX, y, paintTitle)
        y += 24f
        canvas.drawText("Start Date: ${data.startDate}", marginX, y, paintText)
        y += 24f

        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.GRAY; strokeWidth = 1f })
        y += 20f

        val candName = if (data.candidateName.isBlank()) "[Candidate Name]" else data.candidateName
        val compName = if (data.companyName.isBlank()) "[Company Name]" else data.companyName
        val jobT = if (data.jobTitle.isBlank()) "[Job Title]" else data.jobTitle

        y = drawWrappedText(canvas, "Dear $candName,", marginX, y, contentWidth, paintText)
        y += 12f
        y = drawWrappedText(canvas, "On behalf of $compName, we are pleased to offer you the position of $jobT starting on ${data.startDate}.", marginX, y, contentWidth, paintText)
        y += 18f

        canvas.drawText("Salary & Compensation", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, if (data.salary.isBlank()) "[Salary Details]" else data.salary, marginX, y, contentWidth, paintText)
        y += 18f

        canvas.drawText("Offer Details & Duties", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, if (data.offerDetails.isBlank()) "[Offer Details]" else data.offerDetails, marginX, y, contentWidth, paintText)
        y += 30f

        y = drawWrappedText(canvas, "Sincerely,", marginX, y, contentWidth, paintText)
        y += 25f
        y = drawWrappedText(canvas, if (data.signatoryName.isBlank()) "[Signatory Name]" else data.signatoryName, marginX, y, contentWidth, paintText)
        y = drawWrappedText(canvas, "${data.signatoryTitle} | $compName", marginX, y, contentWidth, paintText)

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "offer_letter.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // 7. RESIGNATION LETTER EXPORTERS
    // ==========================================
    fun generateResignationLetterTxt(data: ResignationLetterData): String {
        return """
            RESIGNATION NOTICE
            Date: ${data.lastWorkingDay}
            
            To: ${data.managerName}
            Company: ${data.companyName}
            
            Dear ${data.managerName},
            
            Please accept this letter as formal notification that I am resigning from my position. My last working day will be ${data.lastWorkingDay}.
            
            Reason for Resignation:
            ${data.resignationReason}
            
            Personal Note & Gratitude:
            ${data.personalNote}
            
            I wish the company continued success in the future.
            
            Sincerely,
            ${data.employeeName}
            ${data.signatureName}
        """.trimIndent()
    }

    fun generateResignationLetterPdf(context: Context, data: ResignationLetterData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply { color = Color.BLACK; textSize = 11f; isAntiAlias = true }
        val paintTitle = Paint().apply { color = Color.rgb(244, 67, 54); textSize = 20f; isFakeBoldText = true; isAntiAlias = true }
        val paintHeader = Paint().apply { color = Color.rgb(198, 40, 40); textSize = 13f; isFakeBoldText = true; isAntiAlias = true }

        var y = 60f
        val marginX = 45f
        val contentWidth = 595f - 90f

        canvas.drawText("RESIGNATION LETTER", marginX, y, paintTitle)
        y += 24f
        canvas.drawText("Target Effective Date: ${data.lastWorkingDay}", marginX, y, paintText)
        y += 24f

        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.GRAY; strokeWidth = 1f })
        y += 20f

        val mgr = if (data.managerName.isBlank()) "[Manager Name]" else data.managerName
        val comp = if (data.companyName.isBlank()) "[Company Name]" else data.companyName

        y = drawWrappedText(canvas, "Dear $mgr,", marginX, y, contentWidth, paintText)
        y += 12f
        y = drawWrappedText(canvas, "Please accept this letter as formal notification that I am resigning from my position at $comp. My target last working day will be ${data.lastWorkingDay}.", marginX, y, contentWidth, paintText)
        y += 18f

        canvas.drawText("Reason for Departure", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, if (data.resignationReason.isBlank()) "[Resignation Reason]" else data.resignationReason, marginX, y, contentWidth, paintText)
        y += 18f

        canvas.drawText("Gratitude & Closing Note", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, if (data.personalNote.isBlank()) "[Personal Note / Gratitude]" else data.personalNote, marginX, y, contentWidth, paintText)
        y += 30f

        y = drawWrappedText(canvas, "Sincerely,", marginX, y, contentWidth, paintText)
        y += 25f
        y = drawWrappedText(canvas, if (data.employeeName.isBlank()) "[Employee Name]" else data.employeeName, marginX, y, contentWidth, paintText)
        if (data.signatureName.isNotBlank() && data.signatureName != data.employeeName) {
            y = drawWrappedText(canvas, "Signature: ${data.signatureName}", marginX, y, contentWidth, paintText)
        }

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "resignation_letter.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // 8. SERVICE CONTRACT EXPORTERS
    // ==========================================
    fun generateServiceContractTxt(data: ServiceContractData): String {
        return """
            SERVICE CONTRACT / AGREEMENT
            Date of Agreement: ${data.agreementDate}
            
            PARTIES:
            - Contractor: ${data.contractorName}
            - Client: ${data.clientName}
            
            1. SCOPE OF SERVICES:
            ${data.scopeOfWork}
            
            2. PAYMENT AND COMPENSATION TYPE:
            ${data.paymentTerms} (${data.compensation})
            
            3. GOVERNING LAW:
            This Agreement shall be structured under the rules of ${data.governingLaw}.
            
            Contractor: ${data.contractorName}
            Client: ${data.clientName}
        """.trimIndent()
    }

    fun generateServiceContractPdf(context: Context, data: ServiceContractData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply { color = Color.BLACK; textSize = 11f; isAntiAlias = true }
        val paintTitle = Paint().apply { color = Color.rgb(63, 81, 181); textSize = 20f; isFakeBoldText = true; isAntiAlias = true }
        val paintHeader = Paint().apply { color = Color.rgb(48, 63, 159); textSize = 13f; isFakeBoldText = true; isAntiAlias = true }

        var y = 60f
        val marginX = 45f
        val contentWidth = 595f - 90f

        canvas.drawText("SERVICE AGREEMENT", marginX, y, paintTitle)
        y += 20f
        canvas.drawText("Agreement Date: ${data.agreementDate}", marginX, y, paintText)
        y += 24f

        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.GRAY; strokeWidth = 1f })
        y += 20f

        val contractor = if (data.contractorName.isBlank()) "[Contractor Name]" else data.contractorName
        val client = if (data.clientName.isBlank()) "[Client Name]" else data.clientName

        y = drawWrappedText(canvas, "This Professional Service Agreement is entered into by Contractor $contractor and Client $client.", marginX, y, contentWidth, paintText)
        y += 18f

        canvas.drawText("1. Scope of Work", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, if (data.scopeOfWork.isBlank()) "[Scope of Work / Deliverables]" else data.scopeOfWork, marginX, y, contentWidth, paintText)
        y += 18f

        canvas.drawText("2. Payment Terms & Compensation", marginX, y, paintHeader)
        y += 14f
        val payTerms = if (data.paymentTerms.isBlank()) "[Payment Terms]" else data.paymentTerms
        val compensationVal = if (data.compensation.isBlank()) "" else " (${data.compensation})"
        y = drawWrappedText(canvas, "$payTerms$compensationVal", marginX, y, contentWidth, paintText)
        y += 18f

        canvas.drawText("3. Governing Law", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, "This entire service arrangement shall be construed under the laws of ${data.governingLaw}.", marginX, y, contentWidth, paintText)
        y += 30f

        canvas.drawText("Signed in Agreement:", marginX, y, paintHeader)
        y += 20f
        canvas.drawText("Contractor: $contractor", marginX, y, paintText)
        canvas.drawText("Client Signatory: $client", marginX + 220f, y, paintText)

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "service_contract.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // 9. CERTIFICATE EXPORTERS
    // ==========================================
    fun generateCertificateTxt(data: CertificateData): String {
        val desc = if (data.certificateDescription.isBlank()) "[Certificate Description]" else data.certificateDescription
        val awarding = if (data.awardingOrg.isBlank()) "[Awarding Organization]" else data.awardingOrg
        val recipient = if (data.recipientName.isBlank()) "[Recipient Name]" else data.recipientName
        val title = if (data.achievementTitle.isBlank()) "[Achievement Title]" else data.achievementTitle
        val signatory = if (data.authoritySignatory.isBlank()) "[Authority Signatory]" else data.authoritySignatory

        return """
            CERTIFICATE OF ACHIEVEMENT
            
            This is to certify that
            $recipient
            
            has successfully completed the requirements for
            $title
            
            Description:
            $desc
            
            Awarded by:
            $awarding
            
            Date of Issue: ${data.dateOfIssue}
            Authorized Signatory: $signatory
        """.trimIndent()
    }

    fun generateCertificatePdf(context: Context, data: CertificateData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply { color = Color.BLACK; textSize = 11f; isAntiAlias = true }
        val paintTitle = Paint().apply { color = Color.rgb(255, 193, 7); textSize = 24f; isFakeBoldText = true; isAntiAlias = true }
        val paintHeader = Paint().apply { color = Color.rgb(255, 111, 0); textSize = 14f; isFakeBoldText = true; isAntiAlias = true }

        var y = 100f
        val marginX = 45f
        val contentWidth = 595f - 90f

        // Let's add a neat certificate frame on the canvas!
        canvas.drawRect(20f, 20f, 575f, 822f, Paint().apply { color = Color.rgb(255, 160, 0); strokeWidth = 4f; style = Paint.Style.STROKE })
        canvas.drawRect(25f, 25f, 570f, 817f, Paint().apply { color = Color.rgb(255, 236, 179); strokeWidth = 1f; style = Paint.Style.STROKE })

        canvas.drawText("CERTIFICATE OF COMPLETION", marginX + 60f, y, paintTitle)
        y += 40f

        y = drawWrappedText(canvas, "This certificate is proudly awarded to:", marginX + 110f, y, contentWidth, paintText)
        y += 24f

        canvas.drawText(if (data.recipientName.isBlank()) "[Recipient Full Name]" else data.recipientName, marginX + 120f, y, paintHeader)
        y += 30f

        y = drawWrappedText(canvas, "In recognition of successful achievement in master level skills of:", marginX + 60f, y, contentWidth, paintText)
        y += 16f
        y = drawWrappedText(canvas, if (data.achievementTitle.isBlank()) "[Achievement Title]" else data.achievementTitle, marginX + 80f, y, contentWidth - 80f, paintText)
        y += 20f

        val certificateDesc = if (data.certificateDescription.isBlank()) "[Achievement Details / Scope]" else data.certificateDescription
        y = drawWrappedText(canvas, certificateDesc, marginX + 60f, y, contentWidth - 120f, paintText)
        y += 40f

        canvas.drawText("Granted By: ${if (data.awardingOrg.isBlank()) "[Awarding Organization]" else data.awardingOrg}", marginX + 60f, y, paintText)
        y += 24f
        canvas.drawText("Date: ${data.dateOfIssue}", marginX + 60f, y, paintText)
        canvas.drawText("Signatory: ${if (data.authoritySignatory.isBlank()) "[Authority Signatory]" else data.authoritySignatory}", marginX + 280f, y - 24f, paintText)

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "certificate.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // 10. MEETING MINUTES EXPORTERS
    // ==========================================
    fun generateMeetingMinutesTxt(data: MeetingMinutesData): String {
        return """
            MEETING MINUTES
            Title: ${data.meetingTitle}
            Date: ${data.meetingDate}
            Facilitator: ${data.facilitator}
            
            ATTENDEES:
            ${data.attendees}
            
            DISCUSSIONS AND DECISIONS:
            ${data.discussionSummary}
            
            ACTION ITEMS:
            ${data.actionItems.joinToString("\n") { "• [ ] $it" }}
        """.trimIndent()
    }

    fun generateMeetingMinutesPdf(context: Context, data: MeetingMinutesData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply { color = Color.BLACK; textSize = 11f; isAntiAlias = true }
        val paintTitle = Paint().apply { color = Color.rgb(0, 150, 136); textSize = 20f; isFakeBoldText = true; isAntiAlias = true }
        val paintHeader = Paint().apply { color = Color.rgb(0, 77, 64); textSize = 13f; isFakeBoldText = true; isAntiAlias = true }

        var y = 60f
        val marginX = 45f
        val contentWidth = 595f - 90f

        canvas.drawText("MEETING MINUTES", marginX, y, paintTitle)
        y += 20f
        canvas.drawText("Title: ${data.meetingTitle}", marginX, y, paintText)
        y += 14f
        canvas.drawText("Date: ${data.meetingDate} | Fac: ${data.facilitator}", marginX, y, paintText)
        y += 24f

        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.GRAY; strokeWidth = 1f })
        y += 20f

        canvas.drawText("Attendees:", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, if (data.attendees.isBlank()) "[No Attendees Specified]" else data.attendees, marginX, y, contentWidth, paintText)
        y += 18f

        canvas.drawText("Summary of Discussions:", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, if (data.discussionSummary.isBlank()) "[No Discussion Notes Written]" else data.discussionSummary, marginX, y, contentWidth, paintText)
        y += 18f

        if (data.actionItems.isNotEmpty()) {
            canvas.drawText("Committed Action Items:", marginX, y, paintHeader)
            y += 14f
            data.actionItems.forEach {
                val text = "• [  ]  $it"
                y = drawWrappedText(canvas, text, marginX, y, contentWidth, paintText)
            }
        }

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "meeting_minutes.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // 11. BUSINESS LETTER EXPORTERS
    // ==========================================
    fun generateBusinessLetterTxt(data: BusinessLetterData): String {
        return """
            BUSINESS LETTER
            Date: ${data.date}
            Subject: ${data.subject}
            
            SENDER DETAILS:
            ${data.senderName}
            ${data.senderAddress}
            
            RECIPIENT DETAILS:
            ${data.recipientAddress}
            
            ${data.salutation}
            
            ${data.paragraph1}
            
            ${data.paragraph2}
            
            ${data.paragraph3}
            
            ${data.valediction}
            ${data.senderName}
        """.trimIndent()
    }

    fun generateBusinessLetterPdf(context: Context, data: BusinessLetterData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = Paint().apply { color = Color.BLACK; textSize = 11f; isAntiAlias = true }
        val paintTitle = Paint().apply { color = Color.rgb(121, 85, 72); textSize = 20f; isFakeBoldText = true; isAntiAlias = true }
        val paintHeader = Paint().apply { color = Color.rgb(93, 64, 55); textSize = 13f; isFakeBoldText = true; isAntiAlias = true }

        var y = 60f
        val marginX = 45f
        val contentWidth = 595f - 90f

        canvas.drawText("BUSINESS LETTER", marginX, y, paintTitle)
        y += 20f
        canvas.drawText("Date: ${data.date}", marginX, y, paintText)
        y += 24f

        canvas.drawLine(marginX, y, marginX + contentWidth, y, Paint().apply { color = Color.GRAY; strokeWidth = 1f })
        y += 20f

        canvas.drawText("Sender Details:", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, if (data.senderName.isBlank()) "[Sender Name]" else data.senderName, marginX, y, contentWidth, paintText)
        y = drawWrappedText(canvas, if (data.senderAddress.isBlank()) "[Sender Address]" else data.senderAddress, marginX, y, contentWidth, paintText)
        y += 18f

        canvas.drawText("Recipient Details:", marginX, y, paintHeader)
        y += 14f
        y = drawWrappedText(canvas, if (data.recipientAddress.isBlank()) "[Recipient Address]" else data.recipientAddress, marginX, y, contentWidth, paintText)
        y += 20f

        canvas.drawText("Subject: ${data.subject}", marginX, y, paintHeader)
        y += 20f

        y = drawWrappedText(canvas, data.salutation, marginX, y, contentWidth, paintText)
        y += 12f

        if (data.paragraph1.isNotBlank()) {
            y = drawWrappedText(canvas, data.paragraph1, marginX, y, contentWidth, paintText)
            y += 12f
        }
        if (data.paragraph2.isNotBlank()) {
            y = drawWrappedText(canvas, data.paragraph2, marginX, y, contentWidth, paintText)
            y += 12f
        }
        if (data.paragraph3.isNotBlank()) {
            y = drawWrappedText(canvas, data.paragraph3, marginX, y, contentWidth, paintText)
            y += 12f
        }

        y = drawWrappedText(canvas, data.valediction, marginX, y, contentWidth, paintText)
        y += 25f
        y = drawWrappedText(canvas, if (data.senderName.isBlank()) "[Sender Name]" else data.senderName, marginX, y, contentWidth, paintText)

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "business_letter.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    // ==========================================
    // SYSTEM AND EXPORT PLUMBING
    // ==========================================
    var isEmailOverride: Boolean = false

    fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            if (isEmailOverride) {
                type = "message/rfc822" // direct mail selector
                putExtra(android.content.Intent.EXTRA_SUBJECT, "Nexturn Document Export - ${file.name.replace("_", " ").replace(".pdf", "").replace(".txt", "").uppercase()}")
                putExtra(android.content.Intent.EXTRA_TEXT, "Hello,\n\nPlease find the attached document '${file.name}' generated via your Nexturn Workspace.\n\nBest regards.")
            } else {
                type = mimeType
            }
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooserTitle = if (isEmailOverride) "Send Email via:" else "Share Document via:"
        context.startActivity(android.content.Intent.createChooser(intent, chooserTitle))
    }

    fun sendEmailWithAttachment(context: Context, file: File, mimeType: String, subject: String = "Nexturn Document Export") {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "message/rfc822" // standard mail RFC format
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
            putExtra(android.content.Intent.EXTRA_TEXT, "Hello,\n\nPlease find the attached document '${file.name}' generated via Nexturn Workspace.\n\nBest regards.")
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Send Email via:"))
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

    fun generateDynamicTxt(tabType: Int, json: String): String {
        return "Exported Document (Type $tabType)\n\n" + json // simple dump
    }

    fun generateDynamicPdf(context: Context, tabType: Int, json: String): File {
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
        }
        var yPos = 50f
        
        val outputLines = mutableListOf<String>()
        try {
            val js = org.json.JSONObject(json)
            val keys = js.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val v = js.optString(k, "")
                if (v.isNotBlank() && !v.startsWith("[")) {
                     outputLines.add("$k: $v")
                }
            }
        } catch (_: Exception) {}

        outputLines.forEach { line ->
            canvas.drawText(line, 50f, yPos, paint)
            yPos += 20f
        }
        if (outputLines.isEmpty()) canvas.drawText("Empty or custom structure", 50f, yPos, paint)

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "Document_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { pdfDocument.writeTo(it) }
        pdfDocument.close()
        return file
    }

    // Helper: Saves text to file for sharing
    fun writeTextToFile(context: Context, text: String, filename: String): File {
        val file = File(context.cacheDir, filename)
        FileOutputStream(file).use { out ->
            out.write(text.toByteArray())
        }
        return file
    }
    
    // Batch Export Helper
    fun batchExport(context: Context, docs: List<com.example.data.SavedDocument>, format: String) {
        val files = mutableListOf<File>()
        for (doc in docs) {
            try {
                if (format == "TXT") {
                    files.add(writeTextToFile(context, "Type: ${doc.type}\nContent: ${doc.contentJson}", "${doc.name}.txt"))
                } else {
                    files.add(generateDynamicPdf(context, 23, doc.contentJson)) // Quick fallback
                }
            } catch (_: Exception) {}
        }
        if (files.isNotEmpty()) {
            shareMultipleFiles(context, files, if (format == "TXT") "text/plain" else "application/pdf")
        }
    }

    private fun shareMultipleFiles(context: Context, files: List<File>, mimeType: String) {
        val uris = files.map { file ->
            androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }.let { java.util.ArrayList(it) }

        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND_MULTIPLE).apply {
            type = mimeType
            putParcelableArrayListExtra(android.content.Intent.EXTRA_STREAM, uris)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Batch Export"))
    }

    // Direct public export to the device's main Downloads directory
    fun exportToPublicDownloads(context: Context, file: File, mimeType: String): File? {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        FileInputStream(file).use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    return File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS), file.name)
                }
            } else {
                val destDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                if (!destDir.exists()) destDir.mkdirs()
                val destFile = File(destDir, file.name)
                FileOutputStream(destFile).use { outputStream ->
                    FileInputStream(file).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                return destFile
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
