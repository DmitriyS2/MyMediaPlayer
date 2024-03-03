package ru.netology.mymediaplayer

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class TrackRepositoryImpl:TrackRepository {

    private val gson = Gson()
    private val trackType: Type = object : TypeToken <DataMedia>(){}.type
    private val client:OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun getAlbum():DataMedia? =
         try {
            val request = Request.Builder()
                .url(BASE_URL)
                .build()
            val call = client.newCall(request)
            val response = call.execute()
            val responseString = response.body?.string()
            gson.fromJson(responseString, trackType)
        } catch (e:Exception) {
            e.printStackTrace()
            null
        }

    companion object {
        const val BASE_URL = "https://github.com/netology-code/andad-homeworks/raw/master/09_multimedia/data/album.json"
    }

}