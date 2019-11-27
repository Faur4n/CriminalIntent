package com.example.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.format.DateFormat
import android.widget.TimePicker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

private const val ARG_DATE = "date"

class TimePickerFragment : DialogFragment() {

    interface Callbacks{
        fun onTimeSelected(date: Date)
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val is24Hour = DateFormat.is24HourFormat(context)

        val timeListener = TimePickerDialog.OnTimeSetListener{
            _: TimePicker , hourOfDay : Int , minutes : Int ->



            calendar.add(Calendar.HOUR_OF_DAY,hourOfDay)
            calendar.add(Calendar.MINUTE,minutes)
            val resultTime = calendar.time

            targetFragment.let { fragment ->
                (fragment as Callbacks).onTimeSelected(resultTime)
            }

        }

        //val date = arguments?.getSerializable(ARG_DATE) as Date
//        val calendar = Calendar.getInstance()
//        calendar.time = date
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute = calendar.get(Calendar.MINUTE)
//        val is24Hour = DateFormat.is24HourFormat(context)

        return TimePickerDialog(
            requireContext(),
            timeListener,
            hour,
            minute,
            is24Hour

        )
    }

    companion object {
        fun newInstance(date: Date) : TimePickerFragment{
            val args = Bundle().apply {
                putSerializable(ARG_DATE,date)
            }
            return TimePickerFragment().apply{
                arguments = args
            }
        }
    }
}