package com.example.moveyourfeet.database


import androidx.room.*
////Alexander Schröder 4schoa24///
@Dao
interface PoiDao {
    @Query("SELECT * FROM pointsOfInterest")
    fun getAllPoi(): List<PoiDataEntity>

    @Insert
    fun insertPoi(poi: PoiDataEntity): Long

    @Query("DELETE FROM pointsOfInterest WHERE title=:title AND latitude=:latitude AND longitude=:longitude")
    fun deleteByTitleAndLoc(title: String, latitude: Double, longitude: Double)
}