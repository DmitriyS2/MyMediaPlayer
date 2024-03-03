package ru.netology.mymediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
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
        binding.buttonPlay.isEnabled = viewModel.dataModel.value?.dataMedia != null
        binding.buttonPlay.setOnClickListener {
            if (!flagPlay) {
                //первый старт
                if (firstStart) {
                    mediaObserver.firstStartPlay(viewModel.selectedTrack.value.toString())
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
                mediaObserver.firstStartPlay(viewModel.selectedTrack.value.toString())
                showText("playing")
            }

            viewModel.changeImageTrack(viewModel.selectedTrack.value, flagPlay)
            binding.buttonPlay.setImageResource(if (flagPlay) R.drawable.baseline_pause_80 else R.drawable.baseline_play_80)
        }

        viewModel.dataModel.observe(this) {
            it?.let { modelData ->
                binding.progress.isVisible = modelData.loading
                binding.errorGroup.isVisible = modelData.error
                modelData.dataMedia?.let { dataMedia ->
                    val listDataItemTrack = mutableListOf<DataItemTrack>()
                    dataMedia.tracks.forEach { track ->
                        listDataItemTrack.add(
                            DataItemTrack(
                                id = track.id,
                                name = track.file,
                                album = dataMedia.title
                            )
                        )
                    }
                    viewModel.listDataItemTrack.value = listDataItemTrack
                    binding.nameAlbum.text = dataMedia.title
                    binding.nameArtist.text = dataMedia.artist
                    binding.published.text = dataMedia.published
                    binding.genre.text = dataMedia.genre
                }
            }
        }

        binding.retryButton.setOnClickListener {
            viewModel.getAlbum()
        }

        viewModel.listDataItemTrack.observe(this)
        {
            val list = it ?: emptyList()
            adapter.trackList = list
            adapter.submitList(list)
        }

        viewModel.selectedTrack.observe(this)
        {
            val text = it ?: "Выберите\nкомпозицию"
            binding.chooseTrack.text = text
            binding.buttonPlay.isEnabled = it != null
        }

    }

    override fun onResume() {
        super.onResume()
        binding.buttonPlay.setImageResource(if (flagPlay) R.drawable.baseline_pause_80 else R.drawable.baseline_play_80)
        viewModel.changeImageTrack(viewModel.selectedTrack.value, flagPlay)
    }

    private fun showText(text: String) {
        Toast.makeText(
            this,
            "${viewModel.selectedTrack.value.toString()} $text",
            Toast.LENGTH_SHORT
        ).show()
    }
}