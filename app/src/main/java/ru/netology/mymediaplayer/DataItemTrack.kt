package ru.netology.mymediaplayer

data class DataItemTrack(
    val id:Int,
    val name:String,
    val album:String,
    var isChecked:Boolean = false,
    var isPlaying:Boolean = false
)
