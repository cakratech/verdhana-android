package com.bcasekuritas.mybest.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bcasekuritas.mybest.app.data.entity.SessionObject

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(sessionObject: SessionObject)

    @Query("SELECT sessionPin FROM SESSION WHERE userId = :userId")
    fun getSessionPin(userId: String): Long

    @Query("UPDATE SESSION SET sessionPin = :newSessionPin WHERE userId = :userId")
    fun updateSessionPin(userId: String, newSessionPin: String)

    @Query("DELETE FROM SESSION")
    fun deleteSessions()
}