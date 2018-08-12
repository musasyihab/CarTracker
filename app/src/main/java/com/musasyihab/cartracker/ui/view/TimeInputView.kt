package com.musasyihab.cartracker.ui.view

import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.musasyihab.cartracker.R
import com.musasyihab.cartracker.util.Helper

import java.text.SimpleDateFormat
import java.util.*

class TimeInputView(private val mContext: Context, mAttributes: AttributeSet) : LinearLayout(mContext, mAttributes) {
    private val label: TextView
    private val error: TextView
    private val textView: TextView
    private var timePickerDialog: TimePickerDialog? = null
    private val errorText: String? = null
    private var onTimeSelected: OnTimeSelected? = null

    private val defaultTime = "00:00"
    private val locale = Locale(Locale.getDefault().language, Locale.getDefault().country)
    private val dateFormat = SimpleDateFormat("HH:mm", locale)

    val selectedTimeText: String
        get() = textView.text.toString()

    val selectedTime: Date?
        get() = Helper.getDateFromString(dateFormat, textView.text.toString())

    val selectedHour: Int
        get() {
            val hourMinute = textView.text.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return if (hourMinute.size == 2) Integer.parseInt(hourMinute[0]) else 0
        }

    val selectedMinute: Int
        get() {
            val hourMinute = textView.text.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return if (hourMinute.size == 2) Integer.parseInt(hourMinute[1]) else 0
        }


    init {

        LayoutInflater.from(mContext).inflate(R.layout.view_timeinput, this, true)

        error = findViewById<View>(R.id.view_timeinput_label_error) as TextView
        label = findViewById<View>(R.id.view_timeinput_label) as TextView
        textView = findViewById<View>(R.id.view_timeinput_textview) as TextView
        textView.hint = resources.getString(R.string.choose_time)
        textView.setTextColor(resources.getColor(R.color.hintColor))

        val attributes = mContext.obtainStyledAttributes(mAttributes,
                R.styleable.TimeInputView)
        val hintText = attributes
                .getString(R.styleable.TimeInputView_hint)
        label.text = hintText

        attributes.recycle()

        textView.text = defaultTime

        if (!isInEditMode) {
            setOnClickListener {
                initDialog()
                timePickerDialog!!.show()
            }
        }
    }

    private fun initDialog() {
        val currentHour = selectedHour
        val currentMinute = selectedMinute
        timePickerDialog = TimePickerDialog(mContext, TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            val result = dateFormat.format(selectedTime.time)
            textView.text = result
            timePickerDialog!!.dismiss()
            onTimeSelected!!.onTimeSelected(hourOfDay, minute)
        }, currentHour, currentMinute, true)
        timePickerDialog!!.show()
    }

    fun setupHint(hint: String) {
        label.text = hint
    }

    fun setSelectedTime(selectedHour: Int, seletedMinute: Int) {
        val selectedTime = Calendar.getInstance()
        selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
        selectedTime.set(Calendar.MINUTE, seletedMinute)
        val timeString = dateFormat.format(selectedTime.time)
        textView.text = timeString
    }

    fun setListener(listener: OnTimeSelected) {
        onTimeSelected = listener
    }

    interface OnTimeSelected {
        fun onTimeSelected(hourOfDay: Int, minute: Int)
    }
}
