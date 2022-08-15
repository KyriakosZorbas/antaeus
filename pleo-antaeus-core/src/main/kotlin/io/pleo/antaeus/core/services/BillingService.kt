package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: AntaeusDal
) {
    fun billingProcess() {
        val invoicesForCharge = invoiceService.fetchInvoiceByStatus(InvoiceStatus.PENDING)
        invoicesForCharge.forEach {
            chargeInvoice(it)
        }
    }

    private fun chargeInvoice(invoice: Invoice) {
        paymentProvider.charge(invoice)
        //TODO
        // Update Database status
        // Add new InsufficientFunds Exception
        // Add InvoiceNotFoundException , CurrencyMismatchException , CustomerNotFoundException
    }

}