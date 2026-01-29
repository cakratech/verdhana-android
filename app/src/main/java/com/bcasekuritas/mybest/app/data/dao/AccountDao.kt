package com.bcasekuritas.mybest.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.app.data.entity.AccountObject

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(accountObject: AccountObject)
    @Query("SELECT accName FROM ${DBEntity.ACCOUNT_SOURCE} WHERE accNo = :accNo")
    suspend fun getAccName(accNo: String): String
    @Query("SELECT * FROM ${DBEntity.ACCOUNT_SOURCE} WHERE accNo = :accNo")
    suspend fun getAccountInfo(accNo: String): AccountObject
    @Query("SELECT * FROM ${DBEntity.ACCOUNT_SOURCE}")
    suspend fun getAllAccount(): List<AccountObject>
    @Query("DELETE FROM ${DBEntity.ACCOUNT_SOURCE}")
    suspend fun clearAllAccounts()
}