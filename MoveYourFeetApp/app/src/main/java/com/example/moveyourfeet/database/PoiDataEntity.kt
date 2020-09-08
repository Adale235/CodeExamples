package com.example.moveyourfeet.database

import androidx.room.Entity
import androidx.room.PrimaryKey
////Alexander Schröder 4schoa24///
@Entity(tableName="pointsOfInterest")
data class PoiDataEntity(@PrimaryKey(autoGenerate = true)val id: Long, var title: String, var snippet: String, var latitude: Double, var longitude: Double) {
}