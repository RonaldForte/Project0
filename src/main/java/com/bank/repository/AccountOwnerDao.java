package com.bank.repository;

import java.util.List;

import com.bank.model.AccountOwner;

public interface AccountOwnerDao {
    //Create -----------------------------

    //Link a customer to an account (applying, joint owner)
    AccountOwner create(AccountOwner accountOwner);

    //------------------------------------


    //Read -------------------------------

    //Check a customer owns an account before they use it
    boolean isOwner(int customerId, int accountId);

    //Everyone who owns an account
    List<AccountOwner> findByAccountId(int accountId);
    
    //------------------------------------


    //Update not a part of req

    //Delete not a part of req
}