package com.susess.cv360.ui.pickers

import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.susess.cv360.helpers.DateTimeUtils
import java.util.TimeZone
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatetimePickers {
    fun showDatePicker(textInput: TextInputEditText, supportFragmentManager: FragmentManager) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccione la Fecha")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.show(supportFragmentManager, "date")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val timeZone = TimeZone.getDefault()
            val offsetFromUTC = timeZone.getOffset(Date().time) * -1
            val selectedDate = Date(selection + offsetFromUTC)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.timeZone = timeZone

            val formattedDate = dateFormat.format(selectedDate)
            textInput.setText(formattedDate)
        }
    }

    fun showTimePicker(textInput: TextInputEditText,  supportFragmentManager: FragmentManager){
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setInputMode(INPUT_MODE_CLOCK)
            .setHour(0)
            .setMinute(0)
            .setTitleText("Seleccione la hora")
            .build()

        timePicker.show(supportFragmentManager, "time")

        timePicker.addOnPositiveButtonClickListener {
            // call back code
            val hours = timePicker.hour
            val minutes = timePicker.minute

            textInput.setText(DateTimeUtils.formatearHora(hours, minutes))
        }

    }
}