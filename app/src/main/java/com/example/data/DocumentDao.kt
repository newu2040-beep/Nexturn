package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM saved_documents WHERE type = :type ORDER BY lastUpdated DESC")
    fun getDocumentsByType(type: String): Flow<List<SavedDocument>>

    @Query("SELECT * FROM saved_documents WHERE type = :type AND name = :name LIMIT 1")
    suspend fun getDocumentByTypeAndName(type: String, name: String): SavedDocument?

    @Query("SELECT * FROM saved_documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: Long): SavedDocument?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: SavedDocument): Long

    @Query("DELETE FROM saved_documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Long)

    @Query("DELETE FROM saved_documents WHERE type = :type AND name = :name")
    suspend fun deleteByTypeAndName(type: String, name: String)

    @Query("SELECT * FROM saved_documents ORDER BY lastUpdated DESC")
    fun getAllDocuments(): Flow<List<SavedDocument>>
}
