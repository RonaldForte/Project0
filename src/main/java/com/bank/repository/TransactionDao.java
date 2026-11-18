package com.bank.repository;

import java.util.List;

import com.bank.model.Transaction;

public interface TransactionDao {
    //Create -----------------------------

    //Record a deposit, withdrawal or transfer
    Transaction create(Transaction transaction);

    //------------------------------------


    //Read -------------------------------

    //Full history of one account
    List<Transaction> findByAccountId(int accountId);
    
    //------------------------------------


    //Update not a part of req

    //Delete not a part of req
}