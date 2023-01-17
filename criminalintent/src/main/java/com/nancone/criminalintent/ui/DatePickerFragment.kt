package com.nancone.criminalintent.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import java.util.*

/**
 * DialogFragment 없이 DatePickerDialog를 보여줄 수 있지만
 * DitePickerDialog만 사용할때 장치가 회전했을 경우 화면에서 사라짐
 * 반면에, DatePickerDialog가 Fragment에 포함되면 장치회전 후에도 대화상자가 다시 생성되어 화면에 다시 나타남
 *
 * DatePickerFragment는 MainActivity에 의해서 호스팅될 것임
 */
private const val ARG_DATE = "date"
class DatePickerFragment : DialogFragment() {

    interface Callbacks {
        fun onDateSelected(date: Date)
    }

    @RequiresApi(Build.VERSION_CODES.N) // 24 이상
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener {
                _: DatePicker, year:Int, month: Int, day: Int ->
            val resultDate : Date = GregorianCalendar(year, month, day).time
            parentFragmentManager.setFragmentResult(KEY_DATE, bundleOf(KEY_DATE to resultDate))
        }

        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
//            null,
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }

    companion object {
        fun newInstance(date: Date) : DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}