package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_documents")
data class SavedDocument(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,          // "CV", "COVER_LETTER", "EMAIL", "INVOICE", "PROPOSAL", "ACTIVE_DRAFT_CV", "ACTIVE_DRAFT_COVER_LETTER", etc.
    val name: String,          // Template Name (e.g. "Acme Tech Resume", "Freelance Bill #104")
    val contentJson: String,   // Raw Serialized JSON
    val lastUpdated: Long = System.currentTimeMillis()
)
