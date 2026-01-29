package com.bcasekuritas.mybest.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.ext.converter.Converters

@Entity(tableName = DBEntity.BIOMETRIC_SOURCE)
@TypeConverters(Converters::class)
data class BiometricObject(
    @PrimaryKey
    @ColumnInfo
    val userId: String,

    @ColumnInfo
    val pw: String? = null,

    @ColumnInfo
    val token: String? = null,
)