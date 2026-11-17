package com.bank.model;

import java.math.BigDecimal;

public class Account {
  /*   CREATE TABLE account (
    account_id    SERIAL         PRIMARY KEY,
    account_type  VARCHAR(10)    NOT NULL CHECK (...),
    balance       NUMERIC(12,2)  NOT NULL DEFAULT 0 CHECK (balance >= 0),
    status        VARCHAR(10)    NOT NULL DEFAULT 'PENDING' CHECK (...),
    reviewed_by   INT            REFERENCES employee(employee_id)
  ); */

  private int accountId;
  private AccountType accountType;
  private BigDecimal balance;
  private AccountStatus status;
  private Integer reviewedBy;   // Integer, not int: NULL until an employee reviews it

  // Constructors: no args, new application (no id), all args
  public Account() {}

  // New application: starts like the DB defaults ($0, PENDING, not reviewed)
  public Account(AccountType accountType) {
    this.accountType = accountType;
    this.balance = BigDecimal.ZERO;
    this.status = AccountStatus.PENDING;
    this.reviewedBy = null;
  }

  public Account(int accountId, AccountType accountType, BigDecimal balance,
                 AccountStatus status, Integer reviewedBy) {
    this.accountId = accountId;
    this.accountType = accountType;
    this.balance = balance;
    this.status = status;
    this.reviewedBy = reviewedBy;
  }

  public int getAccountId() {
    return accountId;
  }
  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }
  public AccountType getAccountType() {
    return accountType;
  }
  public void setAccountType(AccountType accountType) {
    this.accountType = accountType;
  }
  public BigDecimal getBalance() {
    return balance;
  }
  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
  public AccountStatus getStatus() {
    return status;
  }
  public void setStatus(AccountStatus status) {
    this.status = status;
  }
  public Integer getReviewedBy() {
    return reviewedBy;
  }
  public void setReviewedBy(Integer reviewedBy) {
    this.reviewedBy = reviewedBy;
  }

  @Override
  public String toString() {
    return "Account [accountId=" + accountId + ", accountType=" + accountType + ", balance=" + balance
        + ", status=" + status + ", reviewedBy=" + reviewedBy + "]";
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(accountId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Account other = (Account) obj;
    return accountId == other.accountId;
  }
}