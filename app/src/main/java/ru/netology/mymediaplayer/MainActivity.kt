package ru.netology.mymediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.mymediaplayer.databinding.ActivityMainBinding

var flagPlay: Boolean = false
var firstStart: Boolean = true

class MainActivity : AppCompatActivity() {

    private val mediaObserver = MediaLifecycleObserver()

    private val viewModel: MainViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.window.statusBarColor = this.getColor(R.color.grey)

        val adapter = TrackAdapter(object : Listener {
            override fun highlight(dataItemTrack: DataItemTrack) {
                flagPlay = false
                firstStart = true
                binding.buttonPlay.setImageResource(if (flagPlay) R.drawable.baseline_pause_80 else R.drawable.baseline_play_80)
                mediaObserver.stopTrack()
                viewModel.highlight(dataItemTrack)
            }
        })

        binding.rwTracks.layoutManager = LinearLayoutManager(this)
        binding.rwTracks.adapter = adapter

        lifecycle.addObserver(mediaObserver)

        binding.buttonPlay.setImageResource(if (flagPlay) R.drawable.baseline_pause_80 else R.drawable.baseline_play_80)

        binding.buttonPlay.setOnClickListener {
            if (!flagPlay) {
                //первый старт
                if (firstStart) {
                    mediaObserver.firstStartPlay(viewModel.selectedTrack?.value.toString())
                    showText("playing")
                    //продолжение после паузы
                } else {
                    mediaObserver.notFirstStartPlay()
                    showText("playing again")
                }
                flagPlay = true
                //пауза
            } else {
                mediaObserver.pauseTrack()
                showText("pause")
            }

            mediaObserver.player?.setOnCompletionListener {
                mediaObserver.stopTrack()
                viewModel.goToNextTrack()
                mediaObserver.firstStartPlay(viewModel.selectedTrack?.value.toString())
                showText("playing")
            }

            viewModel.changeImageTrack(viewModel.selectedTrack?.value, flagPlay)
            binding.buttonPlay.setImageResource(if (flagPlay) R.drawable.baseline_pause_80 else R.drawable.baseline_play_80)
        }

        viewModel.dataMedia.observe(this) {
            it?.let {
                Log.d("MyLog", "MainActivity dataMedia from observe=$it, tracks=${it.tracks}")
                val listDataItemTrack = mutableListOf<DataItemTrack>()
                viewModel.dataMedia.value?.tracks?.forEach { track ->
                    listDataItemTrack.add(
                        DataItemTrack(
                            id = track.id,
                            name = track.file,
                            album = it.title
                        )
                    )
                }
                viewModel.listDataItemTrack.value = listDataItemTrack
                binding.nameAlbum.text = it.title
                binding.nameArtist.text = it.artist
                binding.published.text = it.published
                binding.genre.text = it.genre
            }
        }

        viewModel.listDataItemTrack.observe(this) {
            val list = it ?: emptyList()
            adapter.trackList = list
            adapter.submitList(list)

            Log.d("MyLog", "MainActivity listDataItemTrack from observe=$it")
        }

        viewModel.selectedTrack?.observe(this) {
            val text = it ?: "Выберите\nкомпозицию"
            binding.chooseTrack.text = text
            binding.buttonPlay.isEnabled = it != null

            Log.d("MyLog", "MainActivity selectedTrack from observe=$it")
        }

//            mediaObserver.apply {
//                player?.setDataSource(
//                    "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/+${viewModel.selectedTrack?.value.toString()}"
//                )
//            }.play()
        /*
        findViewById<Button>(R.id.play).setOnClickListener {
            MediaPlayer.create(this, R.raw.ring).apply {
                setOnCompletionListener {
                    it.release()
                }
            }.start()
        }
*/
        /*
                findViewById<Button>(R.id.play).setOnClickListener {
                    mediaObserver.apply {
                        resources.openRawResourceFd(R.raw.ring).use { afd ->
                            player?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        }
                    }.play()
                }
        */
//        findViewById<VideoView>(R.id.video).apply {
//            setMediaController(MediaController(this@AppActivity))
//            setVideoURI(
//                Uri.parse("https://archive.org/download/BigBuckBunny1280x720Stereo/big_buck_bunny_720_stereo.mp4")
//            )
//            setOnPreparedListener {
//                start()
//            }
//            setOnCompletionListener {
//                stopPlayback()
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        binding.buttonPlay.setImageResource(if (flagPlay) R.drawable.baseline_pause_80 else R.drawable.baseline_play_80)
        viewModel.changeImageTrack(viewModel.selectedTrack?.value, flagPlay)
    }

    private fun showText(text: String) {
        Toast.makeText(
            this,
            "${viewModel.selectedTrack?.value.toString()} $text",
            Toast.LENGTH_SHORT
        ).show()
    }
}