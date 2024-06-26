package com.example.clearsky.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// https://developer.android.com/training/data-storage/room
// https://developer.android.com/codelabs/android-room-with-a-view-kotlin#7
@Database(entities = [WeatherEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "weather_database")
                .fallbackToDestructiveMigration()
                .build()
    }
}