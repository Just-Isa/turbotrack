package com.kidrich.turbotrack

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity()
@TypeConverters(TimestampConverter::class)
data class Meal (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Date,
    val calories: Int,
    val isSnack: Boolean
)

class TimestampConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}