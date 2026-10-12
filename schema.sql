-- =========================================================================
-- VECTOR Bank Database Schema
-- Compatible with MySQL 8.0+
-- Precision Banking Standards: DECIMAL(15,2) for exact financial currency
-- Secure Credentials: VARCHAR(60) for BCrypt hashes
-- =========================================================================

CREATE DATABASE IF NOT EXISTS banking;
USE banking;

-- 1. Client Table (Account holder profile)
CREATE TABLE IF NOT EXISTS client (
    accountNo INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    dob DATE NOT NULL,
    pan VARCHAR(20) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (accountNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Account Table (Authentication and security credentials)
CREATE TABLE IF NOT EXISTS account (
    accountNo INT NOT NULL,
    password VARCHAR(60) NOT NULL,
    PRIMARY KEY (accountNo),
    CONSTRAINT fk_account_client FOREIGN KEY (accountNo) 
        REFERENCES client (accountNo) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Passbook Table (Immutable audit trail of all transactions)
CREATE TABLE IF NOT EXISTS passbook (
    transactionId INT AUTO_INCREMENT,
    accountNo INT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deposit DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    withdraw DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (transactionId),
    INDEX idx_passbook_account (accountNo),
    CONSTRAINT fk_passbook_client FOREIGN KEY (accountNo) 
        REFERENCES client (accountNo) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
