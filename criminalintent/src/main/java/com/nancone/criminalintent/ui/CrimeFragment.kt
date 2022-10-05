package com.nancone.criminalintent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nancone.criminalintent.R
import com.nancone.criminalintent.model.Crime

class CrimeFragment : Fragment() {
    private lateinit var crime: Crime

    /**
     * Fragment 에서는 View 를 onCreate 에서 inflate(객체화 시켜 메모리에 올려놓는 것(layout 을 원하는 것으로 변경 가능)) 하지 않음.
     * onCreateView 에서 생성하고 구성함
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    /**
     * Fragment View 의 layout 을 inflate 한 후 inflate 된 view 를 호스팅 액티비티에 반환해야 함.
     * Bundle 은 저장된 상태 데이터 가지며 View 를 재생성하는데 사용
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /**
         * @resource : inflate 함수에 R.layout.fragment_crime 을 인자로 전달하여 fragment view 를 명시적으로 inflate 함.
         * @viewGroup : 위젯들을 올바르게 구성하는 데 필요한 view 의 부모
         * @attachRoot: inflate 된 view 를 이 view 의 부모에게 즉시 추가할 것인지 LayoutInflater 에 알려줌.
         *              여기서는 현재 false : why? 이 fragment 의 view 는 activity 의 container view 에 호스팅되기 때문.
         *              즉, 이 fragment 의 view 는 inflate 되는 즉시 부모 view 에 추가할 필요가 없으며,
         *              Activity 가 나중이 이 view 를 추가한다.
         */
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }
}