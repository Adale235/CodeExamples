package com.example.moveyourfeet.database

import android.content.Context
import androidx.room.*
////Alexander Schröder 4schoa24///
@Database(entities = arrayOf(SpeedDataEntity::class), version = 1, exportSchema = false)
abstract class SpeedDatabase : RoomDatabase(){

    abstract fun speedDataDao(): SpeedDataDao


    companion object {
        private var instance: SpeedDatabase? = null

        fun getDatabase(ctx: Context): SpeedDatabase {
            var tempInstance = instance
            if (tempInstance == null){
                tempInstance = Room.databaseBuilder(ctx.applicationContext,SpeedDatabase::class.java,"speedDatabase").build()
                instance = tempInstance
            }
            return tempInstance
        }
    }
}