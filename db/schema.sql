-- =========================================================
-- Project 0: Console Banking App - schema
-- Re-runnable: drops everything, then rebuilds.
-- =========================================================

-- Drop children first, then parents
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS account_owner;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS customer;

-- ---------- Parents (no foreign keys) ----------

CREATE TABLE customer (
    customer_id  SERIAL        PRIMARY KEY,
    username     VARCHAR(50)   NOT NULL UNIQUE,
    password     VARCHAR(100)  NOT NULL,
    first_name   VARCHAR(50)   NOT NULL,
    last_name    VARCHAR(50)   NOT NULL,
    address      VARCHAR(200),
    phone        VARCHAR(20),
    email        VARCHAR(100)  UNIQUE
);

CREATE TABLE employee (
    employee_id  SERIAL        PRIMARY KEY,
    username     VARCHAR(50)   NOT NULL UNIQUE,
    password     VARCHAR(100)  NOT NULL,
    first_name   VARCHAR(50)   NOT NULL,
    last_name    VARCHAR(50)   NOT NULL
);

-- ---------- Account (needs employee) ----------

CREATE TABLE account (
    account_id    SERIAL         PRIMARY KEY,
    account_type  VARCHAR(10)    NOT NULL
                  CHECK (account_type IN ('CHECKING', 'SAVINGS')),
    balance       NUMERIC(12,2)  NOT NULL DEFAULT 0
                  CHECK (balance >= 0),
    status        VARCHAR(10)    NOT NULL DEFAULT 'PENDING'
                  CHECK (status IN ('PENDING', 'APPROVED', 'DENIED')),
    reviewed_by   INT            REFERENCES employee(employee_id)
);

-- ---------- Children ----------

CREATE TABLE account_owner (
    customer_id  INT  NOT NULL REFERENCES customer(customer_id),
    account_id   INT  NOT NULL REFERENCES account(account_id),
    PRIMARY KEY (customer_id, account_id)
);

CREATE TABLE transaction (
    transaction_id   SERIAL         PRIMARY KEY,
    from_account_id  INT            REFERENCES account(account_id),
    to_account_id    INT            REFERENCES account(account_id),
    type             VARCHAR(10)    NOT NULL
                     CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
    amount           NUMERIC(12,2)  NOT NULL CHECK (amount > 0),
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (
        (type = 'DEPOSIT'    AND from_account_id IS NULL     AND to_account_id IS NOT NULL)
     OR (type = 'WITHDRAWAL' AND from_account_id IS NOT NULL AND to_account_id IS NULL)
     OR (type = 'TRANSFER'   AND from_account_id IS NOT NULL AND to_account_id IS NOT NULL
                             AND from_account_id <> to_account_id)
    )
);