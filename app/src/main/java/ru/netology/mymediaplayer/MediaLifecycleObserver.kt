package ru.netology.mymediaplayer

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class MediaLifecycleObserver : LifecycleEventObserver {
    var player: MediaPlayer? = MediaPlayer()

    fun play() {
        player?.setOnPreparedListener {
            it.start()
        }
        player?.prepareAsync()
    }

    fun firstStartPlay(nameTrack: String) {
        firstStart = false

        if (player == null) {
            player = MediaPlayer()
        }
        player?.setDataSource("https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/$nameTrack")
        Log.d(
            "MyLog",
            "URL=https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/$nameTrack"
        )
        play()
    }

    fun notFirstStartPlay() {
        player?.currentPosition?.let { current ->
            player?.seekTo(current)
        }
        player?.start()
    }

    fun pauseTrack() {
        player?.pause()
        flagPlay = false
    }

    fun stopTrack() {
        player?.stop()
        player?.reset()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                Log.d("MyLog", "ON_RESUME")
            }

            Lifecycle.Event.ON_PAUSE -> {
                player?.pause()
                flagPlay = false
                Log.d("MyLog", "ON_PAUSE")
            }

            Lifecycle.Event.ON_STOP -> {
                player?.release()
                player = null
                flagPlay = false
                firstStart = true
                Log.d("MyLog", "ON_STOP")
            }

            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> Unit
        }
    }
}