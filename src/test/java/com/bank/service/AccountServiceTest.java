package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.repository.AccountDao;
import com.bank.repository.AccountOwnerDao;
import com.bank.repository.CustomerDao;
import com.bank.repository.TransactionDao;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountDao accountDao;
    @Mock
    private AccountOwnerDao accountOwnerDao;
    @Mock
    private CustomerDao customerDao;
    @Mock
    private TransactionDao transactionDao;


    @InjectMocks
    private AccountService accountService;

    //Helper: a real account (models are data, not mocked)
    private Account account(int id, String balance, AccountStatus status) {
        return new Account(id, AccountType.CHECKING, new BigDecimal(balance), status, 1);
    }


    //Withdraw ---------------------------

    @Test
    void withdraw_moreThanBalance_throwsInsufficientFunds() {
        //Arrange
        Account account = new Account(1, AccountType.CHECKING, new BigDecimal("50.00"), AccountStatus.APPROVED, 1);
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account));
        when(accountOwnerDao.isOwner(1, 1)).thenReturn(true);
        //Act
                    //Expected                        //Actual
        assertThrows(InsufficientFundsException.class, () -> accountService.withdraw(1, 1, new BigDecimal("100.00")));
        //Assert
        verify(accountDao, never()).updateBalance(anyInt(), any());
    }

    @Test
    void withdraw_exactBalance_leavesZero() {
        //Arrange
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "50.00", AccountStatus.APPROVED)));
        when(accountOwnerDao.isOwner(1, 1)).thenReturn(true);
        //Act
        Account result = accountService.withdraw(1, 1, new BigDecimal("50.00"));
        //Assert
        assertEquals(0, result.getBalance().compareTo(BigDecimal.ZERO));
        verify(accountDao).updateBalance(1, new BigDecimal("0.00"));
        verify(transactionDao).create(any(Transaction.class));
    }

    @Test
    void withdraw_validAmount_subtractsAndRecords() {
        //Arrange
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "100.00", AccountStatus.APPROVED)));
        when(accountOwnerDao.isOwner(1, 1)).thenReturn(true);
        //Act
        Account result = accountService.withdraw(1, 1, new BigDecimal("40.00"));
        //Assert
        assertEquals(new BigDecimal("60.00"), result.getBalance());
        verify(accountDao).updateBalance(1, new BigDecimal("60.00"));
        verify(transactionDao).create(any(Transaction.class));
    }
    //------------------------------------


    //Deposit ----------------------------

    @Test
    void deposit_validAmount_addsAndRecords() {
        //Arrange
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "50.00", AccountStatus.APPROVED)));
        when(accountOwnerDao.isOwner(1, 1)).thenReturn(true);
        //Act
        Account result = accountService.deposit(1, 1, new BigDecimal("25.00"));
        //Assert
        assertEquals(new BigDecimal("75.00"), result.getBalance());
        verify(accountDao).updateBalance(1, new BigDecimal("75.00"));
        verify(transactionDao).create(any(Transaction.class));
    }

    @Test
    void deposit_zero_throwsInvalidAmount() {
        //Act + Assert: rejected before any DAO is touched
        assertThrows(InvalidAmountException.class, () -> accountService.deposit(1, 1, BigDecimal.ZERO));
        verify(accountDao, never()).updateBalance(anyInt(), any());
    }

    @Test
    void deposit_negative_throwsInvalidAmount() {
        assertThrows(InvalidAmountException.class, () -> accountService.deposit(1, 1, new BigDecimal("-5.00")));
        verify(accountDao, never()).updateBalance(anyInt(), any());
    }

    @Test
    void deposit_moreThanTwoDecimals_throwsInvalidAmount() {
        assertThrows(InvalidAmountException.class, () -> accountService.deposit(1, 1, new BigDecimal("1.005")));
        verify(accountDao, never()).updateBalance(anyInt(), any());
    }

    @Test
    void deposit_nullAmount_throwsInvalidAmount() {
        assertThrows(InvalidAmountException.class, () -> accountService.deposit(1, 1, null));
    }

    @Test
    void deposit_accountMissing_throwsAccountNotFound() {
        //Arrange: no when(...) -> the mock returns Optional.empty()
        //Act + Assert
        assertThrows(AccountNotFoundException.class, () -> accountService.deposit(1, 99, new BigDecimal("10.00")));
        verify(accountDao, never()).updateBalance(anyInt(), any());
    }

    @Test
    void deposit_notOwner_throwsUnauthorized() {
        //Arrange
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "50.00", AccountStatus.APPROVED)));
        when(accountOwnerDao.isOwner(2, 1)).thenReturn(false);
        //Act + Assert
        assertThrows(UnauthorizedAccessException.class, () -> accountService.deposit(2, 1, new BigDecimal("10.00")));
        verify(accountDao, never()).updateBalance(anyInt(), any());
    }

    @Test
    void deposit_pendingAccount_throwsNotApproved() {
        //Arrange
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "0.00", AccountStatus.PENDING)));
        when(accountOwnerDao.isOwner(1, 1)).thenReturn(true);
        //Act + Assert
        assertThrows(AccountNotApprovedException.class, () -> accountService.deposit(1, 1, new BigDecimal("10.00")));
        verify(accountDao, never()).updateBalance(anyInt(), any());
    }

    @Test
    void deposit_deniedAccount_throwsNotApproved() {
        //Arrange
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "0.00", AccountStatus.DENIED)));
        when(accountOwnerDao.isOwner(1, 1)).thenReturn(true);
        //Act + Assert
        assertThrows(AccountNotApprovedException.class, () -> accountService.deposit(1, 1, new BigDecimal("10.00")));
    }
    //------------------------------------


    //Transfer ---------------------------

    @Test
    void transfer_valid_movesMoneyAndRecords() {
        //Arrange: own both, $100 -> $20
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "100.00", AccountStatus.APPROVED)));
        when(accountDao.findByAccountId(2)).thenReturn(Optional.of(account(2, "20.00", AccountStatus.APPROVED)));
        when(accountOwnerDao.isOwner(1, 1)).thenReturn(true);
        when(accountOwnerDao.isOwner(1, 2)).thenReturn(true);
        //Act
        accountService.transfer(1, 1, 2, new BigDecimal("30.00"));
        //Assert
        verify(accountDao).updateBalance(1, new BigDecimal("70.00"));
        verify(accountDao).updateBalance(2, new BigDecimal("50.00"));
        verify(transactionDao).create(any(Transaction.class));
    }

    @Test
    void transfer_sameAccount_throwsInvalidTransfer() {
        assertThrows(InvalidTransferException.class, () -> accountService.transfer(1, 1, 1, new BigDecimal("10.00")));
        verify(accountDao, never()).updateBalance(anyInt(), any());
    }

    @Test
    void transfer_moreThanBalance_throwsInsufficientFunds() {
        //Arrange
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "10.00", AccountStatus.APPROVED)));
        when(accountDao.findByAccountId(2)).thenReturn(Optional.of(account(2, "0.00", AccountStatus.APPROVED)));
        when(accountOwnerDao.isOwner(1, 1)).thenReturn(true);
        when(accountOwnerDao.isOwner(1, 2)).thenReturn(true);
        //Act + Assert
        assertThrows(InsufficientFundsException.class, () -> accountService.transfer(1, 1, 2, new BigDecimal("50.00")));
        verify(accountDao, never()).updateBalance(anyInt(), any());
    }

    @Test
    void transfer_notOwnerOfDestination_throwsUnauthorized() {
        //Arrange: owns account 1, not account 2
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "100.00", AccountStatus.APPROVED)));
        when(accountDao.findByAccountId(2)).thenReturn(Optional.of(account(2, "0.00", AccountStatus.APPROVED)));
        when(accountOwnerDao.isOwner(1, 1)).thenReturn(true);
        when(accountOwnerDao.isOwner(1, 2)).thenReturn(false);
        //Act + Assert
        assertThrows(UnauthorizedAccessException.class, () -> accountService.transfer(1, 1, 2, new BigDecimal("10.00")));
        verify(accountDao, never()).updateBalance(anyInt(), any());
    }
    //------------------------------------


    //Apply ------------------------------

    @Test
    void apply_existingCustomer_createsAccountAndOwnerLink() {
        //Arrange
        when(customerDao.findById(7)).thenReturn(Optional.of(new Customer()));
        Account saved = new Account(10, AccountType.SAVINGS, BigDecimal.ZERO, AccountStatus.PENDING, null);
        when(accountDao.create(any(Account.class))).thenReturn(saved);
        //Act
        Account result = accountService.apply(7, AccountType.SAVINGS);
        //Assert
        assertEquals(10, result.getAccountId());
        verify(accountOwnerDao).create(new AccountOwner(7, 10));
    }

    @Test
    void apply_unknownCustomer_throwsCustomerNotFound() {
        //Arrange: no when(...) -> findById returns Optional.empty()
        //Act + Assert
        assertThrows(CustomerNotFoundException.class, () -> accountService.apply(99, AccountType.CHECKING));
        verify(accountDao, never()).create(any());
    }
    //------------------------------------


    //Approve / deny ---------------------

    @Test
    void approve_pendingAccount_updatesStatusWithEmployee() {
        //Arrange
        when(accountDao.findByAccountId(5)).thenReturn(Optional.of(account(5, "0.00", AccountStatus.PENDING)));
        //Act
        accountService.approve(3, 5);
        //Assert
        verify(accountDao).updateStatus(5, AccountStatus.APPROVED, 3);
    }

    @Test
    void deny_pendingAccount_updatesStatusWithEmployee() {
        //Arrange
        when(accountDao.findByAccountId(5)).thenReturn(Optional.of(account(5, "0.00", AccountStatus.PENDING)));
        //Act
        accountService.deny(3, 5);
        //Assert
        verify(accountDao).updateStatus(5, AccountStatus.DENIED, 3);
    }

    @Test
    void approve_alreadyApproved_throwsInvalidState() {
        //Arrange
        when(accountDao.findByAccountId(5)).thenReturn(Optional.of(account(5, "0.00", AccountStatus.APPROVED)));
        //Act + Assert
        assertThrows(InvalidAccountStateException.class, () -> accountService.approve(3, 5));
        verify(accountDao, never()).updateStatus(anyInt(), any(), anyInt());
    }

    @Test
    void deny_alreadyDenied_throwsInvalidState() {
        when(accountDao.findByAccountId(5)).thenReturn(Optional.of(account(5, "0.00", AccountStatus.DENIED)));
        assertThrows(InvalidAccountStateException.class, () -> accountService.deny(3, 5));
        verify(accountDao, never()).updateStatus(anyInt(), any(), anyInt());
    }

    @Test
    void approve_missingAccount_throwsAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> accountService.approve(3, 99));
        verify(accountDao, never()).updateStatus(anyInt(), any(), anyInt());
    }
    //------------------------------------


    //Views ------------------------------

    @Test
    void getMyAccounts_returnsWhatDaoReturns() {
        List<Account> accounts = List.of(account(1, "10.00", AccountStatus.APPROVED));
        when(accountDao.findByCustomerId(7)).thenReturn(accounts);
        assertSame(accounts, accountService.getMyAccounts(7));
    }

    @Test
    void getAllAccounts_returnsWhatDaoReturns() {
        List<Account> accounts = List.of(account(1, "10.00", AccountStatus.APPROVED));
        when(accountDao.findAllAccounts()).thenReturn(accounts);
        assertSame(accounts, accountService.getAllAccounts());
    }

    @Test
    void getPendingAccounts_returnsWhatDaoReturns() {
        List<Account> accounts = List.of(account(5, "0.00", AccountStatus.PENDING));
        when(accountDao.findPendingAccounts()).thenReturn(accounts);
        assertSame(accounts, accountService.getPendingAccounts());
    }

    @Test
    void getOwners_returnsWhatDaoReturns() {
        List<AccountOwner> owners = List.of(new AccountOwner(3, 4), new AccountOwner(4, 4));
        when(accountOwnerDao.findByAccountId(4)).thenReturn(owners);
        assertSame(owners, accountService.getOwners(4));
    }

    @Test
    void getHistory_owner_returnsTransactions() {
        //Arrange
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "10.00", AccountStatus.APPROVED)));
        when(accountOwnerDao.isOwner(1, 1)).thenReturn(true);
        List<Transaction> history = List.of(new Transaction());
        when(transactionDao.findByAccountId(1)).thenReturn(history);
        //Act + Assert
        assertSame(history, accountService.getHistory(1, 1));
    }

    @Test
    void getHistory_notOwner_throwsUnauthorized() {
        when(accountDao.findByAccountId(1)).thenReturn(Optional.of(account(1, "10.00", AccountStatus.APPROVED)));
        when(accountOwnerDao.isOwner(2, 1)).thenReturn(false);
        assertThrows(UnauthorizedAccessException.class, () -> accountService.getHistory(2, 1));
        verify(transactionDao, never()).findByAccountId(anyInt());
    }
    //------------------------------------
}