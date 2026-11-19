package com.bank.exception;

//Register: a required field is blank
public class InvalidInputException extends BankException {
    public InvalidInputException(String message) {
        super(message);
    }
}