package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun SmartField(
    fieldName: String,
    label: String,
    placeholder: String,
    value: String,
    onUpdate: (String) -> Unit,
    focusedField: MutableState<String>,
    highlightIfEmpty: Boolean,
    isMultiline: Boolean = false
) {
    HighlightField(
        value = value,
        onValueChange = onUpdate,
        label = label,
        promptText = label,
        placeholder = placeholder,
        highlightIfEmpty = highlightIfEmpty,
        isMultiline = isMultiline,
        focusedField = focusedField,
        fieldName = fieldName,
        leadingIcon = null
    )
}

@Composable
fun ReferenceLetterForm(data: ReferenceLetterData, onUpdate: (ReferenceLetterData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("recipientName", "Recipient Name", "[Recipient Name]", data.recipientName, { onUpdate(data.copy(recipientName = it)) }, focusedField, highlightIfEmpty)
    SmartField("subject", "Subject", "[Subject]", data.subject, { onUpdate(data.copy(subject = it)) }, focusedField, highlightIfEmpty)
    SmartField("relationship", "Relationship", "[Your Relationship to Candidate]", data.relationship, { onUpdate(data.copy(relationship = it)) }, focusedField, highlightIfEmpty)
    
    Text("Candidate's Strengths", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
    data.strengths.forEachIndexed { index, strength ->
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                SmartField("strength_$index", "Strength", "[Strength]", strength, { newStr ->
                    val newList = data.strengths.toMutableList().apply { set(index, newStr) }
                    onUpdate(data.copy(strengths = newList))
                }, focusedField, highlightIfEmpty)
            }
            IconButton(onClick = {
                val newList = data.strengths.toMutableList().apply { removeAt(index) }
                onUpdate(data.copy(strengths = newList))
            }) { Icon(Icons.Default.Delete, contentDescription = "Remove") }
        }
    }
    Button(onClick = { onUpdate(data.copy(strengths = data.strengths + "")) }) { Text("Add Strength") }
    
    SmartField("closing", "Closing", "[Closing]", data.closing, { onUpdate(data.copy(closing = it)) }, focusedField, highlightIfEmpty)
    SmartField("nameAndTitle", "Name & Title", "[Your Name & Title]", data.nameAndTitle, { onUpdate(data.copy(nameAndTitle = it)) }, focusedField, highlightIfEmpty)
}

@Composable
fun ReferenceLetterPreview(data: ReferenceLetterData) {
    Text(data.recipientName.ifBlank { "[Recipient Name]" })
    Text("Subject: ${data.subject.ifBlank { "[Subject]" }}")
    Text("Relationship: ${data.relationship.ifBlank { "[Relationship]" }}")
    data.strengths.forEach { Text("• ${it.ifBlank { "[Strength]" }}") }
    Text(data.closing.ifBlank { "[Closing]" })
    Text(data.nameAndTitle.ifBlank { "[Your Name & Title]" })
}

// Write the other 11 form and previews here...
// Purchase Order
@Composable
fun PurchaseOrderForm(data: PurchaseOrderData, onUpdate: (PurchaseOrderData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("poNumber", "PO Number", "PO-0001", data.poNumber, { onUpdate(data.copy(poNumber = it)) }, focusedField, highlightIfEmpty)
    SmartField("date", "Date", "[Date]", data.date, { onUpdate(data.copy(date = it)) }, focusedField, highlightIfEmpty)
    SmartField("vendorName", "Vendor Name", "[Vendor Name]", data.vendorName, { onUpdate(data.copy(vendorName = it)) }, focusedField, highlightIfEmpty)
    SmartField("shipToAddress", "Ship To Address", "[Ship To Address]", data.shipToAddress, { onUpdate(data.copy(shipToAddress = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
    
    Text("Line Items", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
    data.items.forEachIndexed { index, item ->
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                Column {
                    SmartField("po_desc_$index", "Description", "[Description]", item.desc, { onUpdate(data.copy(items = data.items.toMutableList().apply { set(index, item.copy(desc = it)) })) }, focusedField, highlightIfEmpty)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.weight(1f)) { SmartField("po_qty_$index", "Quantity", "[Qty]", item.quantity, { onUpdate(data.copy(items = data.items.toMutableList().apply { set(index, item.copy(quantity = it)) })) }, focusedField, highlightIfEmpty) }
                        Box(Modifier.weight(1f)) { SmartField("po_price_$index", "Unit Price", "[Price]", item.unitPrice, { onUpdate(data.copy(items = data.items.toMutableList().apply { set(index, item.copy(unitPrice = it)) })) }, focusedField, highlightIfEmpty) }
                        Box(Modifier.weight(1f)) { SmartField("po_sub_$index", "Subtotal", "[Subtotal]", item.subtotal, { onUpdate(data.copy(items = data.items.toMutableList().apply { set(index, item.copy(subtotal = it)) })) }, focusedField, highlightIfEmpty) }
                    }
                }
            }
            IconButton(onClick = { onUpdate(data.copy(items = data.items.toMutableList().apply { removeAt(index) })) }) { Icon(Icons.Default.Delete, contentDescription = "Remove") }
        }
    }
    Button(onClick = { onUpdate(data.copy(items = data.items + PurchaseOrderData.PoItem())) }) { Text("Add Line Item") }
    
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.weight(1f)) { SmartField("subtotal", "Subtotal", "[Subtotal]", data.subtotal, { onUpdate(data.copy(subtotal = it)) }, focusedField, highlightIfEmpty) }
        Box(Modifier.weight(1f)) { SmartField("tax", "Tax", "[Tax]", data.tax, { onUpdate(data.copy(tax = it)) }, focusedField, highlightIfEmpty) }
        Box(Modifier.weight(1f)) { SmartField("total", "Total", "[Total]", data.total, { onUpdate(data.copy(total = it)) }, focusedField, highlightIfEmpty) }
    }
}

@Composable
fun PurchaseOrderPreview(data: PurchaseOrderData) {
    Text("PO Number: ${data.poNumber.ifBlank { "[PO Number]" }}")
    Text("Date: ${data.date.ifBlank { "[Date]" }}")
    Text("Vendor Name: ${data.vendorName.ifBlank { "[Vendor Name]" }}")
    Text("Ship To: ${data.shipToAddress.ifBlank { "[Ship To Address]" }}")
    Divider(Modifier.padding(vertical = 4.dp))
    data.items.forEach { Text("${it.desc.ifBlank { "[Desc]" }} - ${it.quantity.ifBlank { "[Qty]" }} x ${it.unitPrice.ifBlank { "[Price]" }} = ${it.subtotal.ifBlank { "[Subtotal]" }}") }
    Divider(Modifier.padding(vertical = 4.dp))
    Text("Subtotal: ${data.subtotal.ifBlank { "[Subtotal]" }}")
    Text("Tax: ${data.tax.ifBlank { "[Tax]" }}")
    Text("Total: ${data.total.ifBlank { "[Total]" }}")
}

// Quote Data
@Composable
fun QuoteForm(data: QuoteData, onUpdate: (QuoteData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("quoteNumber", "Quote Number", "QT-0001", data.quoteNumber, { onUpdate(data.copy(quoteNumber = it)) }, focusedField, highlightIfEmpty)
    SmartField("date", "Date", "[Date]", data.date, { onUpdate(data.copy(date = it)) }, focusedField, highlightIfEmpty)
    SmartField("validUntil", "Valid Until", "[Valid Until Date]", data.validUntil, { onUpdate(data.copy(validUntil = it)) }, focusedField, highlightIfEmpty)
    SmartField("clientName", "Client Name", "[Client Name]", data.clientName, { onUpdate(data.copy(clientName = it)) }, focusedField, highlightIfEmpty)
    
    Text("Line Items", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
    data.items.forEachIndexed { index, item ->
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                Column {
                    SmartField("qt_desc_$index", "Description", "[Description]", item.desc, { onUpdate(data.copy(items = data.items.toMutableList().apply { set(index, item.copy(desc = it)) })) }, focusedField, highlightIfEmpty)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.weight(1f)) { SmartField("qt_qty_$index", "Quantity", "[Qty]", item.quantity, { onUpdate(data.copy(items = data.items.toMutableList().apply { set(index, item.copy(quantity = it)) })) }, focusedField, highlightIfEmpty) }
                        Box(Modifier.weight(1f)) { SmartField("qt_price_$index", "Unit Price", "[Price]", item.unitPrice, { onUpdate(data.copy(items = data.items.toMutableList().apply { set(index, item.copy(unitPrice = it)) })) }, focusedField, highlightIfEmpty) }
                        Box(Modifier.weight(1f)) { SmartField("qt_total_$index", "Total", "[Total]", item.total, { onUpdate(data.copy(items = data.items.toMutableList().apply { set(index, item.copy(total = it)) })) }, focusedField, highlightIfEmpty) }
                    }
                }
            }
            IconButton(onClick = { onUpdate(data.copy(items = data.items.toMutableList().apply { removeAt(index) })) }) { Icon(Icons.Default.Delete, contentDescription = "Remove") }
        }
    }
    Button(onClick = { onUpdate(data.copy(items = data.items + QuoteData.QuoteItem())) }) { Text("Add Item") }
    
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.weight(1f)) { SmartField("subtotal", "Subtotal", "[Subtotal]", data.subtotal, { onUpdate(data.copy(subtotal = it)) }, focusedField, highlightIfEmpty) }
        Box(Modifier.weight(1f)) { SmartField("discount", "Discount", "[Discount]", data.discount, { onUpdate(data.copy(discount = it)) }, focusedField, highlightIfEmpty) }
        Box(Modifier.weight(1f)) { SmartField("tax", "Tax", "[Tax]", data.tax, { onUpdate(data.copy(tax = it)) }, focusedField, highlightIfEmpty) }
        Box(Modifier.weight(1f)) { SmartField("total", "Total", "[Total]", data.total, { onUpdate(data.copy(total = it)) }, focusedField, highlightIfEmpty) }
    }
    SmartField("notes", "Notes", "[Notes/Terms]", data.notes, { onUpdate(data.copy(notes = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
}

@Composable
fun QuotePreview(data: QuoteData) {
    Text("Quote Number: ${data.quoteNumber.ifBlank { "[Quote Number]" }}")
    Text("Date: ${data.date.ifBlank { "[Date]" }}")
    Text("Valid Until: ${data.validUntil.ifBlank { "[Valid Until]" }}")
    Text("Client Name: ${data.clientName.ifBlank { "[Client Name]" }}")
    Divider(Modifier.padding(vertical = 4.dp))
    data.items.forEach { Text("${it.desc.ifBlank { "[Desc]" }} - ${it.quantity.ifBlank { "[Qty]" }} x ${it.unitPrice.ifBlank { "[Price]" }} = ${it.total.ifBlank { "[Total]" }}") }
    Divider(Modifier.padding(vertical = 4.dp))
    Text("Subtotal: ${data.subtotal.ifBlank { "[Subtotal]" }}")
    Text("Discount: ${data.discount.ifBlank { "[Discount]" }}")
    Text("Tax: ${data.tax.ifBlank { "[Tax]" }}")
    Text("Total: ${data.total.ifBlank { "[Total]" }}")
    Text("Notes: ${data.notes.ifBlank { "[Notes]" }}")
}

// NDA
@Composable
fun NdaForm(data: NdaData, onUpdate: (NdaData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("disclosingParty", "Disclosing Party", "[Disclosing Party Name]", data.disclosingParty, { onUpdate(data.copy(disclosingParty = it)) }, focusedField, highlightIfEmpty)
    SmartField("receivingParty", "Receiving Party", "[Receiving Party Name]", data.receivingParty, { onUpdate(data.copy(receivingParty = it)) }, focusedField, highlightIfEmpty)
    SmartField("effectiveDate", "Effective Date", "[Effective Date]", data.effectiveDate, { onUpdate(data.copy(effectiveDate = it)) }, focusedField, highlightIfEmpty)
    SmartField("purpose", "Purpose", "[Purpose of NDA]", data.purpose, { onUpdate(data.copy(purpose = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
    SmartField("duration", "Duration of Confidentiality", "[e.g., 2 years]", data.duration, { onUpdate(data.copy(duration = it)) }, focusedField, highlightIfEmpty)
    SmartField("signatureName", "Signature Name", "[Printed Name]", data.signatureName, { onUpdate(data.copy(signatureName = it)) }, focusedField, highlightIfEmpty)
}

@Composable
fun NdaPreview(data: NdaData) {
    Text("NON-DISCLOSURE AGREEMENT", style = MaterialTheme.typography.titleLarge)
    Text("This NDA is between ${data.disclosingParty.ifBlank { "[Disclosing Party]" }} and ${data.receivingParty.ifBlank { "[Receiving Party]" }}, effective ${data.effectiveDate.ifBlank { "[Effective Date]" }}.")
    Text("Purpose: ${data.purpose.ifBlank { "[Purpose]" }}")
    Text("Duration: ${data.duration.ifBlank { "[Duration]" }}")
    Spacer(modifier = Modifier.height(16.dp))
    Text("Signed: __________________\nName: ${data.signatureName.ifBlank { "[Printed Name]" }}")
}

// Timesheet
@Composable
fun TimesheetForm(data: TimesheetData, onUpdate: (TimesheetData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("employeeName", "Employee Name", "[Employee Name]", data.employeeName, { onUpdate(data.copy(employeeName = it)) }, focusedField, highlightIfEmpty)
    SmartField("weekStarting", "Week Starting", "[Date]", data.weekStarting, { onUpdate(data.copy(weekStarting = it)) }, focusedField, highlightIfEmpty)
    
    Text("Entries", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
    data.entries.forEachIndexed { index, item ->
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.weight(1f)) { SmartField("ts_date_$index", "Date", "[Date]", item.date, { onUpdate(data.copy(entries = data.entries.toMutableList().apply { set(index, item.copy(date = it)) })) }, focusedField, highlightIfEmpty) }
                        Box(Modifier.weight(1f)) { SmartField("ts_hours_$index", "Hours", "[Hours]", item.hours, { onUpdate(data.copy(entries = data.entries.toMutableList().apply { set(index, item.copy(hours = it)) })) }, focusedField, highlightIfEmpty) }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.weight(1f)) { SmartField("ts_proj_$index", "Project/Task", "[Task]", item.project, { onUpdate(data.copy(entries = data.entries.toMutableList().apply { set(index, item.copy(project = it)) })) }, focusedField, highlightIfEmpty) }
                        Box(Modifier.weight(1f)) { SmartField("ts_note_$index", "Notes", "[Notes]", item.notes, { onUpdate(data.copy(entries = data.entries.toMutableList().apply { set(index, item.copy(notes = it)) })) }, focusedField, highlightIfEmpty) }
                    }
                }
            }
            IconButton(onClick = { onUpdate(data.copy(entries = data.entries.toMutableList().apply { removeAt(index) })) }) { Icon(Icons.Default.Delete, contentDescription = "Remove") }
        }
    }
    Button(onClick = { onUpdate(data.copy(entries = data.entries + TimesheetData.TsEntry())) }) { Text("Add Entry") }
    
    SmartField("totalHours", "Total Hours", "[Total Hours]", data.totalHours, { onUpdate(data.copy(totalHours = it)) }, focusedField, highlightIfEmpty)
    SmartField("approvalSignature", "Approval", "[Manager Name/Signature]", data.approvalSignature, { onUpdate(data.copy(approvalSignature = it)) }, focusedField, highlightIfEmpty)
}

@Composable
fun TimesheetPreview(data: TimesheetData) {
    Text("Employee: ${data.employeeName.ifBlank { "[Employee Name]" }} | Week: ${data.weekStarting.ifBlank { "[Week Starting]" }}")
    Divider(Modifier.padding(vertical = 4.dp))
    data.entries.forEach { Text("${it.date.ifBlank { "[Date]" }} - ${it.project.ifBlank { "[Project]" }} - ${it.hours.ifBlank { "[Hours]" }} hrs - ${it.notes.ifBlank { "[Notes]" }}") }
    Divider(Modifier.padding(vertical = 4.dp))
    Text("Total Hours: ${data.totalHours.ifBlank { "[Total Hours]" }}")
    Spacer(Modifier.height(8.dp))
    Text("Approved by: __________________\n${data.approvalSignature.ifBlank { "[Approval Name]" }}")
}

// Expense Report
@Composable
fun ExpenseReportForm(data: ExpenseReportData, onUpdate: (ExpenseReportData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("employeeName", "Employee Name", "[Employee Name]", data.employeeName, { onUpdate(data.copy(employeeName = it)) }, focusedField, highlightIfEmpty)
    SmartField("date", "Date", "[Date]", data.date, { onUpdate(data.copy(date = it)) }, focusedField, highlightIfEmpty)
    SmartField("department", "Department", "[Department]", data.department, { onUpdate(data.copy(department = it)) }, focusedField, highlightIfEmpty)
    
    Text("Expenses", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
    data.entries.forEachIndexed { index, item ->
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.weight(1f)) { SmartField("ex_date_$index", "Date", "[Date]", item.date, { onUpdate(data.copy(entries = data.entries.toMutableList().apply { set(index, item.copy(date = it)) })) }, focusedField, highlightIfEmpty) }
                        Box(Modifier.weight(1f)) { SmartField("ex_cat_$index", "Category", "[Category]", item.category, { onUpdate(data.copy(entries = data.entries.toMutableList().apply { set(index, item.copy(category = it)) })) }, focusedField, highlightIfEmpty) }
                        Box(Modifier.weight(1f)) { SmartField("ex_amt_$index", "Amount", "[Amount]", item.amount, { onUpdate(data.copy(entries = data.entries.toMutableList().apply { set(index, item.copy(amount = it)) })) }, focusedField, highlightIfEmpty) }
                    }
                    SmartField("ex_desc_$index", "Description", "[Description]", item.description, { onUpdate(data.copy(entries = data.entries.toMutableList().apply { set(index, item.copy(description = it)) })) }, focusedField, highlightIfEmpty)
                    SmartField("ex_rec_$index", "Receipt Attached?", "[Yes/No]", item.receipt, { onUpdate(data.copy(entries = data.entries.toMutableList().apply { set(index, item.copy(receipt = it)) })) }, focusedField, highlightIfEmpty)
                }
            }
            IconButton(onClick = { onUpdate(data.copy(entries = data.entries.toMutableList().apply { removeAt(index) })) }) { Icon(Icons.Default.Delete, contentDescription = "Remove") }
        }
    }
    Button(onClick = { onUpdate(data.copy(entries = data.entries + ExpenseReportData.ExEntry())) }) { Text("Add Expense") }
    
    SmartField("totalReimbursement", "Total Reimbursement", "[Total Reimbursement]", data.totalReimbursement, { onUpdate(data.copy(totalReimbursement = it)) }, focusedField, highlightIfEmpty)
}

@Composable
fun ExpenseReportPreview(data: ExpenseReportData) {
    Text("Employee: ${data.employeeName.ifBlank { "[Name]" }} | Date: ${data.date.ifBlank { "[Date]" }} | Dept: ${data.department.ifBlank { "[Dept]" }}")
    Divider(Modifier.padding(vertical = 4.dp))
    data.entries.forEach { Text("${it.date.ifBlank { "[Date]" }} - ${it.category.ifBlank { "[Cat]" }}: ${it.amount.ifBlank { "[Amt]" }}\n  Desc: ${it.description.ifBlank { "[Desc]" }} | Receipt: ${it.receipt.ifBlank { "[Yes/No]" }}") }
    Divider(Modifier.padding(vertical = 4.dp))
    Text("Total Reimbursement: ${data.totalReimbursement.ifBlank { "[Total Reimbursement]" }}")
}

// Press Release
@Composable
fun PressReleaseForm(data: PressReleaseData, onUpdate: (PressReleaseData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("embargoDate", "Release Timing", "[For Immediate Release or Embargo Date]", data.embargoDate, { onUpdate(data.copy(embargoDate = it)) }, focusedField, highlightIfEmpty)
    SmartField("headline", "Headline", "[Headline]", data.headline, { onUpdate(data.copy(headline = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
    SmartField("dateline", "Dateline", "[City, State - Date]", data.dateline, { onUpdate(data.copy(dateline = it)) }, focusedField, highlightIfEmpty)
    SmartField("body", "Body", "[Body Paragraphs]", data.body, { onUpdate(data.copy(body = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
    SmartField("boilerplate", "About Company", "[Company Boilerplate]", data.boilerplate, { onUpdate(data.copy(boilerplate = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
    SmartField("mediaContact", "Media Contact", "[Name, Email, Phone]", data.mediaContact, { onUpdate(data.copy(mediaContact = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
}

@Composable
fun PressReleasePreview(data: PressReleaseData) {
    Text(data.embargoDate.ifBlank { "[For Immediate Release]" }, style = MaterialTheme.typography.labelMedium)
    Spacer(Modifier.height(8.dp))
    Text(data.headline.ifBlank { "[Headline]" }, style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))
    Text("${data.dateline.ifBlank { "[City, Date]" }} -- ${data.body.ifBlank { "[Body]" }}")
    Spacer(Modifier.height(16.dp))
    Text(data.boilerplate.ifBlank { "[Boilerplate]" })
    Spacer(Modifier.height(8.dp))
    Text("Media Contact: \n${data.mediaContact.ifBlank { "[Contact Info]" }}")
}

// Memo
@Composable
fun MemoForm(data: MemoData, onUpdate: (MemoData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("to", "To", "[To]", data.to, { onUpdate(data.copy(to = it)) }, focusedField, highlightIfEmpty)
    SmartField("from", "From", "[From]", data.from, { onUpdate(data.copy(from = it)) }, focusedField, highlightIfEmpty)
    SmartField("date", "Date", "[Date]", data.date, { onUpdate(data.copy(date = it)) }, focusedField, highlightIfEmpty)
    SmartField("subject", "Subject", "[Subject]", data.subject, { onUpdate(data.copy(subject = it)) }, focusedField, highlightIfEmpty)
    SmartField("cc", "CC", "[CC]", data.cc, { onUpdate(data.copy(cc = it)) }, focusedField, highlightIfEmpty)
    SmartField("body", "Body", "[Message Body]", data.body, { onUpdate(data.copy(body = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
}

@Composable
fun MemoPreview(data: MemoData) {
    Text("MEMORANDUM", style = MaterialTheme.typography.titleLarge)
    Divider()
    Text("TO: ${data.to.ifBlank { "[To]" }}")
    Text("FROM: ${data.from.ifBlank { "[From]" }}")
    Text("DATE: ${data.date.ifBlank { "[Date]" }}")
    Text("SUBJECT: ${data.subject.ifBlank { "[Subject]" }}")
    Text("CC: ${data.cc.ifBlank { "[CC]" }}")
    Divider()
    Text(data.body.ifBlank { "[Memo Body]" })
}

// Thank You Letter
@Composable
fun ThankYouLetterForm(data: ThankYouLetterData, onUpdate: (ThankYouLetterData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("date", "Date", "[Date]", data.date, { onUpdate(data.copy(date = it)) }, focusedField, highlightIfEmpty)
    SmartField("recipientName", "Recipient Name", "[Recipient Name]", data.recipientName, { onUpdate(data.copy(recipientName = it)) }, focusedField, highlightIfEmpty)
    SmartField("reason", "Thank You For", "[Reason]", data.reason, { onUpdate(data.copy(reason = it)) }, focusedField, highlightIfEmpty)
    SmartField("specificDetail", "Specific Detail", "[Detail]", data.specificDetail, { onUpdate(data.copy(specificDetail = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
    SmartField("closing", "Closing", "[Closing]", data.closing, { onUpdate(data.copy(closing = it)) }, focusedField, highlightIfEmpty)
    SmartField("yourName", "Your Name", "[Your Name]", data.yourName, { onUpdate(data.copy(yourName = it)) }, focusedField, highlightIfEmpty)
}

@Composable
fun ThankYouLetterPreview(data: ThankYouLetterData) {
    Text(data.date.ifBlank { "[Date]" })
    Spacer(Modifier.height(8.dp))
    Text("Dear ${data.recipientName.ifBlank { "[Recipient Name]" }},")
    Spacer(Modifier.height(8.dp))
    Text("Thank you so much for ${data.reason.ifBlank { "[reason]" }}. I really appreciate ${data.specificDetail.ifBlank { "[specific detail]" }}.")
    Spacer(Modifier.height(8.dp))
    Text(data.closing.ifBlank { "[Closing]" })
    Text(data.yourName.ifBlank { "[Your Name]" })
}

// Acceptance Letter
@Composable
fun AcceptanceLetterForm(data: AcceptanceLetterData, onUpdate: (AcceptanceLetterData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("date", "Date", "[Date]", data.date, { onUpdate(data.copy(date = it)) }, focusedField, highlightIfEmpty)
    SmartField("hiringManagerName", "Hiring Manager Name", "[Name]", data.hiringManagerName, { onUpdate(data.copy(hiringManagerName = it)) }, focusedField, highlightIfEmpty)
    SmartField("companyName", "Company Name", "[Company]", data.companyName, { onUpdate(data.copy(companyName = it)) }, focusedField, highlightIfEmpty)
    SmartField("jobTitle", "Job Title", "[Job Title]", data.jobTitle, { onUpdate(data.copy(jobTitle = it)) }, focusedField, highlightIfEmpty)
    SmartField("declaration", "Declaration", "[I accept the offer...]", data.declaration, { onUpdate(data.copy(declaration = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
    SmartField("startDate", "Start Date", "[Start Date]", data.startDate, { onUpdate(data.copy(startDate = it)) }, focusedField, highlightIfEmpty)
    SmartField("signatureLine", "Signature", "[Signature/Name]", data.signatureLine, { onUpdate(data.copy(signatureLine = it)) }, focusedField, highlightIfEmpty)
}

@Composable
fun AcceptanceLetterPreview(data: AcceptanceLetterData) {
    Text(data.date.ifBlank { "[Date]" })
    Text("Dear ${data.hiringManagerName.ifBlank { "[Hiring Manager Name]" }},")
    Spacer(Modifier.height(8.dp))
    Text("It is with great pleasure that I accept your offer to join ${data.companyName.ifBlank { "[Company Name]" }} as a ${data.jobTitle.ifBlank { "[Job Title]" }}. ${data.declaration.ifBlank { "[Declaration text]" }}")
    Text("I look forward to starting on ${data.startDate.ifBlank { "[Start Date]" }}.")
    Spacer(Modifier.height(8.dp))
    Text("Sincerely,")
    Text(data.signatureLine.ifBlank { "[Signature]" })
}

// Termination Letter
@Composable
fun TerminationLetterForm(data: TerminationLetterData, onUpdate: (TerminationLetterData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("date", "Date", "[Date]", data.date, { onUpdate(data.copy(date = it)) }, focusedField, highlightIfEmpty)
    SmartField("employeeName", "Employee Name", "[Employee Name]", data.employeeName, { onUpdate(data.copy(employeeName = it)) }, focusedField, highlightIfEmpty)
    SmartField("position", "Position", "[Position]", data.position, { onUpdate(data.copy(position = it)) }, focusedField, highlightIfEmpty)
    SmartField("lastDay", "Last Day", "[Last Day of Employment]", data.lastDay, { onUpdate(data.copy(lastDay = it)) }, focusedField, highlightIfEmpty)
    SmartField("reason", "Reason (Optional)", "[Reason for termination]", data.reason, { onUpdate(data.copy(reason = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
    SmartField("returnOfProperty", "Return of Property", "[Instructions on returning property]", data.returnOfProperty, { onUpdate(data.copy(returnOfProperty = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
    SmartField("signatureEmployer", "Employer Signature", "[Signature of Employer]", data.signatureEmployer, { onUpdate(data.copy(signatureEmployer = it)) }, focusedField, highlightIfEmpty)
}

@Composable
fun TerminationLetterPreview(data: TerminationLetterData) {
    Text(data.date.ifBlank { "[Date]" })
    Text("To: ${data.employeeName.ifBlank { "[Employee Name]" }}")
    Spacer(Modifier.height(8.dp))
    Text("This letter serves as formal notice that your employment as ${data.position.ifBlank { "[Position]" }} will be terminated, effective ${data.lastDay.ifBlank { "[Last Day]" }}.")
    if (data.reason.isNotBlank()) Text("Reason: ${data.reason}")
    Text("Property Return: ${data.returnOfProperty.ifBlank { "[Return Instructions]" }}")
    Spacer(Modifier.height(8.dp))
    Text("Signed,")
    Text(data.signatureEmployer.ifBlank { "[Employer Signature]" })
}

// Performance Review
@Composable
fun PerformanceReviewForm(data: PerformanceReviewData, onUpdate: (PerformanceReviewData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("employeeName", "Employee Name", "[Employee Name]", data.employeeName, { onUpdate(data.copy(employeeName = it)) }, focusedField, highlightIfEmpty)
    SmartField("reviewPeriod", "Review Period", "[Review Period]", data.reviewPeriod, { onUpdate(data.copy(reviewPeriod = it)) }, focusedField, highlightIfEmpty)
    SmartField("reviewerName", "Reviewer Name", "[Reviewer Name]", data.reviewerName, { onUpdate(data.copy(reviewerName = it)) }, focusedField, highlightIfEmpty)
    SmartField("ratingsScale", "Overall Rating", "[Overall Rating]", data.ratingsScale, { onUpdate(data.copy(ratingsScale = it)) }, focusedField, highlightIfEmpty)
    
    Text("Strengths", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
    data.strengths.forEachIndexed { index, str ->
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) { SmartField("pr_str_$index", "Strength", "[Strength]", str, { onUpdate(data.copy(strengths = data.strengths.toMutableList().apply { set(index, it) })) }, focusedField, highlightIfEmpty) }
            IconButton(onClick = { onUpdate(data.copy(strengths = data.strengths.toMutableList().apply { removeAt(index) })) }) { Icon(Icons.Default.Delete, contentDescription = "Remove") }
        }
    }
    Button(onClick = { onUpdate(data.copy(strengths = data.strengths + "")) }) { Text("Add Strength") }
    
    Text("Areas for Improvement", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
    data.areasForImprovement.forEachIndexed { index, str ->
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) { SmartField("pr_imp_$index", "Improvement", "[Improvement]", str, { onUpdate(data.copy(areasForImprovement = data.areasForImprovement.toMutableList().apply { set(index, it) })) }, focusedField, highlightIfEmpty) }
            IconButton(onClick = { onUpdate(data.copy(areasForImprovement = data.areasForImprovement.toMutableList().apply { removeAt(index) })) }) { Icon(Icons.Default.Delete, contentDescription = "Remove") }
        }
    }
    Button(onClick = { onUpdate(data.copy(areasForImprovement = data.areasForImprovement + "")) }) { Text("Add Improvement") }
    
    SmartField("goals", "Goals", "[Goals]", data.goals, { onUpdate(data.copy(goals = it)) }, focusedField, highlightIfEmpty, isMultiline = true)
    SmartField("employeeSignature", "Employee Signature", "[Signature]", data.employeeSignature, { onUpdate(data.copy(employeeSignature = it)) }, focusedField, highlightIfEmpty)
    SmartField("managerSignature", "Manager Signature", "[Signature]", data.managerSignature, { onUpdate(data.copy(managerSignature = it)) }, focusedField, highlightIfEmpty)
}

@Composable
fun PerformanceReviewPreview(data: PerformanceReviewData) {
    Text("PERFORMANCE REVIEW", style = MaterialTheme.typography.titleLarge)
    Text("Employee: ${data.employeeName.ifBlank { "[Employee Name]" }} | Period: ${data.reviewPeriod.ifBlank { "[Period]" }}")
    Text("Reviewer: ${data.reviewerName.ifBlank { "[Reviewer]" }} | Rating: ${data.ratingsScale.ifBlank { "[Rating]" }}")
    Divider(Modifier.padding(vertical = 4.dp))
    Text("Strengths:")
    data.strengths.forEach { Text("• ${it.ifBlank { "[Strength]" }}") }
    Text("Improvements:")
    data.areasForImprovement.forEach { Text("• ${it.ifBlank { "[Improvement]" }}") }
    Text("Goals: ${data.goals.ifBlank { "[Goals]" }}")
    Spacer(Modifier.height(16.dp))
    Text("Signatures:")
    Text("Emp: ${data.employeeSignature.ifBlank { "[Emp Sig]" }} | Mgr: ${data.managerSignature.ifBlank { "[Mgr Sig]" }}")
}

@Composable
fun RenderDynamicForm(selectedTab: Int, viewModel: DocumentViewModel, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    val dynamicJsons by viewModel.activeDynamicJsons.collectAsState()
    val customDocs by viewModel.activeCustomDocs.collectAsState()
    
    val json = dynamicJsons[selectedTab] ?: ""
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        when (selectedTab) {
            11 -> ReferenceLetterForm(ReferenceLetterData.fromJson(json), { viewModel.updateDynamicJson(11, it.toJson()) }, focusedField, highlightIfEmpty)
            12 -> PurchaseOrderForm(PurchaseOrderData.fromJson(json), { viewModel.updateDynamicJson(12, it.toJson()) }, focusedField, highlightIfEmpty)
            13 -> QuoteForm(QuoteData.fromJson(json), { viewModel.updateDynamicJson(13, it.toJson()) }, focusedField, highlightIfEmpty)
            14 -> NdaForm(NdaData.fromJson(json), { viewModel.updateDynamicJson(14, it.toJson()) }, focusedField, highlightIfEmpty)
            15 -> TimesheetForm(TimesheetData.fromJson(json), { viewModel.updateDynamicJson(15, it.toJson()) }, focusedField, highlightIfEmpty)
            16 -> ExpenseReportForm(ExpenseReportData.fromJson(json), { viewModel.updateDynamicJson(16, it.toJson()) }, focusedField, highlightIfEmpty)
            17 -> PressReleaseForm(PressReleaseData.fromJson(json), { viewModel.updateDynamicJson(17, it.toJson()) }, focusedField, highlightIfEmpty)
            18 -> MemoForm(MemoData.fromJson(json), { viewModel.updateDynamicJson(18, it.toJson()) }, focusedField, highlightIfEmpty)
            19 -> ThankYouLetterForm(ThankYouLetterData.fromJson(json), { viewModel.updateDynamicJson(19, it.toJson()) }, focusedField, highlightIfEmpty)
            20 -> AcceptanceLetterForm(AcceptanceLetterData.fromJson(json), { viewModel.updateDynamicJson(20, it.toJson()) }, focusedField, highlightIfEmpty)
            21 -> TerminationLetterForm(TerminationLetterData.fromJson(json), { viewModel.updateDynamicJson(21, it.toJson()) }, focusedField, highlightIfEmpty)
            22 -> PerformanceReviewForm(PerformanceReviewData.fromJson(json), { viewModel.updateDynamicJson(22, it.toJson()) }, focusedField, highlightIfEmpty)
            23 -> CustomDocumentForm(customDocs[23] ?: CustomDocumentData(templateId=23), { viewModel.updateCustomDoc(23) { _ -> it } }, focusedField, highlightIfEmpty)
        }
    }
}

@Composable
fun RenderDynamicPreview(selectedTab: Int, viewModel: DocumentViewModel, activePaperColor: androidx.compose.ui.graphics.Color) {
    val dynamicJsons by viewModel.activeDynamicJsons.collectAsState()
    val customDocs by viewModel.activeCustomDocs.collectAsState()
    val json = dynamicJsons[selectedTab] ?: ""
    
    PaperCanvas(paperColor = activePaperColor) {
        when (selectedTab) {
            11 -> ReferenceLetterPreview(ReferenceLetterData.fromJson(json))
            12 -> PurchaseOrderPreview(PurchaseOrderData.fromJson(json))
            13 -> QuotePreview(QuoteData.fromJson(json))
            14 -> NdaPreview(NdaData.fromJson(json))
            15 -> TimesheetPreview(TimesheetData.fromJson(json))
            16 -> ExpenseReportPreview(ExpenseReportData.fromJson(json))
            17 -> PressReleasePreview(PressReleaseData.fromJson(json))
            18 -> MemoPreview(MemoData.fromJson(json))
            19 -> ThankYouLetterPreview(ThankYouLetterData.fromJson(json))
            20 -> AcceptanceLetterPreview(AcceptanceLetterData.fromJson(json))
            21 -> TerminationLetterPreview(TerminationLetterData.fromJson(json))
            22 -> PerformanceReviewPreview(PerformanceReviewData.fromJson(json))
            23 -> CustomDocumentPreview(customDocs[23] ?: CustomDocumentData(templateId=23))
        }
    }
}


@Composable
fun UserProfileDialog(viewModel: DocumentViewModel, onDismiss: () -> Unit) {
    val profile by viewModel.userProfile.collectAsState()
    val focusedField = remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(16.dp).fillMaxWidth().heightIn(max=600.dp)) {
                Text("Your Saved Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("This profile is automatically applied/suggested across all documents.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(Modifier.height(16.dp))
                LazyColumn(Modifier.weight(1f, fill=false)) {
                    item {
                        SmartField("pro_name", "Name", "e.g. John Doe", profile.name, { viewModel.saveUserProfile(profile.copy(name = it)) }, focusedField, false)
                        SmartField("pro_company", "Company Name", "e.g. Acme Corp", profile.companyName, { viewModel.saveUserProfile(profile.copy(companyName = it)) }, focusedField, false)
                        SmartField("pro_address", "Address", "e.g. 123 Main St", profile.address, { viewModel.saveUserProfile(profile.copy(address = it)) }, focusedField, false, isMultiline = true)
                        SmartField("pro_phone", "Phone", "e.g. +1 555-0100", profile.phone, { viewModel.saveUserProfile(profile.copy(phone = it)) }, focusedField, false)
                        SmartField("pro_email", "Email", "e.g. john@example.com", profile.email, { viewModel.saveUserProfile(profile.copy(email = it)) }, focusedField, false)
                        SmartField("pro_tax", "Tax ID", "e.g. AB1234567", profile.taxId, { viewModel.saveUserProfile(profile.copy(taxId = it)) }, focusedField, false)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close & Save")
                }
            }
        }
    }
}

// Custom Form
@Composable
fun CustomDocumentForm(data: CustomDocumentData, onUpdate: (CustomDocumentData) -> Unit, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    SmartField("title", "Document Title", "[Document Title]", data.title, { onUpdate(data.copy(title = it)) }, focusedField, highlightIfEmpty)
    
    Text("Custom Fields", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
    data.customFieldLabels.forEachIndexed { index, label ->
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                SmartField("custom_label_$index", "Field Label", "e.g. Subtitle", label, { newLabel ->
                    onUpdate(data.copy(customFieldLabels = data.customFieldLabels.toMutableList().apply { set(index, newLabel) }))
                }, focusedField, false)
            }
            Box(Modifier.weight(1f)) {
                val value = data.fields[label] ?: ""
                SmartField("custom_val_$index", label.ifBlank { "Value" }, "[Value]", value, { newVal ->
                    onUpdate(data.copy(fields = data.fields.toMutableMap().apply { put(label, newVal) }))
                }, focusedField, highlightIfEmpty)
            }
            IconButton(onClick = {
                onUpdate(data.copy(
                    customFieldLabels = data.customFieldLabels.toMutableList().apply { removeAt(index) },
                    fields = data.fields.toMutableMap().apply { remove(label) }
                ))
            }) { Icon(Icons.Default.Delete, contentDescription = "Remove") }
        }
    }
    Button(onClick = { onUpdate(data.copy(customFieldLabels = data.customFieldLabels + "")) }) { Text("Add Field") }
}

@Composable
fun CustomDocumentPreview(data: CustomDocumentData) {
    Text(data.title.ifBlank { "[Custom Document]" }, style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(16.dp))
    data.customFieldLabels.forEach { label ->
        if (label.isNotBlank()) {
            Text("$label: ${data.fields[label]?.ifBlank { "[$label]" } ?: "[$label]"}")
        }
    }
}
