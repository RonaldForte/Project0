package com.bank.exception;

//Register: username already exists
public class UsernameTakenException extends BankException {
    public UsernameTakenException(String message) {
        super(message);
    }
}