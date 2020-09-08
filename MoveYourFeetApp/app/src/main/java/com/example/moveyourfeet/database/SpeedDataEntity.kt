package com.example.moveyourfeet.database

import androidx.room.Entity
import androidx.room.PrimaryKey
////Alexander Schröder 4schoa24///
@Entity(tableName="speedValues")
data class SpeedDataEntity(@PrimaryKey(autoGenerate = true)val id: Long, var startHour: Double, var startMinute: Double,  var speed: Double) {

}