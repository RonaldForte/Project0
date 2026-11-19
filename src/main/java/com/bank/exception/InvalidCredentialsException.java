package com.bank.exception;

//Login: wrong username or password (same error for both)
public class InvalidCredentialsException extends BankException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}