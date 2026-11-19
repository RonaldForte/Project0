package com.bank.exception;

//Transfer: from and to are the same account
public class InvalidTransferException extends BankException {
    public InvalidTransferException(String message) {
        super(message);
    }
}