package com.bank.exception;

// Parent of every business-rule exception. controller catch them all at once
public class BankException extends RuntimeException {
    public BankException(String message) {
        super(message);
    }
}