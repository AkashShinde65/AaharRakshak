package com.aaharrakshak.mobile.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDraftDao {
    @Query("SELECT * FROM complaint_drafts ORDER BY updatedAtEpochMs DESC")
    fun observeDrafts(): Flow<List<OfflineComplaintDraftEntity>>

    @Query("SELECT * FROM complaint_drafts ORDER BY updatedAtEpochMs DESC")
    suspend fun drafts(): List<OfflineComplaintDraftEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(draft: OfflineComplaintDraftEntity): Long

    @Update
    suspend fun update(draft: OfflineComplaintDraftEntity)

    @Delete
    suspend fun delete(draft: OfflineComplaintDraftEntity)
}
