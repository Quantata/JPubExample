package com.nancone.criminalintent.dao

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.nancone.criminalintent.database.CrimeDatabase
import com.nancone.criminalintent.model.Crime
import java.util.*

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context) {

    // database 객체 참조
    private val database : CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    )
//        .fallbackToDestructiveMigration()
        .build()

    // dao 객체 참조
    private val crimeDao = database.crimeDao()

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID) : LiveData<Crime?> = crimeDao.getCrime(id)

    companion object {
        private var INSTANCE: CrimeRepository? = null

        // constructor 를 private 으로 해놓음으로서 initialize를 통해서만 CrimeRepository 인스턴스 생성
        fun initialize(context: Context) {
            if(INSTANCE == null)
                INSTANCE = CrimeRepository(context)
        }

        fun get() : CrimeRepository = INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
    }
}