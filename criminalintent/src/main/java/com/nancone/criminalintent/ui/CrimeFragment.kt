package com.nancone.criminalintent.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.nancone.criminalintent.R
import com.nancone.criminalintent.model.Crime
import java.text.DateFormat
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
class CrimeFragment : Fragment() {
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    /**
     * Fragment 에서는 View 를 onCreate 에서 inflate(객체화 시켜 메모리에 올려놓는 것(layout 을 원하는 것으로 변경 가능)) 하지 않음.
     * onCreateView 에서 생성하고 구성함
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        Log.d(TAG, "onCreate: args bundle crime ID: $crimeId")
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
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }

        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        return view
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                // 비워둠
            }

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // 비워둠.
            }
        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }
        }
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}