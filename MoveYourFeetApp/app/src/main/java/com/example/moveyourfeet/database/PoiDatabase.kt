package com.example.moveyourfeet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
////Alexander Schröder 4schoa24///
@Database(entities = arrayOf(PoiDataEntity::class), version = 1, exportSchema = false)
abstract class PoiDatabase : RoomDatabase() {
    abstract fun PoiDao(): PoiDao


    companion object {
        private var instance: PoiDatabase? = null

        fun getDatabase(ctx: Context): PoiDatabase {
            var tempInstance = instance
            if (tempInstance == null){
                tempInstance = Room.databaseBuilder(ctx.applicationContext,PoiDatabase::class.java,"poiDatabase").build()
                instance = tempInstance
            }
            return tempInstance
        }
    }
}