package com.example.mviimageeditor.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mviimageeditor.data.dao.CollectionDao
import com.example.mviimageeditor.model.CollectionModel

@Database(
    version = 1,
    entities = [CollectionModel::class],
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
}
