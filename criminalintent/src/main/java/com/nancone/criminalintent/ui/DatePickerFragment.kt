package com.nancone.criminalintent.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment

/**
 * DialogFragment 없이 DatePickerDialog를 보여줄 수 있지만
 * DitePickerDialog만 사용할때 장치가 회전했을 경우 화면에서 사라짐
 * 반면에, DatePickerDialog가 Fragment에 포함되면 장치회전 후에도 대화상자가 다시 생성되어 화면에 다시 나타남
 *
 * DatePickerFragment는 MainActivity에 의해서 호스팅될 것임
 */
class DatePickerFragment : DialogFragment() {

    @RequiresApi(Build.VERSION_CODES.N) // 24 이상
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            null,
            initialYear,
            initialMonth,
            initialDay
        )
    }
}