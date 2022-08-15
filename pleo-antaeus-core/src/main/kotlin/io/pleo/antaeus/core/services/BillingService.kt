package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.*
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService
) {

    fun billingProcess() {

        val invoicesForCharge = invoiceService.fetchByStatus(InvoiceStatus.PENDING)
        invoicesForCharge.forEach {
            chargeInvoice(it)
        }
    }

    fun chargeInvoice(invoice: Invoice): Boolean {
        try {
            if (paymentProvider.charge(invoice)) {
                invoiceService.updateInvoiceStatus(invoice.id, status = InvoiceStatus.PAID)
                return true
            }
        } catch (e: CustomerNotFoundException) {
            // TODO: add logic
            return false
        } catch (e: CurrencyMismatchException) {
            // TODO: add logic
            return false
        } catch (e: InsufficientFundsException) {
            // TODO: add logic
            return false
        } catch (e: NetworkException) {
            // TODO: add logic
            return false
        }
        return false
    }

}