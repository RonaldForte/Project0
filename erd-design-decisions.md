# Bank App ERD: Design Decisions

## Tables

- **Five tables: customer, employee, account, account_owner, transaction.** Each one is a real thing the bank tracks. Transaction came from the verbs (deposit, withdraw, transfer) because the bank needs a record of them.
- **"Account," not "bank."** There's only one bank. The thing that holds money is an account.

## Relationships

- **`account_owner` junction table.** Customers to accounts is many-to-many: a customer can have checking *and* savings, and a joint account has several owners. A single foreign key column on either side can only hold one value, so the link gets its own table.
- **Composite primary key `(customer_id, account_id)`.** Neither column is unique alone, but the pair should never repeat. Both columns are also foreign keys.
- **`reviewed_by` on account, pointing to employee.** "Approve/deny" is the action that links one specific employee to one specific account. It's one-to-many, so the foreign key goes on the "many" side (account). It allows NULL because a pending account hasn't been reviewed yet.
- **Employees have no foreign keys for viewing.** Viewing everything is a query, not a relationship. Who can see what is enforced in the application layers, not by the table layout.

## Keys

- **Surrogate ids, not SSN, username, or email.** Real-world values can change or be missing. Sensitive data shouldn't be copied into foreign keys across tables. A database-generated id never has to change.

## Customer vs employee

- **Separate tables.** This avoids null columns (employees don't need an address), and `reviewed_by` can only point to a real employee. The tradeoff: an employee who also banks there needs a separate customer login, which is acceptable for this project.
- **No `manages` column.** The requirements say employees see *all* customers, so build only what's required (YAGNI).

## Account

- **`account_type` and `status` are enum-style columns.** Checking/savings and pending/approved/denied are fixed sets of values.
- **Money is `NUMERIC(12,2)` in SQL and `BigDecimal` in Java, never float.** Floating point can't represent values like 0.1 exactly, so pennies get lost.

## Transaction

- **One row per transaction, with `from_account_id` and `to_account_id`.** A transfer is a single INSERT.
- **Both foreign keys allow NULL. NULL means "outside the bank."** A deposit has no "from" account and a withdrawal has no "to" account. `type` records what happened. You can't put `DIRECT_DEPOSIT` in a foreign key column, and from 10 to 10 would record something false.
- **History query uses `OR`:**

  ```sql
  WHERE from_account_id = 10 OR to_account_id = 10
  ```

  `AND` would miss deposits and withdrawals. Comparing anything with NULL gives *unknown*, so you write `IS NULL`, never `= NULL`.
