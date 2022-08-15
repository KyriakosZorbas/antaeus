package io.pleo.antaeus.core.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BillingSchedulerServiceTest {

    var billingSchedulerService = BillingSchedulerService()

    @Test
    fun `returns true on first of a month`() {
        // Returns instance with current date and time set
        val calendar = Calendar.getInstance()

        // Mock current time to 1st day of month
        calendar.set(2022, 8, 1)

        val isFirstDayOfMonth = billingSchedulerService.isFirstDayOfMonth(calendar)

        assertEquals(true, isFirstDayOfMonth)
    }

    @Test
    fun `returns false if it isn't first day of the month`() {
        val calendar = Calendar.getInstance() // Returns instance with current date and time set
        // Mock current time to a day other that 1st day of month
        calendar.set(2022, 8, 15)
        val isFirstDayOfMonth = billingSchedulerService.isFirstDayOfMonth(calendar)

        assertEquals(false, isFirstDayOfMonth)
    }

    @Test
    fun `returns delay until first day of the month`() {
        val calendar = Calendar.getInstance() // Returns instance with current date and time set
        /*
           Mock current time to 15/01/2022
           January has 31 days
           So until the first day of the next month we want 17 more days
           17 days are  1468800000 milliseconds
         */
        calendar.set(2022, 0, 15)
        val remainingMilliSecondsUntilEndOfMonth = billingSchedulerService.milliSecondsUntilFirstDayOfMonth(calendar)
        assertEquals(1468800000, remainingMilliSecondsUntilEndOfMonth)
    }

    @Test
    fun `returns zero delay if it is the first day of the month`() {
        val calendar = Calendar.getInstance()

        // Mock current time to 01/01/2022
        calendar.set(2022, 0, 1)
        val remainingMilliSecondsUntilEndOfMonth = billingSchedulerService.milliSecondsUntilFirstDayOfMonth(calendar)
        assertEquals(0, remainingMilliSecondsUntilEndOfMonth)
    }


}