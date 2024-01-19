package ru.netology.mymediaplayer

data class DataMedia(
    val artist: String,
    val genre: String,
    val id: Int,
    val published: String,
    val subtitle: String,
    val title: String,
    val tracks: List<Track>
)