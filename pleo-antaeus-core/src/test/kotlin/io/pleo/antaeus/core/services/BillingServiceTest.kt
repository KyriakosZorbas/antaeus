package io.pleo.antaeus.core.services

import io.mockk.*
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.InsufficientFundsException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Assertions

import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BillingServiceTest {
    private val invoiceService = mockk<InvoiceService>(relaxed = true)
    private val invoice = Invoice(1, 1, Money(BigDecimal(100), Currency.EUR), InvoiceStatus.PENDING)
    private val invoice2 = Invoice(1, 2, Money(BigDecimal(100), Currency.DKK), InvoiceStatus.PENDING)
    private val invoice3 = Invoice(1, 3, Money(BigDecimal(100), Currency.EUR), InvoiceStatus.PENDING)
    private val invoice4 = Invoice(1, 4, Money(BigDecimal(100), Currency.EUR), InvoiceStatus.PENDING)
    private val invoice5 = Invoice(1, 5, Money(BigDecimal(100), Currency.EUR), InvoiceStatus.PENDING)
    private val invoice6 = Invoice(1, 6, Money(BigDecimal(10), Currency.EUR), InvoiceStatus.PENDING)

    private val paymentProvider = mockk<PaymentProvider> {
        every { charge(invoice) } returns true
        every { charge(invoice2) } throws CurrencyMismatchException(invoice2.id, invoice2.customerId)
        every { charge(invoice3) } throws CustomerNotFoundException(invoice3.id)
        every { charge(invoice4) } throws InsufficientFundsException(invoice4.id)
        every { charge(invoice5) } throws NetworkException()
        every { charge(invoice6) } returns false

    }

    private val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService)

    @Test
    fun `Charges a pending invoice`() {
        Assertions.assertEquals(billingService.chargeInvoice(invoice), true)
    }

    @Test
    fun `Checks currency mismatch exception `() {
        Assertions.assertEquals(billingService.chargeInvoice(invoice2), false)
    }

    @Test
    fun `Checks customer not found exception`() {
        Assertions.assertEquals(billingService.chargeInvoice(invoice3), false)
    }

    @Test
    fun `Checks insufficient funds exception`() {
        Assertions.assertEquals(billingService.chargeInvoice(invoice4), false)
    }

    @Test
    fun `Checks network exception`() {
        Assertions.assertEquals(billingService.chargeInvoice(invoice5), false)
    }

    @Test
    fun `Checks account balance`() {
        Assertions.assertEquals(billingService.chargeInvoice(invoice6), false)
    }

}