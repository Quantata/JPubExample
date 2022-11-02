package com.nancone.criminalintent

import android.app.Application
import com.nancone.criminalintent.dao.CrimeRepository

class CriminalIntentApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this) // 초기화
    }
}