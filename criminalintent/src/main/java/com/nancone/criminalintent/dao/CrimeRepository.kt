package com.nancone.criminalintent.dao

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.nancone.criminalintent.database.CrimeDatabase
import com.nancone.criminalintent.model.Crime
import java.util.*
import java.util.concurrent.Executors

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
    private val crimeDao = database.crimeDao() // 정의한 인터페이스를 기반으로 Room이 함수를 자동생성
    private val executor = Executors.newSingleThreadExecutor() // 새로운 스레드를 참조하는 executor 반환


    // LiveData를 반환하여 백그라운드에서 쿼리를 자동으로 수행시키고 LiveData가 해당 데이터를 mainThread로 전달 -> UI 변경 가능
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID) : LiveData<Crime?> = crimeDao.getCrime(id)

    // LiveData를 반환하는 것이 아니므로 Room이 백그라운드 스레드로 자동으로 실행하지 않음. 그래서 executor를 이용하여 백그라운드 스레드에서 실행
    fun updateCrime(crime: Crime) {
        executor.execute{   //
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }
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