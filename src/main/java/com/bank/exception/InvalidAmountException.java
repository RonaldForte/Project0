package com.bank.exception;

//Deposit, withdraw, transfer: amount is missing, zero, negative, or has more than 2 decimals
public class InvalidAmountException extends BankException {
    public InvalidAmountException(String message) {
        super(message);
    }
}