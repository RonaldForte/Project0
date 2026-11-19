-- =========================================================
-- Project 0: Console Banking App - sample data
-- Run schema.sql FIRST (it resets everything), then this file.
--Quick logins: username test / password test (customer AND employee)
-- =========================================================

-- Employees (seeded only: employees neveregister through the app)
INSERT INTO employee (username, password, first_name, last_name) VALUES
    ('test',   'test',     'Test',  'Employee'),   -- employee_id 1
    ('alee',   'admin123', 'Ann',   'Lee'),        -- employee_id 2
    ('jsmith', 'password', 'John',  'Smith');      -- employee_id 3

-- Customers
INSERT INTO customer (username, password, first_name, last_name, address, phone, email) VALUES
    ('test',   'test',    'Test',   'Customer', '1 Test St, Brooklyn, NY',   '555-0100', 'test@bank.com'),    -- customer_id 1
    ('ronald', 'pass123', 'Ronald', 'Forte',    '12 Court St, Brooklyn, NY', '555-0101', 'ronald@bank.com'),  -- customer_id 2
    ('maria',  'pass123', 'Maria',  'Lopez',    '45 Main St, Queens, NY',    '555-0102', 'maria@bank.com'),   -- customer_id 3
    ('sam',    'pass123', 'Sam',    'Patel',    '45 Main St, Queens, NY',    '555-0103', 'sam@bank.com');     -- customer_id 4

--Accounts(balances match the transactions below)
INSERT INTO account (account_type, balance, status, reviewed_by) VALUES
    ('CHECKING',  350.00, 'APPROVED', 1),     -- account_id 1: test
    ('SAVINGS',  2500.00, 'APPROVED', 1),     -- account_id 2: test
    ('CHECKING', 1200.00, 'APPROVED', 2),     -- account_id 3: ronald
    ('CHECKING',  300.00, 'APPROVED', 2),     -- account_id 4: maria + sam (joint)
    ('SAVINGS',     0.00, 'PENDING',  NULL),  -- account_id 5: test (waiting for review)
    ('CHECKING',    0.00, 'PENDING',  NULL),  -- account_id 6: maria
    ('SAVINGS',     0.00, 'DENIED',   3);     -- account_id 7: sam

-- Who owns what
INSERT INTO account_owner (customer_id, account_id) VALUES
    (1, 1), (1, 2), (1, 5),
    (2, 3),
    (3, 4), (4, 4),
    (3, 6),
    (4, 7);

-- History (NULL from = money came in from outside, NULL to = money left the bank)
INSERT INTO transaction (from_account_id, to_account_id, type, amount, created_at) VALUES
    (NULL, 1,    'DEPOSIT',    1000.00, NOW() - INTERVAL '6 days'),
    (NULL, 2,    'DEPOSIT',    2000.00, NOW() - INTERVAL '6 days'),
    (1,    NULL, 'WITHDRAWAL',  150.00, NOW() - INTERVAL '4 days'),
    (1,    2,    'TRANSFER',    500.00, NOW() - INTERVAL '2 days'),
    (NULL, 3,    'DEPOSIT',    1200.00, NOW() - INTERVAL '5 days'),
    (NULL, 4,    'DEPOSIT',     300.00, NOW() - INTERVAL '3 days');