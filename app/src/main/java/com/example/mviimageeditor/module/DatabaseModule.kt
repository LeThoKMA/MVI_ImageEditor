package com.example.mviimageeditor.module

import android.content.Context
import androidx.room.Room.databaseBuilder
import com.example.mviimageeditor.data.AppDatabase

object DatabaseModule {
    internal fun provideDatabase(context: Context) =
        databaseBuilder(
            context,
            AppDatabase::class.java,
            "imgEdt-database",
        ).fallbackToDestructiveMigration().build()

    internal fun provideCollectionDao(db: AppDatabase) = db.collectionDao()
}
