package ru.netology.mymediaplayer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {

    private val repository: TrackRepository = TrackRepositoryImpl()

    val dataMedia: MutableLiveData<DataMedia> = MutableLiveData<DataMedia>()

    val listDataItemTrack: MutableLiveData<List<DataItemTrack>> =
        MutableLiveData<List<DataItemTrack>>()

    //   val selectedTrack: MutableLiveData<String>? = null

    val selectedTrack
        get() = listDataItemTrack?.map {
            it.filter { data ->
                data.isChecked
            }.map { item ->
                item.name
            }.firstOrNull()
        }


    init {
        getAlbum()

    }

    fun getAlbum() {
        thread {
            //   val repo = TrackRepositoryImpl()
            //   listTracks.postValue(dataMedia.value?.tracks)
            //    val list = repository.getAlbum()
            //  dataMediaT = repository.getAlbum()
            dataMedia.postValue(repository.getAlbum())
//            val firstFile = list.tracks[0].file

            Log.d(
                "MyLog",
                "1.dataMedia = ${dataMedia.value}, listtracks=${dataMedia.value?.tracks}"
            )
        }
        Log.d("MyLog", "2.dataMedia = ${dataMedia.value}, tracks=${dataMedia.value?.tracks}")
    }


    fun highlite(dataItemTrack: DataItemTrack) {

        if (dataItemTrack.isChecked) {
            listDataItemTrack.value = listDataItemTrack.value?.let {
                it.map { data ->
                    if (data.id == dataItemTrack.id) {
                        data.copy(isChecked = !dataItemTrack.isChecked)
                    } else {
                        data
                    }
                }
            }
        } else {
            listDataItemTrack.value = listDataItemTrack.value?.let {
                it.map { data ->
                    if (data.id == dataItemTrack.id) {
                        data.copy(isChecked = !dataItemTrack.isChecked)
                    } else {
                        data.copy(isChecked = false)
                    }
                }
            }
        }


    }
}