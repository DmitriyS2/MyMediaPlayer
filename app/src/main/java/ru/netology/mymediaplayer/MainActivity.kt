package ru.netology.mymediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.mymediaplayer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val mediaObserver = MediaLifecycleObserver()

    private val viewModel:MainViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = TrackAdapter(object : Listener {
            override fun highlite(dataItemTrack: DataItemTrack) {
                viewModel.highlite(dataItemTrack)
            }

        })

        binding.rwTracks.layoutManager = LinearLayoutManager(this)
        binding.rwTracks.adapter = adapter

        lifecycle.addObserver(mediaObserver)

        binding.buttonPlay.setOnClickListener {
            viewModel.getAlbum()

//            mediaObserver.apply {
//                player?.setDataSource(
//                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
//                )
//            }.play()
        }

        viewModel.dataMedia.observe(this) {
            it?.let {
                Log.d("MyLog", "3.dataMedia=$it, tracks=${it.tracks}")
                val listDataItemTrack= mutableListOf<DataItemTrack>()
                viewModel.dataMedia.value?.tracks?.forEach {track ->
                    listDataItemTrack.add(DataItemTrack(id=track.id, name = track.file, album = it.title))
                }
                viewModel.listDataItemTrack.value = listDataItemTrack
                binding.nameAlbum.text = it.title
                binding.nameArtist.text = it.artist
                binding.published.text = it.published
                binding.genre.text = it.genre
                Log.d("MyLog", "t=$listDataItemTrack")
            }



//            adapter.trackList = listDataItemTrack
//            adapter.submitList(listDataItemTrack)
        }

        viewModel.listDataItemTrack.observe(this) {
            val list = it ?: emptyList()
                adapter.trackList = list
                adapter.submitList(list)

            Log.d("MyLog", "5.listDataItemTrack=$it")
        }

        viewModel.selectedTrack?.observe(this) {
        val text = it ?: "Выберите\nкомпозицию"
            binding.chooseTrack.text = text
        }

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
}