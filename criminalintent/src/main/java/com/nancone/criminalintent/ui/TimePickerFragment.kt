package com.nancone.criminalintent.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import java.util.*

/**
 * DialogFragment 없이 DatePickerDialog를 보여줄 수 있지만
 * DitePickerDialog만 사용할때 장치가 회전했을 경우 화면에서 사라짐
 * 반면에, DatePickerDialog가 Fragment에 포함되면 장치회전 후에도 대화상자가 다시 생성되어 화면에 다시 나타남
 *
 * TimePickerFragment는 MainActivity에 의해서 호스팅될 것임
 */
const val ARG_TIME = "time"
class TimePickerFragment : DialogFragment() {

    interface Callbacks {
        fun onTimeSelected(date: Date)
    }

    @RequiresApi(Build.VERSION_CODES.N) // 24 이상
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(ARG_DATE) as Date

        val calendar = Calendar.getInstance()
        calendar.time = date
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timeListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.time = date
//            calendar.set(calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH),
//                        calendar.get(Calendar.HOUR_OF_DAY),
//                        calendar.get(Calendar.MINUTE))
            val resultDate : Date = GregorianCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, minute).time
//            val result = Bundle().apply {
//                putSerializable(ARG_DATE, date)
//            }
            parentFragmentManager.setFragmentResult(KEY_DATE, bundleOf(KEY_DATE to resultDate))
        }

        return TimePickerDialog(
            requireContext(),
//            null,
            timeListener,
            hour,
            minute,
            DateFormat.is24HourFormat(requireContext())
        )
    }


    companion object {
        fun newInstance(date: Date) : TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
//                putSerializable(ARG_TIME, time)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }
}