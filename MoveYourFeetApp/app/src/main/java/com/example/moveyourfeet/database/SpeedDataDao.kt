package com.example.moveyourfeet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
////Alexander Schröder 4schoa24///
@Dao
interface SpeedDataDao {
    @Query("SELECT * FROM speedValues")
    fun getAllSpeeds(): List<SpeedDataEntity>

    @Query("DELETE FROM speedValues")
    fun deleteAllStatistics()

    @Query("SELECT * FROM speedValues WHERE id=(SELECT MAX(id) FROM speedValues)")
    fun getMaxId(): SpeedDataEntity?

    @Query("SELECT * FROM speedValues WHERE id=(SELECT MIN(id) FROM speedValues)")
    fun getMinId(): SpeedDataEntity?

    @Insert
    fun insertSpeedValue(speed: SpeedDataEntity): Long
}