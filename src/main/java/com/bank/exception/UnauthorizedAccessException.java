package com.bank.exception;

//Customer tried to use an account they don't own
public class UnauthorizedAccessException extends BankException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}