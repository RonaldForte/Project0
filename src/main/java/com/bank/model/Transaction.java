package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
  /*   CREATE TABLE transaction (
    transaction_id   SERIAL         PRIMARY KEY,
    from_account_id  INT            REFERENCES account(account_id),   -- NULL for deposits
    to_account_id    INT            REFERENCES account(account_id),   -- NULL for withdrawals
    type             VARCHAR(10)    NOT NULL CHECK (...),
    amount           NUMERIC(12,2)  NOT NULL CHECK (amount > 0),
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
  ); */

  private int transactionId;
  private Integer fromAccountId;   // nullable -> Integer
  private Integer toAccountId;     // nullable -> Integer
  private TransactionType type;
  private BigDecimal amount;
  private LocalDateTime createdAt;

  // Constructors: no args, no id (DB fills id AND created_at), all args
  public Transaction() {}

  public Transaction(Integer fromAccountId, Integer toAccountId, TransactionType type, BigDecimal amount) {
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
    this.type = type;
    this.amount = amount;
  }

  public Transaction(int transactionId, Integer fromAccountId, Integer toAccountId,
                     TransactionType type, BigDecimal amount, LocalDateTime createdAt) {
    this.transactionId = transactionId;
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
    this.type = type;
    this.amount = amount;
    this.createdAt = createdAt;
  }

  public int getTransactionId() {
    return transactionId;
  }
  public void setTransactionId(int transactionId) {
    this.transactionId = transactionId;
  }
  public Integer getFromAccountId() {
    return fromAccountId;
  }
  public void setFromAccountId(Integer fromAccountId) {
    this.fromAccountId = fromAccountId;
  }
  public Integer getToAccountId() {
    return toAccountId;
  }
  public void setToAccountId(Integer toAccountId) {
    this.toAccountId = toAccountId;
  }
  public TransactionType getType() {
    return type;
  }
  public void setType(TransactionType type) {
    this.type = type;
  }
  public BigDecimal getAmount() {
    return amount;
  }
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return "Transaction [transactionId=" + transactionId + ", fromAccountId=" + fromAccountId
        + ", toAccountId=" + toAccountId + ", type=" + type + ", amount=" + amount
        + ", createdAt=" + createdAt + "]";
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(transactionId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Transaction other = (Transaction) obj;
    return transactionId == other.transactionId;
  }
}