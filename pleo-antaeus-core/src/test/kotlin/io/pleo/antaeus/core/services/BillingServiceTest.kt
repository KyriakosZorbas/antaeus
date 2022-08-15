package io.pleo.antaeus.core.services

import io.mockk.*
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BillingServiceTest {
    private val invoiceService = mockk<AntaeusDal>(relaxed = true)
    private val paymentProvider = mockk<PaymentProvider>(relaxed = true)
    private val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService)

    private fun mockInvoice(mockedResult: BigDecimal): Invoice {
        return mockk {
            every { status } returns InvoiceStatus.PENDING
            every { amount } returns Money(mockedResult, Currency.EUR)
        }
    }

    @Test
    fun `will charge each invoice fetched`() {
        every { invoiceService.fetchInvoiceByStatus(InvoiceStatus.PENDING) } returns listOf(
            mockInvoice(BigDecimal.valueOf(100)),
            mockInvoice(BigDecimal.valueOf(500))

        )
        billingService.billingProcess()
        verify(exactly = 2) { paymentProvider.charge(any()) }
        confirmVerified(paymentProvider)
    }


}