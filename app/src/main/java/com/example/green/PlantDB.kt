package com.example.green


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//synchronized DB singleton
@Database(entities = [(PlantModel::class)], version = 3, exportSchema = false)
abstract class PlantDB: RoomDatabase() {
    abstract fun plantDao(): PlantDao
    companion object {
        private var sInstance: PlantDB? = null
        @Synchronized
        fun get(context: Context): PlantDB {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.applicationContext,
                        PlantDB::class.java,
                        "plant1").build()
            }
            return sInstance!!
        }
    }
}