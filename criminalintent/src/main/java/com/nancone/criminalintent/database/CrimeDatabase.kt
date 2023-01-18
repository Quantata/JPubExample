package com.nancone.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nancone.criminalintent.dao.CrimeDao
import com.nancone.criminalintent.model.Crime

@Database(entities = [ Crime::class ], version = 2)
@TypeConverters(CrimeTypeConverters::class) // TypeConverter 추가
abstract class CrimeDatabase : RoomDatabase() {
    abstract fun crimeDao() : CrimeDao
}


val migration_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime Add COLUMN suspect TEXT NOT NULL DEFAULT''"
        )
    }
}