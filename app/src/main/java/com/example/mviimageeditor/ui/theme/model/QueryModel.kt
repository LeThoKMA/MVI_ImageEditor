package com.example.imageEditor2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QueryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("content")
    val content: String,
)
