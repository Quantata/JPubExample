package com.nancone.criminalintent.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nancone.criminalintent.R

class MainActivity : AppCompatActivity() {
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
}