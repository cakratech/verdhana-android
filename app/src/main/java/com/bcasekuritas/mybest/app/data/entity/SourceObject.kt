package com.bcasekuritas.mybest.app.data.entity

import androidx.annotation.NonNull
import androidx.room.*
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.ext.converter.Converters

@Entity(tableName = DBEntity.ENTITY_SOURCE)
@TypeConverters(Converters::class)
data class SourceObject(
    @PrimaryKey @NonNull
    @ColumnInfo val id: String,

    @ColumnInfo
    val difficulty: Int,

    @ColumnInfo
    val image: String,

    @ColumnInfo
    val fats: String,

    @ColumnInfo
    val thumb: String,

    @ColumnInfo
    val proteins: String,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val carbos: String,

    @ColumnInfo
    val description: String,

    @ColumnInfo
    val calories: String,

    @ColumnInfo
    val time: String,

    @ColumnInfo
    val headline: String

)