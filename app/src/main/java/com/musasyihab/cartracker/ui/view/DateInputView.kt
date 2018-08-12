package com.musasyihab.cartracker.ui.view

import android.app.DatePickerDialog
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

class DateInputView(private val mContext: Context, mAttributes: AttributeSet) : LinearLayout(mContext, mAttributes) {
    private val label: TextView
    private val error: TextView
    private val textView: TextView
    private var datePickerDialog: DatePickerDialog? = null
    private val errorText: String? = null
    private var minDate: Date? = null
    private var maxDate: Date? = null
    private var onDateSelected: OnDateSelected? = null

    private val today: Calendar = Calendar.getInstance()
    private val locale = Locale(Locale.getDefault().language, Locale.getDefault().country)
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", locale)

    val selectedTimeText: String
        get() = textView.text.toString()

    val selectedDateInMilis: Long
        get() {
            val date: Date? = Helper.getDateFromString(dateFormat, textView.text.toString())
            return date!!.time
        }

    val selectedDate: Date?
        get() = Helper.getDateFromString(dateFormat, textView.text.toString())

    val selectedYear: Int
        get() {

            var calendar: Calendar = Calendar.getInstance()
            if(selectedDate !== null) calendar.time = selectedDate

            return calendar.get(Calendar.YEAR)
        }

    val selectedMonth: Int
        get() {
            var calendar: Calendar = Calendar.getInstance()
            if(selectedDate !== null) calendar.time = selectedDate

            return calendar.get(Calendar.MONTH)
        }

    val selectedDayOfMonth: Int
        get() {
            var calendar: Calendar = Calendar.getInstance()
            if(selectedDate !== null) calendar.time = selectedDate

            return calendar.get(Calendar.DAY_OF_MONTH)
        }


    init {

        LayoutInflater.from(mContext).inflate(R.layout.view_dateinput, this, true)

        error = findViewById<View>(R.id.view_dateinput_label_error) as TextView
        label = findViewById<View>(R.id.view_dateinput_label) as TextView
        textView = findViewById<View>(R.id.view_dateinput_textview) as TextView
        textView.hint = resources.getString(R.string.choose_time)
        textView.setTextColor(resources.getColor(R.color.hintColor))

        val attributes = mContext.obtainStyledAttributes(mAttributes,
                R.styleable.DateInputView)
        val hasMinDate = attributes.getBoolean(R.styleable.DateInputView_hasMinDate, false)
        val hasMaxDate = attributes.getBoolean(R.styleable.DateInputView_hasMinDate, false)
        val hintText = attributes
                .getString(R.styleable.DateInputView_hint)
        label.text = hintText

        if (hasMinDate) minDate = today.time
        if (hasMaxDate) maxDate = today.time

        attributes.recycle()

        textView.text = Helper.convertDateToString(dateFormat, today.time)

        if (!isInEditMode) {
            setOnClickListener {
                initDialog()
                datePickerDialog!!.show()
            }
        }
    }

    private fun initDialog() {
        val currentYear: Int = selectedYear
        val currentMonth: Int = selectedMonth
        val currentDayOfMonth: Int = selectedDayOfMonth
        datePickerDialog = DatePickerDialog(mContext, DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->

            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.YEAR, year)
            selectedTime.set(Calendar.MONTH, month)
            selectedTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val result = dateFormat.format(selectedTime.time)
            textView.text = result
            datePickerDialog!!.dismiss()
            if(onDateSelected!=null) {
                onDateSelected!!.onDateSelected(selectedTime.time)
            }
        }, currentYear, currentMonth, currentDayOfMonth)
        if(minDate != null) datePickerDialog!!.datePicker.minDate = minDate!!.time
        datePickerDialog!!.show()
    }

    fun setupHint(hint: String) {
        label.text = hint
    }

    fun setMinDate(minDate: Date) {
        this.minDate = minDate
    }

    fun setSelectedDate(selectedDate: Date) {
        val dateString = Helper.convertDateToString(dateFormat, selectedDate)
        textView.text = dateString
    }

    fun setListener(listener: OnDateSelected) {
        onDateSelected = listener
    }

    interface OnDateSelected {
        fun onDateSelected(dateSelected: Date)
    }

}