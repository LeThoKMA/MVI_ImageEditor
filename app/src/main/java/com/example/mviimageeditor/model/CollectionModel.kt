package com.example.mviimageeditor.model

import androidx.compose.runtime.Stable
import androidx.core.text.HtmlCompat
import com.google.gson.annotations.SerializedName

@Stable
data class CollectionModel(
    @SerializedName("cover_photo")
    val coverPhoto: com.example.mviimageeditor.model.CoverPhoto,
    @SerializedName("description")
    val description: String?,
    @SerializedName("featured")
    val featured: Boolean,
    @SerializedName("id")
    val id: String,
    @SerializedName("last_collected_at")
    val lastCollectedAt: String,
    @SerializedName("links")
    val links: com.example.mviimageeditor.model.Links,
    @SerializedName("preview_photos")
    val previewPhotos: List<com.example.mviimageeditor.model.PreviewPhoto>,
    @SerializedName("private")
    val `private`: Boolean,
    @SerializedName("published_at")
    val publishedAt: String,
    @SerializedName("share_key")
    val shareKey: String,
    @SerializedName("tags")
    val tags: List<com.example.mviimageeditor.model.Tag>,
    @SerializedName("title")
    val title: String,
    @SerializedName("total_photos")
    val totalPhotos: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user")
    val user: com.example.mviimageeditor.model.User,
    var isLiked: Boolean = false,
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
