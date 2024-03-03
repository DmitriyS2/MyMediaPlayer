package ru.netology.mymediaplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

import kotlin.Exception
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {

    private val repository: TrackRepository = TrackRepositoryImpl()

    val dataModel: MutableLiveData<ModelData> = MutableLiveData<ModelData>()

    val listDataItemTrack: MutableLiveData<List<DataItemTrack>> =
        MutableLiveData<List<DataItemTrack>>()

    val selectedTrack
        get() = listDataItemTrack.map {
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
            try {
                dataModel.postValue(ModelData(loading = true))
                val data = repository.getAlbum()
                dataModel.postValue(ModelData(dataMedia = data, error = data==null))

            } catch (e: Exception) {
                e.printStackTrace()
                dataModel.postValue(ModelData(error = true))

            }
        }
    }

    fun highlight(dataItemTrack: DataItemTrack) {
        //снимаем выделение
        if (dataItemTrack.isChecked) {
            listDataItemTrack.value = listDataItemTrack.value?.let {
                it.map { data ->
                    if (data.id == dataItemTrack.id) {
                        data.copy(isChecked = false, isPlaying = false)
                    } else {
                        data
                    }
                }
            }
        } else {
            //выделяем
            listDataItemTrack.value = listDataItemTrack.value?.let {
                it.map { data ->
                    if (data.id == dataItemTrack.id) {
                        data.copy(isChecked = true)
                    } else {
                        data.copy(isChecked = false, isPlaying = false)
                    }
                }
            }
        }
    }

    fun changeImageTrack(name: String?, flag: Boolean) {
        name?.let {
            //играется
            if (flag) {
                listDataItemTrack.value = listDataItemTrack.value?.let {
                    it.map { data ->
                        if (data.name == name) {
                            data.copy(isPlaying = true)
                        } else {
                            data.copy(isPlaying = false)
                        }
                    }
                }
                //не играется
            } else {
                listDataItemTrack.value = listDataItemTrack.value?.let {
                    it.map { data ->
                        if (data.name == name) {
                            data.copy(isPlaying = false)
                        } else {
                            data
                        }
                    }
                }
            }
        }
    }

    fun goToNextTrack() {
        val maxId = listDataItemTrack.value?.maxByOrNull {
            it.id
        }?.id ?: 0

        val currentId = listDataItemTrack.value?.filter {
            it.name == selectedTrack.value
        }?.map { data ->
            data.id
        }?.firstOrNull() ?: 0

        val newId = if (currentId == maxId) 1 else currentId + 1

        val newTrack = listDataItemTrack.value?.firstOrNull {
            it.id == newId
        }

        val newName = newTrack?.name

        changeImageTrack(newName, true)

        newTrack?.let {
            highlight(it)
        }
    }
}