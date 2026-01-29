package com.bcasekuritas.mybest.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.app.data.entity.BiometricObject

@Dao
interface BiometricDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(biometricObject: BiometricObject)
    @Query("SELECT * FROM ${DBEntity.BIOMETRIC_SOURCE} WHERE userId = :userId")
    suspend fun getToken(userId: String): BiometricObject
    @Query("UPDATE ${DBEntity.BIOMETRIC_SOURCE} SET token = :token WHERE userId = :userId")
    suspend fun updateToken(userId: String, token: String)
    @Query("UPDATE ${DBEntity.BIOMETRIC_SOURCE} SET token = NULL WHERE userId = :userId")
    suspend fun deleteToken(userId: String)
    @Query("DELETE FROM ${DBEntity.BIOMETRIC_SOURCE}")
    suspend fun deleteBiometric()
}