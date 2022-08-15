/*
    Defines the main() entry point of the app.
    Configures the database and sets up the REST web service.
 */

@file:JvmName("AntaeusApp")

package io.pleo.antaeus.app

import getPaymentProvider
import io.pleo.antaeus.core.services.BillingSchedulerService
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.CustomerTable
import io.pleo.antaeus.data.InvoiceTable
import io.pleo.antaeus.rest.AntaeusRest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import setupInitialData
import java.io.File
import java.sql.Connection
import java.util.*

fun main() {
    // The tables to create in the database.
    val tables = arrayOf(InvoiceTable, CustomerTable)

    val dbFile: File = File.createTempFile("antaeus-db", ".sqlite")

    // Connect to the database and create the needed tables. Drop any existing data.
    val db = Database
        .connect(
            url = "jdbc:sqlite:${dbFile.absolutePath}",
            driver = "org.sqlite.JDBC",
            user = "root",
            password = ""
        )
        .also {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            transaction(it) {
                addLogger(StdOutSqlLogger)
                // Drop all existing tables to ensure a clean slate on each run
                SchemaUtils.drop(*tables)
                // Create all tables
                SchemaUtils.create(*tables)
            }
        }

    // Set up data access layer.
    val dal = AntaeusDal(db = db)

    // Insert example data in the database.
    setupInitialData(dal = dal)

    // Get third parties
    val paymentProvider = getPaymentProvider()

    // Create core services
    val invoiceService = InvoiceService(dal = dal)
    val customerService = CustomerService(dal = dal)

    // Billing service
    val billingService = BillingService(paymentProvider, invoiceService = invoiceService)

    // Create REST web service
    AntaeusRest(
        invoiceService = invoiceService,
        customerService = customerService,
        billingService = billingService
    ).run()

    // Billing service scheduler
    val billingSchedulerService = BillingSchedulerService()

    val calendar = Calendar.getInstance()
    val isFirstDayOfMonth = billingSchedulerService.isFirstDayOfMonth(calendar)


    /*Custom scheduler to run the Billing Process every first day of each month
      The logic of the scheduler is:
      1) Checks if it is the first day of the month, if it is true, then executes
         the billing process 3 times (in order to avoid loss due to network exceptions) and then
         adds a delay to run again when it is the first day of the month
      2) Else adds a delay to run again when it is the first day of the month
     */
    var delay: Long = 0
    val maxRetries = 3
    var executionsCounter = 0

    while (true) {
        if (isFirstDayOfMonth) {

            if (executionsCounter < maxRetries) { // Run the Billing process 3 times before next execution
                billingService.billingProcess()
                delay = 0
                executionsCounter++
            } else {
                delay = billingSchedulerService.milliSecondsUntilFirstDayOfMonth(calendar) // Sleep until 1 day of the next month
                executionsCounter = 0
            }
        } else {
            delay = billingSchedulerService.milliSecondsUntilFirstDayOfMonth(calendar)  // Sleep until 1 day of the next month
            executionsCounter = 0
        }

        Thread.sleep(delay)
    }
}
