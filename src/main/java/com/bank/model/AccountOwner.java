package com.bank.model;

import java.util.Objects;

public class AccountOwner {
  /*   CREATE TABLE account_owner (
    customer_id  INT  NOT NULL REFERENCES customer(customer_id),
    account_id   INT  NOT NULL REFERENCES account(account_id),
    PRIMARY KEY (customer_id, account_id)
  ); */

  private int customerId;
  private int accountId;

  // Only two constructors: there's no generated id, so "no id" and "all args" are the same
  public AccountOwner() {}

  public AccountOwner(int customerId, int accountId) {
    this.customerId = customerId;
    this.accountId = accountId;
  }

  public int getCustomerId() {
    return customerId;
  }
  public void setCustomerId(int customerId) {
    this.customerId = customerId;
  }
  public int getAccountId() {
    return accountId;
  }
  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  @Override
  public String toString() {
    return "AccountOwner [customerId=" + customerId + ", accountId=" + accountId + "]";
  }

  // Composite key: identity is BOTH columns together
  @Override
  public int hashCode() {
    return Objects.hash(customerId, accountId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    AccountOwner other = (AccountOwner) obj;
    return customerId == other.customerId && accountId == other.accountId;
  }
}