package com.sample.chrono12.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat

object DateUtil {

    private val date = SimpleDateFormat("dd/MM/yyyy")

    private val time = SimpleDateFormat("hh:mm a")

    private fun Long.getTime(): String = time.format(this)

    private fun Long.getDate(): String = date.format(this)

    private fun String.isToday(): Boolean = DateUtils.isToday(this.toLong())


    private fun String.isYesterday(): Boolean = DateUtils.isToday(this.toLong() + DateUtils.DAY_IN_MILLIS)


    fun getDateAndTime(time: Long): String {
        return when {
            time.toString().isToday() -> {
                "Today ${time.getTime()}"
            }
            time.toString().isYesterday() -> {
                "Yesterday ${time.getTime()}"
            }
            else -> {
                "${time.getDate()} ${time.getTime()}"
            }
        }

    }

}