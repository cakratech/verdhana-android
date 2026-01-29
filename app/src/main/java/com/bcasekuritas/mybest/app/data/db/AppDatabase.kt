package com.bcasekuritas.mybest.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bcasekuritas.mybest.app.data.dao.*
import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.data.entity.BiometricObject
import com.bcasekuritas.mybest.app.data.entity.FilterRunningTradeObject
import com.bcasekuritas.mybest.app.data.entity.OrderReplyObject
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.data.entity.SourceObject
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.ext.converter.Converters

@Database(
    entities = [
        SourceObject::class,
        AccountObject::class,
        StockParamObject::class,
        OrderReplyObject::class,
        StockNotationObject::class,
        BiometricObject::class,
        SessionObject::class,
        FilterRunningTradeObject::class],
    version = 14, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
    abstract fun accountDao(): AccountDao
    abstract fun stockParamDao(): StockParamDao
    abstract fun sessionDao(): SessionDao
    abstract fun orderReplyDao(): OrderReplyDao
    abstract fun stockNotationDao(): StockNotationDao
    abstract fun biometricDao(): BiometricDao
    abstract fun filterDao(): FilterDao

    companion object {
        // For Singleton instantion
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "project.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}