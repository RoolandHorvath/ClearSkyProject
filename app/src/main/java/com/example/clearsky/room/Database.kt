package com.example.clearsky.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Room database configuration. More details: https://developer.android.com/training/data-storage/room
@Database(entities = [WeatherEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        // Singleton pattern to get instance of database. For implementation guidance, see:
        // https://developer.android.com/codelabs/android-room-with-a-view-kotlin#7
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        // Builds the Room database. See https://developer.android.com/training/data-storage/room
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "weather_database")
                .fallbackToDestructiveMigration()
                .build()
    }
}