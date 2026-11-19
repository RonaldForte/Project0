package com.bank.exception;

//Withdraw, transfer: amount is more than the balance
public class InsufficientFundsException extends BankException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}