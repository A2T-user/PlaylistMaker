package com.a2t.myapplication.di

import android.content.Context
import androidx.room.Room
import com.a2t.myapplication.mediateca.data.db.AppDatabase
import com.a2t.myapplication.search.data.NetworkClient
import com.a2t.myapplication.search.data.network.ItunesApi
import com.a2t.myapplication.search.data.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    // для Search
    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }
    // для Search и Settings
    single {
        androidContext()
            .getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
    }
    // для Search
    factory { Gson() }
    // для Search
    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }
    // для базы данных
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}