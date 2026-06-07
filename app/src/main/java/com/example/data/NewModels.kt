package com.example.data

import org.json.JSONObject
import org.json.JSONArray

data class ReferenceLetterData(
    val recipientName: String = "",
    val subject: String = "",
    val relationship: String = "",
    val strengths: List<String> = emptyList(),
    val closing: String = "",
    val nameAndTitle: String = ""
) {
    fun toJson(): String {
        return JSONObject().apply {
            put("recipientName", recipientName)
            put("subject", subject)
            put("relationship", relationship)
            put("strengths", JSONArray(strengths))
            put("closing", closing)
            put("nameAndTitle", nameAndTitle)
        }.toString()
    }
    companion object {
        fun fromJson(jsonStr: String): ReferenceLetterData {
            if (jsonStr.isEmpty()) return ReferenceLetterData()
            val js = JSONObject(jsonStr)
            val st = mutableListOf<String>()
            val arr = js.optJSONArray("strengths")
            if (arr != null) {
                for (i in 0 until arr.length()) st.add(arr.optString(i, ""))
            }
            return ReferenceLetterData(
                recipientName = js.optString("recipientName", ""),
                subject = js.optString("subject", ""),
                relationship = js.optString("relationship", ""),
                strengths = st,
                closing = js.optString("closing", ""),
                nameAndTitle = js.optString("nameAndTitle", "")
            )
        }
    }
}

data class PurchaseOrderData(
    val poNumber: String = "",
    val date: String = "",
    val vendorName: String = "",
    val shipToAddress: String = "",
    val items: List<PoItem> = emptyList(),
    val subtotal: String = "",
    val tax: String = "",
    val total: String = ""
) {
    data class PoItem(val desc: String = "", val unitPrice: String = "", val quantity: String = "", val subtotal: String = "")
    fun toJson(): String {
        return JSONObject().apply {
            put("poNumber", poNumber)
            put("date", date)
            put("vendorName", vendorName)
            put("shipToAddress", shipToAddress)
            put("subtotal", subtotal)
            put("tax", tax)
            put("total", total)
            val arr = JSONArray()
            items.forEach { arr.put(JSONObject().apply {
                put("desc", it.desc)
                put("unitPrice", it.unitPrice)
                put("quantity", it.quantity)
                put("subtotal", it.subtotal)
            })}
            put("items", arr)
        }.toString()
    }
    companion object {
        fun fromJson(jsonStr: String): PurchaseOrderData {
            if (jsonStr.isEmpty()) return PurchaseOrderData()
            val js = JSONObject(jsonStr)
            val items = mutableListOf<PoItem>()
            val arr = js.optJSONArray("items")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i)
                    if (obj != null) items.add(PoItem(obj.optString("desc", ""), obj.optString("unitPrice", ""), obj.optString("quantity", ""), obj.optString("subtotal", "")))
                }
            }
            return PurchaseOrderData(
                poNumber = js.optString("poNumber", ""),
                date = js.optString("date", ""),
                vendorName = js.optString("vendorName", ""),
                shipToAddress = js.optString("shipToAddress", ""),
                items = items,
                subtotal = js.optString("subtotal", ""),
                tax = js.optString("tax", ""),
                total = js.optString("total", "")
            )
        }
    }
}

data class QuoteData(
    val quoteNumber: String = "",
    val date: String = "",
    val validUntil: String = "",
    val clientName: String = "",
    val items: List<QuoteItem> = emptyList(),
    val subtotal: String = "",
    val discount: String = "",
    val tax: String = "",
    val total: String = "",
    val notes: String = ""
) {
    data class QuoteItem(val desc: String = "", val unitPrice: String = "", val quantity: String = "", val total: String = "")
    fun toJson(): String {
        return JSONObject().apply {
            put("quoteNumber", quoteNumber)
            put("date", date)
            put("validUntil", validUntil)
            put("clientName", clientName)
            put("subtotal", subtotal)
            put("discount", discount)
            put("tax", tax)
            put("total", total)
            put("notes", notes)
            val arr = JSONArray()
            items.forEach { arr.put(JSONObject().apply { put("desc", it.desc); put("unitPrice", it.unitPrice); put("quantity", it.quantity); put("total", it.total) }) }
            put("items", arr)
        }.toString()
    }
    companion object {
        fun fromJson(jsonStr: String): QuoteData {
            if (jsonStr.isEmpty()) return QuoteData()
            val js = JSONObject(jsonStr)
            val items = mutableListOf<QuoteItem>()
            val arr = js.optJSONArray("items")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i)
                    if (obj != null) items.add(QuoteItem(obj.optString("desc", ""), obj.optString("unitPrice", ""), obj.optString("quantity", ""), obj.optString("total", "")))
                }
            }
            return QuoteData(
                quoteNumber = js.optString("quoteNumber", ""),
                date = js.optString("date", ""),
                validUntil = js.optString("validUntil", ""),
                clientName = js.optString("clientName", ""),
                items = items,
                subtotal = js.optString("subtotal", ""),
                discount = js.optString("discount", ""),
                tax = js.optString("tax", ""),
                total = js.optString("total", ""),
                notes = js.optString("notes", "")
            )
        }
    }
}

data class NdaData(
    val disclosingParty: String = "",
    val receivingParty: String = "",
    val effectiveDate: String = "",
    val purpose: String = "",
    val duration: String = "",
    val signatureName: String = ""
) {
    fun toJson(): String = JSONObject().apply {
        put("disclosingParty", disclosingParty); put("receivingParty", receivingParty); put("effectiveDate", effectiveDate); put("purpose", purpose); put("duration", duration); put("signatureName", signatureName)
    }.toString()
    companion object {
        fun fromJson(jsonStr: String): NdaData {
            if (jsonStr.isEmpty()) return NdaData()
            val js = JSONObject(jsonStr)
            return NdaData(js.optString("disclosingParty", ""), js.optString("receivingParty", ""), js.optString("effectiveDate", ""), js.optString("purpose", ""), js.optString("duration", ""), js.optString("signatureName", ""))
        }
    }
}

data class TimesheetData(
    val employeeName: String = "",
    val weekStarting: String = "",
    val entries: List<TsEntry> = emptyList(),
    val totalHours: String = "",
    val approvalSignature: String = ""
) {
    data class TsEntry(val date: String = "", val project: String = "", val hours: String = "", val notes: String = "")
    fun toJson(): String {
        return JSONObject().apply {
            put("employeeName", employeeName); put("weekStarting", weekStarting); put("totalHours", totalHours); put("approvalSignature", approvalSignature)
            val arr = JSONArray()
            entries.forEach { arr.put(JSONObject().apply { put("date", it.date); put("project", it.project); put("hours", it.hours); put("notes", it.notes) }) }
            put("entries", arr)
        }.toString()
    }
    companion object {
        fun fromJson(jsonStr: String): TimesheetData {
            if (jsonStr.isEmpty()) return TimesheetData()
            val js = JSONObject(jsonStr)
            val entries = mutableListOf<TsEntry>()
            val arr = js.optJSONArray("entries")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i)
                    if (obj != null) entries.add(TsEntry(obj.optString("date", ""), obj.optString("project", ""), obj.optString("hours", ""), obj.optString("notes", "")))
                }
            }
            return TimesheetData(js.optString("employeeName", ""), js.optString("weekStarting", ""), entries, js.optString("totalHours", ""), js.optString("approvalSignature", ""))
        }
    }
}

data class ExpenseReportData(
    val employeeName: String = "",
    val date: String = "",
    val department: String = "",
    val entries: List<ExEntry> = emptyList(),
    val totalReimbursement: String = ""
) {
    data class ExEntry(val date: String = "", val category: String = "", val description: String = "", val amount: String = "", val receipt: String = "")
    fun toJson(): String {
        return JSONObject().apply {
            put("employeeName", employeeName); put("date", date); put("department", department); put("totalReimbursement", totalReimbursement)
            val arr = JSONArray()
            entries.forEach { arr.put(JSONObject().apply { put("date", it.date); put("category", it.category); put("description", it.description); put("amount", it.amount); put("receipt", it.receipt) }) }
            put("entries", arr)
        }.toString()
    }
    companion object {
        fun fromJson(jsonStr: String): ExpenseReportData {
            if (jsonStr.isEmpty()) return ExpenseReportData()
            val js = JSONObject(jsonStr)
            val entries = mutableListOf<ExEntry>()
            val arr = js.optJSONArray("entries")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i)
                    if (obj != null) entries.add(ExEntry(obj.optString("date", ""), obj.optString("category", ""), obj.optString("description", ""), obj.optString("amount", ""), obj.optString("receipt", "")))
                }
            }
            return ExpenseReportData(js.optString("employeeName", ""), js.optString("date", ""), js.optString("department", ""), entries, js.optString("totalReimbursement", ""))
        }
    }
}

data class PressReleaseData(
    val embargoDate: String = "",
    val headline: String = "",
    val dateline: String = "",
    val body: String = "",
    val boilerplate: String = "",
    val mediaContact: String = ""
) {
    fun toJson(): String = JSONObject().apply { put("embargoDate", embargoDate); put("headline", headline); put("dateline", dateline); put("body", body); put("boilerplate", boilerplate); put("mediaContact", mediaContact) }.toString()
    companion object { fun fromJson(jsonStr: String): PressReleaseData { if (jsonStr.isEmpty()) return PressReleaseData(); val js = JSONObject(jsonStr); return PressReleaseData(js.optString("embargoDate", ""), js.optString("headline", ""), js.optString("dateline", ""), js.optString("body", ""), js.optString("boilerplate", ""), js.optString("mediaContact", "")) } }
}

data class MemoData(
    val to: String = "", val from: String = "", val date: String = "", val subject: String = "", val body: String = "", val cc: String = ""
) {
    fun toJson(): String = JSONObject().apply { put("to", to); put("from", from); put("date", date); put("subject", subject); put("body", body); put("cc", cc) }.toString()
    companion object { fun fromJson(jsonStr: String): MemoData { if (jsonStr.isEmpty()) return MemoData(); val js = JSONObject(jsonStr); return MemoData(js.optString("to", ""), js.optString("from", ""), js.optString("date", ""), js.optString("subject", ""), js.optString("body", ""), js.optString("cc", "")) } }
}

data class ThankYouLetterData(
    val date: String = "", val recipientName: String = "", val reason: String = "", val specificDetail: String = "", val closing: String = "", val yourName: String = ""
) {
    fun toJson(): String = JSONObject().apply { put("date", date); put("recipientName", recipientName); put("reason", reason); put("specificDetail", specificDetail); put("closing", closing); put("yourName", yourName) }.toString()
    companion object { fun fromJson(jsonStr: String): ThankYouLetterData { if (jsonStr.isEmpty()) return ThankYouLetterData(); val js = JSONObject(jsonStr); return ThankYouLetterData(js.optString("date", ""), js.optString("recipientName", ""), js.optString("reason", ""), js.optString("specificDetail", ""), js.optString("closing", ""), js.optString("yourName", "")) } }
}

data class AcceptanceLetterData(
    val date: String = "", val hiringManagerName: String = "", val companyName: String = "", val jobTitle: String = "", val declaration: String = "", val startDate: String = "", val signatureLine: String = ""
) {
    fun toJson(): String = JSONObject().apply { put("date", date); put("hiringManagerName", hiringManagerName); put("companyName", companyName); put("jobTitle", jobTitle); put("declaration", declaration); put("startDate", startDate); put("signatureLine", signatureLine) }.toString()
    companion object { fun fromJson(jsonStr: String): AcceptanceLetterData { if (jsonStr.isEmpty()) return AcceptanceLetterData(); val js = JSONObject(jsonStr); return AcceptanceLetterData(js.optString("date", ""), js.optString("hiringManagerName", ""), js.optString("companyName", ""), js.optString("jobTitle", ""), js.optString("declaration", ""), js.optString("startDate", ""), js.optString("signatureLine", "")) } }
}

data class TerminationLetterData(
    val date: String = "", val employeeName: String = "", val position: String = "", val lastDay: String = "", val reason: String = "", val returnOfProperty: String = "", val signatureEmployer: String = ""
) {
    fun toJson(): String = JSONObject().apply { put("date", date); put("employeeName", employeeName); put("position", position); put("lastDay", lastDay); put("reason", reason); put("returnOfProperty", returnOfProperty); put("signatureEmployer", signatureEmployer) }.toString()
    companion object { fun fromJson(jsonStr: String): TerminationLetterData { if (jsonStr.isEmpty()) return TerminationLetterData(); val js = JSONObject(jsonStr); return TerminationLetterData(js.optString("date", ""), js.optString("employeeName", ""), js.optString("position", ""), js.optString("lastDay", ""), js.optString("reason", ""), js.optString("returnOfProperty", ""), js.optString("signatureEmployer", "")) } }
}

data class PerformanceReviewData(
    val employeeName: String = "", val reviewPeriod: String = "", val reviewerName: String = "", val ratingsScale: String = "", val strengths: List<String> = emptyList(), val areasForImprovement: List<String> = emptyList(), val goals: String = "", val employeeSignature: String = "", val managerSignature: String = ""
) {
    fun toJson(): String = JSONObject().apply {
        put("employeeName", employeeName); put("reviewPeriod", reviewPeriod); put("reviewerName", reviewerName); put("ratingsScale", ratingsScale)
        put("strengths", JSONArray(strengths)); put("areasForImprovement", JSONArray(areasForImprovement))
        put("goals", goals); put("employeeSignature", employeeSignature); put("managerSignature", managerSignature)
    }.toString()
    companion object {
        fun fromJson(jsonStr: String): PerformanceReviewData {
            if (jsonStr.isEmpty()) return PerformanceReviewData()
            val js = JSONObject(jsonStr)
            val st = mutableListOf<String>()
            val stArr = js.optJSONArray("strengths")
            if (stArr != null) for (i in 0 until stArr.length()) st.add(stArr.optString(i, ""))
            val ar = mutableListOf<String>()
            val arArr = js.optJSONArray("areasForImprovement")
            if (arArr != null) for (i in 0 until arArr.length()) ar.add(arArr.optString(i, ""))
            return PerformanceReviewData(js.optString("employeeName", ""), js.optString("reviewPeriod", ""), js.optString("reviewerName", ""), js.optString("ratingsScale", ""), st, ar, js.optString("goals", ""), js.optString("employeeSignature", ""), js.optString("managerSignature", ""))
        }
    }
}

data class GenericDocumentData(
    val title: String = "",
    val fields: Map<String, String> = emptyMap(),
    val listItems: List<String> = emptyList()
) {
    fun toJson(): String {
        return JSONObject().apply {
            put("title", title)
            val fObj = JSONObject()
            fields.forEach { (k, v) -> fObj.put(k, v) }
            put("fields", fObj)
            val lArr = JSONArray()
            listItems.forEach { lArr.put(it) }
            put("listItems", lArr)
        }.toString()
    }
    companion object {
        fun fromJson(jsonStr: String): GenericDocumentData {
            if (jsonStr.isEmpty()) return GenericDocumentData()
            val js = JSONObject(jsonStr)
            val fMap = mutableMapOf<String, String>()
            val fObj = js.optJSONObject("fields")
            if (fObj != null) {
                fObj.keys().forEach { k ->
                    fMap[k] = fObj.optString(k, "")
                }
            }
            val lList = mutableListOf<String>()
            val lArr = js.optJSONArray("listItems")
            if (lArr != null) {
                for (i in 0 until lArr.length()) {
                    lList.add(lArr.optString(i, ""))
                }
            }
            return GenericDocumentData(
                title = js.optString("title", ""),
                fields = fMap,
                listItems = lList
            )
        }
    }
}

data class MedicalCertificateData(
    val patientName: String = "",
    val date: String = "",
    val diagnosis: String = "",
    val recommendedTimeOffFrom: String = "",
    val recommendedTimeOffTo: String = "",
    val doctorName: String = "",
    val clinicStampText: String = "REGISTERED CLINIC STAMP"
) {
    fun toJson(): String = JSONObject().apply {
        put("patientName", patientName)
        put("date", date)
        put("diagnosis", diagnosis)
        put("recommendedTimeOffFrom", recommendedTimeOffFrom)
        put("recommendedTimeOffTo", recommendedTimeOffTo)
        put("doctorName", doctorName)
        put("clinicStampText", clinicStampText)
    }.toString()

    companion object {
        fun fromJson(jsonStr: String): MedicalCertificateData {
            if (jsonStr.isEmpty()) return MedicalCertificateData()
            val js = JSONObject(jsonStr)
            return MedicalCertificateData(
                patientName = js.optString("patientName", ""),
                date = js.optString("date", ""),
                diagnosis = js.optString("diagnosis", ""),
                recommendedTimeOffFrom = js.optString("recommendedTimeOffFrom", ""),
                recommendedTimeOffTo = js.optString("recommendedTimeOffTo", ""),
                doctorName = js.optString("doctorName", ""),
                clinicStampText = js.optString("clinicStampText", "REGISTERED CLINIC STAMP")
            )
        }
    }
}

data class ConsentFormData(
    val organizationName: String = "",
    val participantName: String = "",
    val activityEvent: String = "",
    val risksAcknowledged: List<String> = emptyList(),
    val consentStatement: String = "",
    val signatureLine: String = "",
    val date: String = ""
) {
    fun toJson(): String = JSONObject().apply {
        put("organizationName", organizationName)
        put("participantName", participantName)
        put("activityEvent", activityEvent)
        put("risksAcknowledged", JSONArray(risksAcknowledged))
        put("consentStatement", consentStatement)
        put("signatureLine", signatureLine)
        put("date", date)
    }.toString()

    companion object {
        fun fromJson(jsonStr: String): ConsentFormData {
            if (jsonStr.isEmpty()) return ConsentFormData()
            val js = JSONObject(jsonStr)
            val risks = mutableListOf<String>()
            val arr = js.optJSONArray("risksAcknowledged")
            if (arr != null) {
                for (i in 0 until arr.length()) risks.add(arr.optString(i, ""))
            }
            return ConsentFormData(
                organizationName = js.optString("organizationName", ""),
                participantName = js.optString("participantName", ""),
                activityEvent = js.optString("activityEvent", ""),
                risksAcknowledged = risks,
                consentStatement = js.optString("consentStatement", ""),
                signatureLine = js.optString("signatureLine", ""),
                date = js.optString("date", "")
            )
        }
    }
}

data class LetterOfIntentData(
    val recipient: String = "",
    val sender: String = "",
    val programPosition: String = "",
    val whyInterested: String = "",
    val whyGoodFitBullets: List<String> = emptyList(),
    val closing: String = "",
    val signature: String = ""
) {
    fun toJson(): String = JSONObject().apply {
        put("recipient", recipient)
        put("sender", sender)
        put("programPosition", programPosition)
        put("whyInterested", whyInterested)
        put("whyGoodFitBullets", JSONArray(whyGoodFitBullets))
        put("closing", closing)
        put("signature", signature)
    }.toString()

    companion object {
        fun fromJson(jsonStr: String): LetterOfIntentData {
            if (jsonStr.isEmpty()) return LetterOfIntentData()
            val js = JSONObject(jsonStr)
            val bullets = mutableListOf<String>()
            val arr = js.optJSONArray("whyGoodFitBullets")
            if (arr != null) {
                for (i in 0 until arr.length()) bullets.add(arr.optString(i, ""))
            }
            return LetterOfIntentData(
                recipient = js.optString("recipient", ""),
                sender = js.optString("sender", ""),
                programPosition = js.optString("programPosition", ""),
                whyInterested = js.optString("whyInterested", ""),
                whyGoodFitBullets = bullets,
                closing = js.optString("closing", ""),
                signature = js.optString("signature", "")
              )
          }
      }
}

