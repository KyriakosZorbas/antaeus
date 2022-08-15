/*
    Defines methods in order to ensure that the Billing process
    runs every first day of each month
 */

package io.pleo.antaeus.core.services

import java.util.*
import java.util.concurrent.TimeUnit

class BillingSchedulerService {

    // This function returns true if it is the first day of month else it returns false
    fun isFirstDayOfMonth(calendar: Calendar): Boolean {
        val firstDayOfMonth = calendar
        if (firstDayOfMonth.get(Calendar.DAY_OF_MONTH) == 1) {
            return true
        }
        return false
    }

    /*
       This function returns the amount of milliseconds until the first day of the next month
       If it is the first day of the month returns 0
     */
    fun milliSecondsUntilFirstDayOfMonth(calendar: Calendar): Long {

        var remainingMilliSecondsUntilEndOfMonth :Long = 0
        // if it the first day of the month return zero delay
        if (!isFirstDayOfMonth(calendar)) {
            /*
               The following code:
               1) Gets the current day
               2) Gets the last day of the month
               3) Subtracts them and add one more day in order find the remaining days until the first day of the next month
               4) Converts the remaining days to milliseconds
             */
            val today = calendar.get(Calendar.DAY_OF_MONTH)
            val lastDayOfMonth = calendar.getActualMaximum(Calendar.DATE)
            val remainingDaysUntilEndOfMonth = lastDayOfMonth - today + 1
            remainingMilliSecondsUntilEndOfMonth = TimeUnit.DAYS.toMillis(remainingDaysUntilEndOfMonth.toLong())
        }
        return remainingMilliSecondsUntilEndOfMonth

    }
}