package com.example.data

import kotlinx.coroutines.flow.Flow

class DocumentRepository(private val documentDao: DocumentDao) {
    fun getTemplatesByType(type: String): Flow<List<SavedDocument>> = 
        documentDao.getDocumentsByType(type)

    suspend fun getDocumentById(id: Long): SavedDocument? = 
        documentDao.getDocumentById(id)

    suspend fun getDocumentByTypeAndName(type: String, name: String): SavedDocument? = 
        documentDao.getDocumentByTypeAndName(type, name)

    suspend fun saveDocument(document: SavedDocument): Long = 
        documentDao.insertDocument(document)

    suspend fun deleteDocument(id: Long) = 
        documentDao.deleteDocumentById(id)

    suspend fun deleteByTypeAndName(type: String, name: String) = 
        documentDao.deleteByTypeAndName(type, name)

    fun getAllDocuments(): Flow<List<SavedDocument>> =
        documentDao.getAllDocuments()
}
