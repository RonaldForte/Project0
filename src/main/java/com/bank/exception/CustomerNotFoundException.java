package com.bank.exception;

//Apply: no customer with that id
public class CustomerNotFoundException extends BankException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}