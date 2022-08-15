package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.InsufficientFundsException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService
) {
    private val logger = KotlinLogging.logger {}

    fun billingProcess() {
        logger.info { "The Billing process has started " }

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
            logger.info { "There isn't customer with the given id: $" + invoice.customerId }
            return false
        } catch (e: CurrencyMismatchException) {
            logger.info { "The currency does not match the customer account, his id is : " + invoice.customerId }
            return false
        } catch (e: InsufficientFundsException) {
            logger.info { "Account balance is insufficient or its daily credit limit has been reached. Invoice id: " + invoice.id }
            return false
        } catch (e: NetworkException) {
            logger.info { "Network error occurred" }
            return false
        }
        return false
    }

}