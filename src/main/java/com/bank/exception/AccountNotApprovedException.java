package com.bank.exception;

//Money moved in or out of an account that isn't APPROVED
public class AccountNotApprovedException extends BankException {
    public AccountNotApprovedException(String message) {
        super(message);
    }
}