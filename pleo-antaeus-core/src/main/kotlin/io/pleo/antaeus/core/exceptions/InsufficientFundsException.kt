package io.pleo.antaeus.core.exceptions

class InsufficientFundsException(invoiceId: Int) :
    Exception ( "Account balance is insufficient or its daily credit limit has been reached. Invoice '$invoiceId'" )