-- Database Schema Initialization for Multicurrency Payment Engine

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create wallets table
CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance DECIMAL(19, 4) NOT NULL,
    CONSTRAINT uq_user_currency UNIQUE (user_id, currency),
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    from_wallet_id BIGINT,
    to_wallet_id BIGINT,
    amount DECIMAL(19, 4) NOT NULL,
    converted_amount DECIMAL(19, 4),
    source_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    fx_rate DECIMAL(19, 6) NOT NULL,
    fx_provider VARCHAR(255),
    fraud_score INT,
    fraud_decision VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    idempotency_key VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create fx_rates table
CREATE TABLE IF NOT EXISTS fx_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    base_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(19, 6) NOT NULL,
    provider VARCHAR(255),
    fetched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create webhook_endpoints table
CREATE TABLE IF NOT EXISTS webhook_endpoints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    secret VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_webhook_endpoint_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create webhook_deliveries table
CREATE TABLE IF NOT EXISTS webhook_deliveries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    endpoint_id BIGINT NOT NULL,
    transaction_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    attempts INT DEFAULT 0,
    last_attempted_at TIMESTAMP NULL DEFAULT NULL,
    next_retry_at TIMESTAMP NULL DEFAULT NULL,
    response_code INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_webhook_delivery_endpoint FOREIGN KEY (endpoint_id) REFERENCES webhook_endpoints(id) ON DELETE CASCADE
);
