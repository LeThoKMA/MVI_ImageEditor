package com.example.mviimageeditor.data

import androidx.room.TypeConverter
import com.example.mviimageeditor.model.CoverPhoto
import com.example.mviimageeditor.model.PreviewPhoto
import com.example.mviimageeditor.model.Urls
import com.example.mviimageeditor.model.User
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromUrls(urls: Urls): String = gson.toJson(urls)

    @TypeConverter
    fun toUrls(json: String): Urls = gson.fromJson(json, Urls::class.java)

    @TypeConverter
    fun fromUser(users: User): String = gson.toJson(users)

    @TypeConverter
    fun toUser(json: String): User = gson.fromJson(json, User::class.java)

    @TypeConverter
    fun fromPreviewPhoto(previewPhoto: PreviewPhoto): String = gson.toJson(previewPhoto)

    @TypeConverter
    fun toPreviewPhoto(json: String): PreviewPhoto = gson.fromJson(json, PreviewPhoto::class.java)

    @TypeConverter
    fun fromCoverPhoto(coverPhoto: CoverPhoto): String = gson.toJson(coverPhoto)

    @TypeConverter
    fun toCoverPhoto(json: String): CoverPhoto = gson.fromJson(json, CoverPhoto::class.java)
}
