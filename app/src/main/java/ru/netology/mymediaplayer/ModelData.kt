package ru.netology.mymediaplayer

data class ModelData(
    val dataMedia: DataMedia? = null,
    val loading: Boolean = false,
    val error: Boolean = false
)