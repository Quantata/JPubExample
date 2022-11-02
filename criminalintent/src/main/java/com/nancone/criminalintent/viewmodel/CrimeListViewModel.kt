package com.nancone.criminalintent.viewmodel

import androidx.lifecycle.ViewModel
import com.nancone.criminalintent.dao.CrimeRepository
import com.nancone.criminalintent.model.Crime
import com.nancone.criminalintent.model.CrimeType

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()
//    val crimes = crimeRepository.getCrimes()

//    val crimes = mutableListOf<Crime>()
//
//    init {
//        for (i in 0 until 100) {
//            val crime = Crime()
//            crime.title = "Crime #$i"
//            crime.isSolved = i % 2 == 0
//            crime.requiresPolice = if(i%3 == 0) CrimeType.requiresPoliceCrime else CrimeType.normalCrime
//            crimes += crime
//        }
//    }
}