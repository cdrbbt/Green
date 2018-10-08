package com.example.green

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.util.Log
import com.huma.room_for_asset.RoomAsset

@Database(entities = [(PlantModel::class)], version = 3, exportSchema = false)
abstract class PlantDB: RoomDatabase() {
    abstract fun plantDao(): plantDao
    companion object {
        private var sInstance: PlantDB? = null
        @Synchronized
        fun get(context: Context): PlantDB {
            if (sInstance == null) {
                Log.d("CREATING", "PLANTS HERE")
                sInstance = Room.databaseBuilder(context.applicationContext,
                        PlantDB::class.java,
                        "plant1").build()
            }
            return sInstance!!
        }


    }
}