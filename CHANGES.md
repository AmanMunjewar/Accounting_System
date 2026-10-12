# CHANGES.md - VECTOR Bank Architecture & Security Modernization

This document details all security vulnerabilities, design flaws, and performance bottlenecks identified in the legacy codebase and how each was resolved in VECTOR Bank v2.0.

---

### 1. Plaintext Passwords
- **Problem**: Passwords were stored in the `account` table as raw plaintext strings (`varchar(10)`). A database breach would instantly expose every customer's banking credentials.
- **Fix**: Integrated **BCrypt** (`org.mindrot:jbcrypt`) with a work factor of 12 (`BCrypt.gensalt(12)`). Passwords are never stored or logged in plain text. Passwords in the database are hashed (`varchar(60)`), and verification uses `BCrypt.checkpw`.

---

### 2. Full-Table Scan on User Validation (`validateClient`)
- **Problem**: `validateClient(account, pass)` executed `SELECT * FROM account` and looped over every single account in memory in a Java `while` loop until it found a match. For $N$ accounts, this caused $O(N)$ network latency, memory usage, and database load.
- **Fix**: Replaced the table scan with an indexed point lookup: `SELECT ... FROM client c LEFT JOIN account a ON c.accountNo = a.accountNo WHERE c.accountNo = ?`. Query execution is $O(1)$ on the database primary key index.

---

### 3. Non-Atomic Deposit / Withdraw Without Overdraft Protection
- **Problem**:
  1. Deposits and withdrawals ran two separate auto-committed SQL statements: an `UPDATE client` followed by an `INSERT INTO passbook`. If the second query failed, the financial ledger became corrupt and un-auditable.
  2. Withdrawals executed `update client set balance = balance - ?` without verifying whether the account had sufficient funds. Accounts could withdraw infinite money into negative balances.
- **Fix**:
  1. Atomic transactions using JDBC connection boundaries (`connection.setAutoCommit(false)`): both the balance update and passbook entry are committed together (`conn.commit()`), and automatically rolled back on any exception (`conn.rollback()`).
  2. Strict balance checking before executing the SQL update. If `requestedAmount > availableBalance`, the transaction aborts immediately and raises `InsufficientFundsException`.

---

### 4. Integer Balance
- **Problem**: Account balances and transaction amounts were stored as `int` in Java and `int` in MySQL. This prevented any decimal cents/paise (e.g., \$10.50 was impossible) and caused integer overflow risk once balances exceeded \$21,474,836.47.
- **Fix**: Upgraded all financial amounts in Java to `java.math.BigDecimal` with explicit scale (`setScale(2, RoundingMode.HALF_UP)`) to prevent binary floating-point roundoff errors. Upgraded MySQL columns to `DECIMAL(15, 2)`.

---

### 5. Hardcoded Database Credentials & Leaked Static Connection Resources
- **Problem**: Database host, port, username, and password were hardcoded (`"jdbc:mysql://localhost:3306/banking", "root", "root"`). The database connection, statement, and result set were static variables (`static Connection connection`), causing thread safety hazards and unclosed resource leaks.
- **Fix**: Created `DatabaseConfig` which reads from environment variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_URL`) with safe local fallbacks. All queries utilize Java's **try-with-resources** pattern (`AutoCloseable`), guaranteeing that connections, statements, and result sets are closed even when exceptions occur.

---

### 6. Swallowed Exceptions
- **Problem**: All errors in the legacy code were caught with `catch (Exception e) { e.printStackTrace(); }`, hiding database outages and errors from caller services, returning corrupted default values (e.g. `0` balance on error), and dumping ugly stack traces to console users.
- **Fix**: Created custom domain exceptions extending `BankingException`:
  - `AccountNotFoundException`
  - `InsufficientFundsException`
  - `AuthenticationException`
  - `BankingDatabaseException`
  
  Removed all `e.printStackTrace()` calls in favor of propagating meaningful exceptions up to the CLI layer, which presents clean user-friendly messages.

---

### 7. Missing Schema Definition for Passbook
- **Problem**: `Database Design.txt` only created `client` and `account` tables. The `passbook` table used in `deposit` and `withdraw` was omitted entirely, causing a fresh installation to crash with `Table 'banking.passbook' doesn't exist`.
- **Fix**: Added a complete, declarative `schema.sql` script including `client`, `account`, and `passbook` with foreign keys, timestamps, indexes (`idx_passbook_account`), and `DECIMAL(15, 2)` column precision.
