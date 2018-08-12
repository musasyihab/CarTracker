package com.musasyihab.cartracker.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.musasyihab.cartracker.R
import com.musasyihab.cartracker.model.AvailabilityFormModel
import java.util.*

class AvailabilityFormView(private val mContext: Context, mAttributes: AttributeSet): LinearLayout(mContext, mAttributes) {

    private var bookBtn: Button? = null
    private var startDate: DateInputView? = null
    private var startTime: TimeInputView? = null
    private var endDate: DateInputView? = null
    private var endTime: TimeInputView? = null
    private var onFormSubmited: OnFormSubmited? = null

    init {

        LayoutInflater.from(mContext).inflate(R.layout.view_availability_form, this, true)

        startDate = findViewById<View>(R.id.startDate) as DateInputView
        startTime = findViewById<View>(R.id.startTime) as TimeInputView
        endDate = findViewById<View>(R.id.endDate) as DateInputView
        endTime = findViewById<View>(R.id.endTime) as TimeInputView
        bookBtn = findViewById<View>(R.id.bookBtn) as Button

        startDate!!.setListener(object : DateInputView.OnDateSelected {
            override fun onDateSelected(dateSelected: Date) {

                endDate!!.setMinDate(dateSelected)

                if(endDate!!.selectedDate!!.before(dateSelected)) {
                    endDate!!.setSelectedDate(dateSelected)
                }
                if(isStartEndSameDay() && isEndTimeBeforeStartTime()) {
                    endTime!!.setSelectedTime(startTime!!.selectedHour, startTime!!.selectedMinute)
                }
            }
        })

        endDate!!.setListener(object : DateInputView.OnDateSelected {
            override fun onDateSelected(dateSelected: Date) {
                if(isStartEndSameDay() && isEndTimeBeforeStartTime()) {
                    endTime!!.setSelectedTime(startTime!!.selectedHour, startTime!!.selectedMinute)
                }
            }
        })

        startTime!!.setListener(object : TimeInputView.OnTimeSelected {
            override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                if(isStartEndSameDay() && isEndTimeBeforeStartTime()) {
                    endTime!!.setSelectedTime(hourOfDay, minute)
                }
            }
        })

        endTime!!.setListener(object : TimeInputView.OnTimeSelected {
            override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                if(isStartEndSameDay() && isEndTimeBeforeStartTime()) {
                    startTime!!.setSelectedTime(hourOfDay, minute)
                }
            }
        })

        bookBtn!!.setOnClickListener {
            submitForm()
        }
    }


    private fun submitForm() {
        val startTime: Long = getStartTimeInMilis()
        val endTime: Long = getEndTimeInMilis()

        if(onFormSubmited!=null) {
            onFormSubmited!!.onFormSubmited(AvailabilityFormModel(startTime, endTime))
        }
    }

    private fun isStartEndSameDay(): Boolean {
        return startDate!!.selectedDate!!.time == endDate!!.selectedDate!!.time
    }

    private fun isEndTimeBeforeStartTime(): Boolean {
        return endTime!!.selectedTime!!.before(startTime!!.selectedTime)
    }

    private fun getStartTimeInMilis(): Long {
        val start: Calendar = Calendar.getInstance()
        start.time = startDate!!.selectedDate
        start.set(Calendar.HOUR_OF_DAY, startTime!!.selectedHour)
        start.set(Calendar.MINUTE, startTime!!.selectedMinute)
        return  start.timeInMillis/1000
    }

    private fun getEndTimeInMilis(): Long {
        val end: Calendar = Calendar.getInstance()
        end.time = endDate!!.selectedDate
        end.set(Calendar.HOUR_OF_DAY, endTime!!.selectedHour)
        end.set(Calendar.MINUTE, endTime!!.selectedMinute)
        return  end.timeInMillis/1000
    }

    fun setListener(listener: OnFormSubmited) {
        onFormSubmited = listener
    }

    interface OnFormSubmited {
        fun onFormSubmited(form: AvailabilityFormModel)
    }

}