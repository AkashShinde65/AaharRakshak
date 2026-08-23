package com.aaharrakshak.mobile.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [OfflineComplaintDraftEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AaharRakshakDatabase : RoomDatabase() {
    abstract fun complaintDraftDao(): ComplaintDraftDao

    companion object {
        fun create(context: Context): AaharRakshakDatabase = Room.databaseBuilder(
            context.applicationContext,
            AaharRakshakDatabase::class.java,
            "aahar-rakshak.db"
        ).build()
    }
}
