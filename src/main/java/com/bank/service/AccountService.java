package com.bank.service;

import java.math.BigDecimal;
import java.util.List;

import com.bank.exception.AccountNotApprovedException;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.CustomerNotFoundException;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAccountStateException;
import com.bank.exception.InvalidAmountException;
import com.bank.exception.InvalidTransferException;
import com.bank.exception.UnauthorizedAccessException;
import com.bank.model.Account;
import com.bank.model.AccountOwner;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.AccountDao;
import com.bank.repository.AccountOwnerDao;
import com.bank.repository.CustomerDao;
import com.bank.repository.TransactionDao;

public class AccountService {

    private final AccountDao accountDao;
    private final AccountOwnerDao accountOwnerDao;
    private final TransactionDao transactionDao;
    private final CustomerDao customerDao;

    //All DAOs handed in (dependency injection) so tests can pass Mockito mocks
    public AccountService(AccountDao accountDao, AccountOwnerDao accountOwnerDao,
                          TransactionDao transactionDao, CustomerDao customerDao) {
        this.accountDao = accountDao;
        this.accountOwnerDao = accountOwnerDao;
        this.transactionDao = transactionDao;
        this.customerDao = customerDao;
    }

    //Apply ------------------------------

    //Customer applies : customer exists, then create account AND link the owner
    public Account apply(int customerId, AccountType accountType) {
        if (customerDao.findById(customerId).isEmpty()) {
            throw new CustomerNotFoundException("Customer " + customerId + " does not exist.");
        }
        Account account = accountDao.create(new Account(accountType));
        accountOwnerDao.create(new AccountOwner(customerId, account.getAccountId()));
        return account;
    }
    //------------------------------------


    //View -------------------------------

    //Customer sees their own accounts
    public List<Account> getMyAccounts(int customerId) {
        return accountDao.findByCustomerId(customerId);
    }

    //Customer sees one account's history (must own it)
    public List<Transaction> getHistory(int customerId, int accountId) {
        getOwnedAccount(customerId, accountId);
        return transactionDao.findByAccountId(accountId);
    }
    //------------------------------------


    //Money ------------------------------

    // Deposit: amount > 0, owns it, APPROVED
    public Account deposit(int customerId, int accountId, BigDecimal amount) {
        validateAmount(amount);
        Account account = getOwnedApprovedAccount(customerId, accountId);

        BigDecimal newBalance = account.getBalance().add(amount);
        accountDao.updateBalance(accountId, newBalance);
        transactionDao.create(new Transaction(null, accountId, TransactionType.DEPOSIT, amount));

        account.setBalance(newBalance);
        return account;
    }

    // Withdraw: amount > 0, owns it, APPROVED, no overdraft
    public Account withdraw(int customerId, int accountId, BigDecimal amount) {
        validateAmount(amount);
        Account account = getOwnedApprovedAccount(customerId, accountId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(String.format("Insufficient funds. Balance: $%,.2f", account.getBalance()));
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        accountDao.updateBalance(accountId, newBalance);
        transactionDao.create(new Transaction(accountId, null, TransactionType.WITHDRAWAL, amount));

        account.setBalance(newBalance);
        return account;
    }

    //Transfer: amount > 0, from != to, owns BOTH, both APPROVED, no overdraft
    //TODO: the two balance updates aren't one DB transaction yet (stored procedure stretch goal)
    public void transfer(int customerId, int fromAccountId, int toAccountId, BigDecimal amount) {
        validateAmount(amount);
        if (fromAccountId == toAccountId) {
            throw new InvalidTransferException("Cannot transfer to the same account.");
        }
        Account from = getOwnedApprovedAccount(customerId, fromAccountId);
        Account to = getOwnedApprovedAccount(customerId, toAccountId);

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(String.format("Insufficient funds. Balance: $%,.2f", from.getBalance()));
        }

        accountDao.updateBalance(fromAccountId, from.getBalance().subtract(amount));
        accountDao.updateBalance(toAccountId, to.getBalance().add(amount));
        transactionDao.create(new Transaction(fromAccountId, toAccountId, TransactionType.TRANSFER, amount));
    }
    //------------------------------------


    //Employee features ------------------

    //Employee sees all accounts and balances
    public List<Account> getAllAccounts() {
        return accountDao.findAllAccounts();
    }

    //Employee sees all pending applications
    public List<Account> getPendingAccounts() {
        return accountDao.findPendingAccounts();
    }

    //Employee sees who owns an account
    public List<AccountOwner> getOwners(int accountId) {
        return accountOwnerDao.findByAccountId(accountId);
    }

    //Employee approves: account exists, still PENDING
    public void approve(int employeeId, int accountId) {
        review(employeeId, accountId, AccountStatus.APPROVED);
    }

    //Employee denies: account exists, still PENDING
    public void deny(int employeeId, int accountId) {
        review(employeeId, accountId, AccountStatus.DENIED);
    }
    //------------------------------------


    //Helpers ----------------------------

    //Shared by approve and deny
    private void review(int employeeId, int accountId, AccountStatus decision) {
        Account account = accountDao.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account " + accountId + " does not exist."));
        if (account.getStatus() != AccountStatus.PENDING) {
            throw new InvalidAccountStateException("Account " + accountId + " was already " + account.getStatus() + ".");
        }
        accountDao.updateStatus(accountId, decision, employeeId);
    }

    //Amount must exist, be > 0, and have at most 2 decimal places (cents)
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than $0.");
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            throw new InvalidAmountException("Amount can have at most 2 decimal places.");
        }
    }

    //Account exists and this customer owns it
    private Account getOwnedAccount(int customerId, int accountId) {
        Account account = accountDao.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account " + accountId + " does not exist."));
        if (!accountOwnerDao.isOwner(customerId, accountId)) {
            throw new UnauthorizedAccessException("You do not own account " + accountId + ".");
        }
        return account;
    }

    //Account exists, this customer owns it, and it's APPROVED
    private Account getOwnedApprovedAccount(int customerId, int accountId) {
        Account account = getOwnedAccount(customerId, accountId);
        if (account.getStatus() != AccountStatus.APPROVED) {
            throw new AccountNotApprovedException("Account " + accountId + " is " + account.getStatus() + ", not APPROVED.");
        }
        return account;
    }
    // ------------------------------------
}