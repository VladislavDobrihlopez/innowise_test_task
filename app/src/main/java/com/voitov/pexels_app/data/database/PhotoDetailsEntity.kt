package com.voitov.pexels_app.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity("photo_details")
data class PhotoDetailsEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: Int,
    @ColumnInfo("network_url")
    val networkUrl: String,
    @ColumnInfo(name = "author")
    val author: String? = null,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo("local_url")
    val localUrl: String? = null,
    @ColumnInfo(name = "is_download")
    val isDownload: Boolean = false,
    @ColumnInfo(name = "is_bookmarked")
    val isBookmarked: Boolean = false,
)