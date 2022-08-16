package io.pleo.antaeus.data

import io.pleo.antaeus.models.InvoiceStatus
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AntaeusDalTest : AbstractBaseTest() {

    @Test
    fun `retrieve all invoices`() {

        val numberOfInvoices = sut.fetchInvoices().size
        assertEquals(1000, numberOfInvoices)
    }

    @Test
    fun `retrieve all pending invoices`() {

        val numberOfInvoices = sut.fetchInvoiceByStatus(InvoiceStatus.PENDING).size
        assertEquals(100, numberOfInvoices)
    }

    @Test
    fun `retrieve all paid invoices`() {

        val numberOfInvoices = sut.fetchInvoiceByStatus(InvoiceStatus.PAID).size
        assertEquals(900, numberOfInvoices)
    }

    @Test
    fun `expect table not found error`() {

        transaction(db) { SchemaUtils.drop(*tables) }
        var thrown = false
        try {
            sut.fetchInvoices()
        } catch (e: Exception) {
            thrown = true
            assertEquals(
                "org.sqlite.SQLiteException: [SQLITE_ERROR] SQL error or missing database (no such table: Invoice)",
                e.message
            )
        }
        assertTrue(thrown)
    }

} 