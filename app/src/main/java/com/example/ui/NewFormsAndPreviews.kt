package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
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
fun GenericDocumentPreview(selectedTab: Int, data: GenericDocumentData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = data.title.ifBlank { "Document Preview" },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        data.fields.forEach { (key, value) ->
            if (value.isNotBlank() && key != "title") {
                val label = when (key) {
                    "recipientName" -> "Recipient Name"
                    "salutation" -> "Salutation"
                    "message" -> "Personal Message"
                    "closing" -> "Closing"
                    "senderName" -> "Sender Name"
                    "hostName" -> "Host / Organizer Name"
                    "eventType" -> "Event Name / Type"
                    "dateTime" -> "Date & Time"
                    "location" -> "Location / Venue"
                    "rsvpContact" -> "RSVP Contact Details"
                    "specialNotes" -> "Special Instructions"
                    "reason" -> "Reason"
                    "resolution" -> "Resolution Plan"
                    "deceasedName" -> "Deceased Person Name"
                    "memories" -> "Fond Memories / Condolence"
                    "supportOffer" -> "Offer of Support"
                    "achievement" -> "Achievement Details"
                    "pieceTitle" -> "Title"
                    "authorName" -> "Author Name"
                    "genre" -> "Genre / Tone"
                    "content" -> "Content Body Text"
                    "date" -> "Date / Stamp"
                    "mood" -> "Mood & Perspective"
                    "reflection" -> "Personal Journal Reflection"
                    "gratefulFor" -> "Grateful Elements"
                    "listTitle" -> "Objective / Title"
                    "dueDate" -> "Target Due Date"
                    "storeName" -> "Target Store Name"
                    "recipeName" -> "Recipe Name"
                    "prepTime" -> "Prep & Cook Time"
                    "servings" -> "Yield & Servings"
                    "instructions" -> "Preparation Steps"
                    "recipient" -> "Recipient Name"
                    "gift" -> "Occasion / Gift Description"
                    "warmMessage" -> "Personal Greeting Message"
                    "sender" -> "Sender Identity"
                    "eventName" -> "Event Name"
                    "responderName" -> "Guest / Responder"
                    "acceptStatus" -> "RSVP Status"
                    "dietaryNeeds" -> "Dietary / Accommodations"
                    "petitionTitle" -> "Petition Title"
                    "targetAuthority" -> "Target Governing Authority"
                    "statement" -> "Petition Accords & Statement"
                    "initiatorName" -> "Prime Initiator Contact"
                    "issueDate" -> "Date of Occurrence"
                    "problemDescription" -> "Problem Report"
                    "desiredOutcome" -> "Expected Resolution"
                    "senderContact" -> "Your Contact Info"
                    "refereeName" -> "Professional Referee Name"
                    "purpose" -> "Applying Program/Agency"
                    "deadline" -> "Filing Deadline"
                    "keyAchievements" -> "Key Points"
                    "employeeName" -> "Associate"
                    "acknowledgedDate" -> "Docket Date"
                    "lastDay" -> "Severance Date"
                    "transitionNotes" -> "Transition Directives"
                    "managerName" -> "Manager Signature Authority"
                    "issuedTo" -> "Subject Party"
                    "dateIssued" -> "Issuance Date"
                    "violation" -> "Infraction Details"
                    "consequences" -> "Corrective Measures"
                    "issuedBy" -> "Supervisor Title"
                    "presenter" -> "Host Presenter"
                    "headerText" -> "Display Headline"
                    "details" -> "Specifications"
                    "time" -> "Time Period"
                    "contactInfo" -> "Contact Authority"
                    "testatorName" -> "Your Full Name"
                    "executor" -> "Appointed Executor"
                    "bequests" -> "Bequests"
                    "witnesses" -> "Witness Signature"
                    "landlord" -> "Lessor (Landlord)"
                    "tenant" -> "Lessee (Tenant)"
                    "address" -> "Real Estate Address"
                    "rentAmount" -> "Rental Consideration"
                    "terms" -> "Lease Terms & Covenants"
                    "seller" -> "Seller Name"
                    "buyer" -> "Buyer Name"
                    "itemDescription" -> "Sale Asset"
                    "price" -> "Purchase Price"
                    "warrantyDetails" -> "Warranty Terms"
                    "borrower" -> "Borrower"
                    "lender" -> "Lender"
                    "principalAmount" -> "Debt Principal"
                    "interestRate" -> "Interest Rate"
                    "repaymentSchedule" -> "Repayment Plan"
                    else -> if (key.isNotEmpty()) key[0].uppercaseChar() + key.substring(1) else ""
                }
                
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
        
        if (data.listItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Checklist Elements:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            data.listItems.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("☑  ", fontWeight = FontWeight.Bold)
                    Text(text = item, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

fun getDefaultGenericDoc(selectedTab: Int): GenericDocumentData {
    return when (selectedTab) {
        23 -> GenericDocumentData(
            title = "Love Letter",
            fields = mapOf(
                "recipientName" to "My Beloved",
                "message" to "My heart beats only for you. Every moment apart feels like an eternity.",
                "closing" to "Forever yours",
                "senderName" to "Your Devoted Partner"
            )
        )
        24 -> GenericDocumentData(
            title = "Event Invitation",
            fields = mapOf(
                "hostName" to "The Shah Family",
                "eventType" to "Summer Celebration Party",
                "dateTime" to "Saturday, July 18th at 6:00 PM",
                "location" to "The Grand Garden Pavilions, District 5",
                "rsvpContact" to "Rahul at 555-0199",
                "specialNotes" to "Please wear elegant pastel attire. Valet parking provided."
            )
        )
        25 -> GenericDocumentData(
            title = "Apology Letter",
            fields = mapOf(
                "recipientName" to "Valued Client",
                "salutation" to "Dear Business Partner",
                "reason" to "the service interruptions during software maintenance on June 5th",
                "resolution" to "We have upgraded our secondary backup servers and instituted real-time health alerts to prevent any future occurrences.",
                "closing" to "Deeply regretful",
                "senderName" to "Rahul Shah, Director of Operations"
            )
        )
        26 -> GenericDocumentData(
            title = "Condolence & Sympathy Letter",
            fields = mapOf(
                "recipientName" to "The Henderson Family",
                "deceasedName" to "Uncle Robert",
                "memories" to "He was exceptionally generous and brought laughter to everyone in the room. His bright spirit will never be forgotten.",
                "supportOffer" to "Please let us know if we can assist you with meals, housekeeping, or anything else during this hard transition.",
                "closing" to "With heartfelt condolences",
                "senderName" to "Rahul and Family"
            )
        )
        27 -> GenericDocumentData(
            title = "Congratulations Letter",
            fields = mapOf(
                "recipientName" to "Sarah Jenkins",
                "achievement" to "being promoted to Lead Software Engineer",
                "message" to "Your focus, exceptional problem-solving skills, and deep engineering design expertise made this a well-deserved recognition.",
                "closing" to "Wishing you absolute success",
                "senderName" to "Rahul Shah, engineering partner"
            )
        )
        28 -> GenericDocumentData(
            title = "Poem / Short Story Template",
            fields = mapOf(
                "pieceTitle" to "Echoes of Tomorrow",
                "authorName" to "Rahul Shah",
                "genre" to "Philosophical Sci-Fi Poem",
                "content" to "Silhouettes fade in the digital stream,\nEchoes of choices in an ancient dream.\nWe write the future line by line,\nBridging the binary and the divine."
            )
        )
        29 -> GenericDocumentData(
            title = "Personal Journal Entry",
            fields = mapOf(
                "date" to "2026-06-06",
                "mood" to "Productive & Creative",
                "reflection" to "Reflecting on visual aesthetics today. A clean, spacious Material 3 layout with dark tones brings so much focus and peace.",
                "gratefulFor" to "Having the capability to build and code elegant tools that help people create."
            )
        )
        30 -> GenericDocumentData(
            title = "My Daily To-Do List",
            fields = mapOf(
                "listTitle" to "Workplace Launch Checklist",
                "dueDate" to "Today before COB"
            ),
            listItems = listOf(
                "Review document layout export engine",
                "Install adaptive custom launcher icons",
                "Align search bar at the very top of drawer UI",
                "Run test suite to verify UI compilation"
            )
        )
        31 -> GenericDocumentData(
            title = "Shopping Checklist",
            fields = mapOf(
                "listTitle" to "Launch Celebration Dinner Ingredients",
                "storeName" to "Fresh Foods Market"
            ),
            listItems = listOf(
                "Fresh Basil Leaves",
                "Heirloom Cherry Tomatoes",
                "Aged Balsamic Glaze",
                "Organic Jasmine Rice"
            )
        )
        32 -> GenericDocumentData(
            title = "Aesthetic Recipe Card",
            fields = mapOf(
                "recipeName" to "Rahul's Classic Basil Pesto Pasta",
                "prepTime" to "20 minutes",
                "servings" to "4 portions",
                "instructions" to "1. Boil penne pasta in salted water until al dente.\n2. Blend basil, garlic, pine nuts, parmesan, and olive oil.\n3. Toss pasta with pesto, add halved cherry tomatoes, and serve warm with basil garnish."
            ),
            listItems = listOf(
                "350g Penne Pasta",
                "2 cups Fresh Sweet Basil",
                "1/2 cup Parmigiano-Reggiano",
                "1/3 cup Pine Nuts or Walnuts",
                "1/2 cup Rich Extra Virgin Olive Oil"
            )
        )
        33 -> GenericDocumentData(
            title = "Official RSVP Response",
            fields = mapOf(
                "eventName" to "Nexturn Annual Gala Night",
                "responderName" to "Rahul Shah, workspace engineer",
                "acceptStatus" to "Happily Accepting",
                "dietaryNeeds" to "Vegetarian option, no peanut allergies"
            )
        )
        34 -> GenericDocumentData(
            title = "Public Petition",
            fields = mapOf(
                "petitionTitle" to "Revitalize the City Green Canopy",
                "targetAuthority" to "City Parks and Recreation Bureau",
                "statement" to "We petition the council to plant 500 indigenous shade trees along the central avenue to combat urban heat island effects.",
                "initiatorName" to "Rahul Shah, Community Alliance Representative"
            )
        )
        35 -> GenericDocumentData(
            title = "Formal Complaint Letter",
            fields = mapOf(
                "recipientName" to "Metro Transit Commission",
                "issueDate" to "June 4th, 2026",
                "problemDescription" to "Multiple express bus routes were cancelled without real-time notifications on the transit app, causing commuters 1+ hour waiting times.",
                "desiredOutcome" to "Implement instant push alerts for route shifts and restore transit frequency.",
                "senderContact" to "Rahul Shah - rahulshah2021nice@gmail.com"
            )
        )
        36 -> GenericDocumentData(
            title = "Recommendation Request Card",
            fields = mapOf(
                "refereeName" to "Professor Aris Thorne, Computer Science",
                "purpose" to "an application for the Advanced Software Architecture Research Grant",
                "deadline" to "June 25th",
                "keyAchievements" to "My core research contributions to clean codebases and highly responsive full-stack MVVM Android applications in Kotlin.",
                "senderContact" to "Rahul Shah -- senior developer"
            )
        )
        37 -> GenericDocumentData(
            title = "Resignation Acceptance",
            fields = mapOf(
                "employeeName" to "David Vance, junior dev",
                "acknowledgedDate" to "June 10th",
                "lastDay" to "June 24th",
                "transitionNotes" to "Ensure all in-progress features are merged into main branch and write clear readme files explaining the custom modules.",
                "managerName" to "Rahul Shah, Engineering Manager"
            )
        )
        38 -> GenericDocumentData(
            title = "Formal Warning Ticket",
            fields = mapOf(
                "issuedTo" to "Liam Carter",
                "dateIssued" to "2026-06-06",
                "violation" to "Deploying unchecked code changes straight to the release branch three separate times without code reviews.",
                "consequences" to "Placement on a structured engineering performance improvement program and pairing with a senior review guardian.",
                "issuedBy" to "Rahul Shah, Supervisor of Core Engineering"
            )
        )
        39 -> GenericDocumentData(
            title = "Certificate of Appreciation",
            fields = mapOf(
                "recipientName" to "Liam Carter",
                "achievement" to "outstanding quality assurance and rigorous end-to-end regression testing",
                "presenter" to "Rahul Shah, lead architect",
                "date" to "June 6th, 2026"
            )
        )
        40 -> GenericDocumentData(
            title = "Text Flyer / Program Details",
            fields = mapOf(
                "headerText" to "Grand Creative Writing Workshop",
                "details" to "Learn the foundations of narrative architecture, character creation, and editing aesthetics with expert writers.",
                "time" to "Every Sunday in September from 2:00 PM to 5:00 PM",
                "location" to "Main Hall, Nexturn Office Suite",
                "contactInfo" to "email info@nexturn.workspace to reserve your place"
            )
        )
        41 -> GenericDocumentData(
            title = "Last Will & Testament (Simple)",
            fields = mapOf(
                "testatorName" to "Rahul Shah",
                "executor" to "Trustee Liam Carter",
                "bequests" to "I bequeath my collection of rare mechanical keyboards and technical design manuals to my closest nephew.",
                "witnesses" to "Notary Public and Core Workspace Witnesses"
            )
        )
        42 -> GenericDocumentData(
            title = "Rental & Lease Agreement",
            fields = mapOf(
                "landlord" to "Rahul Shah Properties Ltd.",
                "tenant" to "Jordan Fletcher",
                "address" to "Suite 404, Aesthetic Towers, District 5",
                "rentAmount" to "$1,250 a month",
                "terms" to "A twelve-month fixed-term lease starting on August 1st. Subletting is forbidden without prior written approval."
            )
        )
        43 -> GenericDocumentData(
            title = "Simple Bill of Sale",
            fields = mapOf(
                "seller" to "Rahul Shah",
                "buyer" to "Alex Avery",
                "itemDescription" to "Custom Walnut Top Work Desk (60 x 30 inches) with matte black steel framing",
                "price" to "$450.00 cash",
                "warrantyDetails" to "Sold strictly in as-is condition. No post-sale refunds or technical guarantees provided."
            )
        )
        44 -> GenericDocumentData(
            title = "Promissory Note",
            fields = mapOf(
                "borrower" to "Alex Avery",
                "lender" to "Rahul Shah",
                "principalAmount" to "$5,000.00",
                "interestRate" to "5.0% simple interest per year",
                "repaymentSchedule" to "The borrower agrees to pay monthly installments of $500 starting from September 1st until the entire balance is cleared."
            )
        )
        45 -> GenericDocumentData(
            title = "Business Report Summary",
            fields = mapOf(
                "title" to "Nexturn Workspace Q2 Strategic Expansion Report",
                "author" to "Rahul Shah, CEO & Lead Architect",
                "executiveSummary" to "An aggressive expansion of offline-first document template categories, paired with edge-to-edge UI spacing and interactive branding assets.",
                "keyFindings" to "Users reported a 40% increase in productivity when template options were scaled from 24 up to 47 custom styled letter and formal sheets.",
                "nextSteps" to "Integrate automatic local Room Database backup scheduling and visual PDF styling templates."
            )
        )
        else -> GenericDocumentData(title = "Custom Document")
    }
}

@Composable
fun RenderDynamicForm(selectedTab: Int, viewModel: DocumentViewModel, focusedField: MutableState<String>, highlightIfEmpty: Boolean) {
    val dynamicJsons by viewModel.activeDynamicJsons.collectAsState()
    val customDocs by viewModel.activeCustomDocs.collectAsState()
    
    val json = dynamicJsons[selectedTab] ?: ""
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        if (selectedTab in 23..45) {
            val currentData = if (json.isEmpty()) {
                getDefaultGenericDoc(selectedTab)
            } else {
                GenericDocumentData.fromJson(json)
            }
            
            // Loop through default form template keys
            val defaultDocKeys = getDefaultGenericDoc(selectedTab).fields.keys
            defaultDocKeys.forEach { key ->
                val value = currentData.fields[key] ?: ""
                val label = when (key) {
                    "recipientName" -> "Recipient Name"
                    "salutation" -> "Salutation"
                    "message" -> "Personal Message"
                    "closing" -> "Closing"
                    "senderName" -> "Sender Name"
                    "hostName" -> "Host / Organizer Name"
                    "eventType" -> "Event Name / Type"
                    "dateTime" -> "Date & Time"
                    "location" -> "Location / Venue"
                    "rsvpContact" -> "RSVP Contact Details"
                    "specialNotes" -> "Special Instructions"
                    "reason" -> "Reason"
                    "resolution" -> "Resolution Plan"
                    "deceasedName" -> "Deceased Person Name"
                    "memories" -> "Fond Memories / Condolence Words"
                    "supportOffer" -> "Offer of Support"
                    "achievement" -> "Achievement Details"
                    "pieceTitle" -> "Title"
                    "authorName" -> "Author Name"
                    "genre" -> "Genre / Tone"
                    "content" -> "Content Body Text"
                    "date" -> "Date / Stamp"
                    "mood" -> "Mood & Perspective"
                    "reflection" -> "Personal Journal Reflection"
                    "gratefulFor" -> "Grateful Elements"
                    "listTitle" -> "Objective / Title"
                    "dueDate" -> "Target Due Date"
                    "storeName" -> "Target Store Name"
                    "recipeName" -> "Recipe Name"
                    "prepTime" -> "Prep & Cook Time"
                    "servings" -> "Yield & Servings"
                    "instructions" -> "Preparation Steps"
                    "recipient" -> "Recipient Name"
                    "gift" -> "Occasion / Gift Description"
                    "warmMessage" -> "Personal Greeting Message"
                    "sender" -> "Sender Identity"
                    "eventName" -> "Event Name"
                    "responderName" -> "Guest / Responder Name"
                    "acceptStatus" -> "RSVP Acceptance Status"
                    "dietaryNeeds" -> "Dietary / Accommodations"
                    "petitionTitle" -> "Petition Title"
                    "targetAuthority" -> "Target Governing Authority"
                    "statement" -> "Petition Accords & Statement"
                    "initiatorName" -> "Prime Initiator Contact"
                    "issueDate" -> "Date of Occurrence"
                    "problemDescription" -> "Problem Report Details"
                    "desiredOutcome" -> "Expected Resolution"
                    "senderContact" -> "Your Name & Contact Info"
                    "refereeName" -> "Professional Referee Name"
                    "purpose" -> "Applying Program/Agency"
                    "deadline" -> "Filing Deadline"
                    "keyAchievements" -> "Key Points to Highlight"
                    "employeeName" -> "Associate Named"
                    "acknowledgedDate" -> "Docket Date"
                    "lastDay" -> "Severance Date"
                    "transitionNotes" -> "Transition Handover Directives"
                    "managerName" -> "Manager Signature Authority"
                    "issuedTo" -> "Subject Party"
                    "dateIssued" -> "Issuance Date"
                    "violation" -> "Infraction/Conduct Matters"
                    "consequences" -> "Future Corrective Measures"
                    "issuedBy" -> "Supervisor Title"
                    "presenter" -> "Host Presenter Identity"
                    "headerText" -> "Display Banner Headline"
                    "details" -> "Detailed Program Specifications"
                    "time" -> "Time Period"
                    "contactInfo" -> "Contact Authority Details"
                    "testatorName" -> "Your Full Name"
                    "executor" -> "Appointed Executor"
                    "bequests" -> "Bequests / Wills Allocations"
                    "witnesses" -> "Witness Signature Blocks"
                    "landlord" -> "Lessor (Landlord) Name"
                    "tenant" -> "Lessee (Tenant) Name"
                    "address" -> "Asset Real Estate Address"
                    "rentAmount" -> "Rental Consideration Price"
                    "terms" -> "Lease Terms & Covenants"
                    "seller" -> "Seller Name"
                    "buyer" -> "Buyer Name"
                    "itemDescription" -> "Sale Asset Description"
                    "price" -> "Transaction Purchase Price"
                    "warrantyDetails" -> "Warranty Terms"
                    "borrower" -> "Borrower Name"
                    "lender" -> "Lender Name"
                    "principalAmount" -> "Debt Principal Sum"
                    "interestRate" -> "Interest Standard %"
                    "repaymentSchedule" -> "Repayment Plan"
                    else -> if (key.isNotEmpty()) key[0].uppercaseChar() + key.substring(1) else ""
                }
                val isMultiline = key == "message" || key == "specialNotes" || key == "resolution" || key == "memories" || key == "content" || key == "reflection" || key == "instructions" || key == "warmMessage" || key == "statement" || key == "problemDescription" || key == "keyAchievements" || key == "transitionNotes" || key == "consequences" || key == "details" || key == "bequests" || key == "terms" || key == "repaymentSchedule"
                
                SmartField(
                    fieldName = key,
                    label = label,
                    placeholder = "Enter $label...",
                    value = value,
                    onUpdate = { newVal ->
                        val nextFields = currentData.fields.toMutableMap().apply { put(key, newVal) }
                        viewModel.updateDynamicJson(selectedTab, currentData.copy(fields = nextFields).toJson())
                    },
                    focusedField = focusedField,
                    highlightIfEmpty = highlightIfEmpty,
                    isMultiline = isMultiline
                )
            }
            
            if (selectedTab == 30 || selectedTab == 31 || selectedTab == 32) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = when(selectedTab) {
                        32 -> "Ingredients Checklist"
                        else -> "List Items"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                currentData.listItems.forEachIndexed { idx, itm ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f)) {
                            SmartField(
                                fieldName = "list_item_$idx",
                                label = "Item #${idx + 1}",
                                placeholder = "Enter item...",
                                value = itm,
                                onUpdate = { newVal ->
                                    val nextList = currentData.listItems.toMutableList().apply { set(idx, newVal) }
                                    viewModel.updateDynamicJson(selectedTab, currentData.copy(listItems = nextList).toJson())
                                },
                                focusedField = focusedField,
                                highlightIfEmpty = highlightIfEmpty
                            )
                        }
                        IconButton(onClick = {
                            val nextList = currentData.listItems.toMutableList().apply { removeAt(idx) }
                            viewModel.updateDynamicJson(selectedTab, currentData.copy(listItems = nextList).toJson())
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Item")
                        }
                    }
                }
                Button(
                    onClick = {
                        val nextList = currentData.listItems + ""
                        viewModel.updateDynamicJson(selectedTab, currentData.copy(listItems = nextList).toJson())
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Item")
                }
            }
        } else {
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
                46 -> CustomDocumentForm(customDocs[46] ?: CustomDocumentData(templateId=46), { viewModel.updateCustomDoc(46) { _ -> it } }, focusedField, highlightIfEmpty)
            }
        }
    }
}

@Composable
fun RenderDynamicPreview(selectedTab: Int, viewModel: DocumentViewModel, activePaperColor: androidx.compose.ui.graphics.Color) {
    val dynamicJsons by viewModel.activeDynamicJsons.collectAsState()
    val customDocs by viewModel.activeCustomDocs.collectAsState()
    val json = dynamicJsons[selectedTab] ?: ""
    
    PaperCanvas(paperColor = activePaperColor) {
        if (selectedTab in 23..45) {
            val currentData = if (json.isEmpty()) {
                getDefaultGenericDoc(selectedTab)
            } else {
                GenericDocumentData.fromJson(json)
            }
            GenericDocumentPreview(selectedTab, currentData)
        } else {
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
                46 -> CustomDocumentPreview(customDocs[46] ?: CustomDocumentData(templateId=46))
            }
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
