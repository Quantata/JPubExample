package com.nancone.criminalintent.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nancone.criminalintent.model.Crime
import com.nancone.criminalintent.viewmodel.CrimeListViewModel

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    // CrimeListViewModel 참조
    // ViewModel이 Fragment와 같이 사용되면 ViewModel의 생명주기는 Fragment의 생명주기를 따라감
    private val crimeListViewModel: CrimeListViewModel by lazy {
        // ViewModelProvider(this): 현재의 CrimeListFragment 인스턴스와 연관된 ViewModelProvider 인스턴스를 생성하고 반환
        // get(CrimeListViewModel::class.java): CrimeListViewModel 인스턴스 반환
        ViewModelProvider(this)[CrimeListViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Total crimes: ${crimeListViewModel.crimes.size}")
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}