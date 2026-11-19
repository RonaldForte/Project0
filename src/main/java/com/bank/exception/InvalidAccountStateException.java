package com.bank.exception;

//Approve/deny: account is not PENDING
public class InvalidAccountStateException extends BankException {
    public InvalidAccountStateException(String message) {
        super(message);
    }
}