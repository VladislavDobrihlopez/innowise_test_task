package com.voitov.pexels_app.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("photo_details")
data class PhotoDetailsEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: Int,
    @ColumnInfo("source_url")
    val sourceUrl: String, // coil use it as a key and chooses the source of the photo receive
    @ColumnInfo(name = "author")
    val author: String? = null,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "query")
    val query: String,
    @ColumnInfo(name = "is_bookmarked")
    val isBookmarked: Boolean = false,
)