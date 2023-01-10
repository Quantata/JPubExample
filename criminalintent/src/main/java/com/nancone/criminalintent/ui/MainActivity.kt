package com.nancone.criminalintent.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.nancone.criminalintent.R
import java.util.*

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FragmentManager 에 Fragment 를 관리하도록 넘겨주는 코드
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if(currentFragment == null) {
                val fragment = CrimeListFragment.newInstance()
            // fragment transaction 을 생성하고 커밋함.
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onCrimesSelected(crimeId: UUID) {
        Log.d(TAG, "onCrimesSelected: $crimeId")
        val fragment = CrimeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // MainActivity 가 아닌 이전 화면이 나올 수 있도록 구현
            .commit()
    }
}