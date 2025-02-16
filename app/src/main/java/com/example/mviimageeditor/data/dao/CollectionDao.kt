package com.example.mviimageeditor.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mviimageeditor.model.CollectionModel

@Dao
interface CollectionDao {
    @Query("SELECT * FROM CollectionModel")
    fun getAll(): PagingSource<Int, CollectionModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(collections: List<CollectionModel>)
}
