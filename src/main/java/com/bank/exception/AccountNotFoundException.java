package com.bank.exception;

//Any account feature: no account with that id
public class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}