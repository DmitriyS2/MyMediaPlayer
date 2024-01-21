package ru.netology.mymediaplayer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mymediaplayer.databinding.ItemTrackBinding

interface Listener {
    fun highlight(dataItemTrack: DataItemTrack)
}

class TrackAdapter(private val listener: Listener) :
    ListAdapter<DataItemTrack, TrackAdapter.TrackHolder>(TrackDiffCallback()) {

    var trackList = emptyList<DataItemTrack>()

    class TrackHolder(item: View, private val listener: Listener) : RecyclerView.ViewHolder(item) {
        val binding = ItemTrackBinding.bind(item)

        fun bind(payload: Payload) {
            payload.isChecked?.also { isChecked ->
                binding.cardView1.getBackground()
                    .setTint(if (isChecked) Color.YELLOW else Color.WHITE)
            }
        }

        fun bind(dataItemTrack: DataItemTrack) {
            binding.apply {
                imagePlayOrPause.setImageResource(if (dataItemTrack.isPlaying) R.drawable.baseline_pause_48 else R.drawable.baseline_play_48)

                textNameTrack.text = dataItemTrack.name
                textNameAlbum.text = dataItemTrack.album

                cardView1.getBackground()
                    .setTint(if (dataItemTrack.isChecked) Color.YELLOW else Color.WHITE)

                cardView2.setOnClickListener {
                    listener.highlight(dataItemTrack)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackHolder(view, listener)
    }

    override fun onBindViewHolder(
        holder: TrackHolder,
        position: Int,
        payloads: List<Any>
    ) {
        //          if (payloads.isEmpty()) {
        onBindViewHolder(holder, position)
//        } else {
//            payloads.forEach {
//                if (it is Payload) {
//                    holder.bind(it)
//                }
//            }
//        }
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<DataItemTrack>() {
    override fun areItemsTheSame(oldItem: DataItemTrack, newItem: DataItemTrack): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItemTrack, newItem: DataItemTrack): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DataItemTrack, newItem: DataItemTrack): Any =
        Payload(
//            isChecked = newItem.isChecked.takeIf { oldItem.isChecked != it }
        )

}

data class Payload(
    val id: Int? = null,
    val isChecked: Boolean? = null
)