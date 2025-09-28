package com.example.mviimageeditor.model

import androidx.core.text.HtmlCompat
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class CollectionModel(
    @SerializedName("cover_photo")
    val coverPhoto: CoverPhoto,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("preview_photos")
    val previewPhotos: List<PreviewPhoto>,
    @SerializedName("user")
    val user: User,
    val isLiked: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val idLocal: Int = 0,
) {
    val descriptionTextShow
        get() =
            description?.let {
                HtmlCompat.fromHtml(
                    "<b>${user.username}</b> $description",
                    HtmlCompat.FROM_HTML_MODE_LEGACY,
                )
            }
}
