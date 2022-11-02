package com.nancone.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nancone.criminalintent.dao.CrimeDao
import com.nancone.criminalintent.model.Crime

@Database(entities = [ Crime::class ], version = 3)
@TypeConverters(CrimeTypeConverters::class) // TypeConverter 추가
abstract class CrimeDatabase : RoomDatabase() {
    abstract fun crimeDao() : CrimeDao
}