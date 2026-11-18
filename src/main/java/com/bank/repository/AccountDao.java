package com.bank.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.bank.model.Account;
import com.bank.model.AccountStatus;

public interface AccountDao {
    //Create -----------------------------

    //Customer applies to open an account
    Account create(Account account); 
    //------------------------------------


    //Read -------------------------------

    //Customer sees their own accounts
    List<Account> findByCustomerId(int customerId);

    //Employee sees all pending applications
    List<Account> findPendingAccounts();

    //Employee sees all accounts and balances
    List<Account> findAllAccounts();

    //Look up one account
    Optional<Account> findByAccountId(int accountId);

    //------------------------------------

    
    //Update -----------------------------

    //Deposit, withdraw, transfer change a balance
    boolean updateBalance(int accountId, BigDecimal newBalance);

    //Employee approves or denies
    boolean updateStatus(int accountId, AccountStatus status, int employeeId);

    //------------------------------------


    //Delete not apart of req
}
