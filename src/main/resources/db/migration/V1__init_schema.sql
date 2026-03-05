CREATE TABLE stores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    master_pin VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admin_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    lock_until TIMESTAMP NULL,
    failed_attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admin_store FOREIGN KEY (store_id) REFERENCES stores(id),
    CONSTRAINT uk_admin_store_username UNIQUE (store_id, username)
);

CREATE TABLE restaurant_tables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    table_number INT NOT NULL,
    pin VARCHAR(10) NOT NULL,
    current_session_id VARCHAR(36) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_table_store FOREIGN KEY (store_id) REFERENCES stores(id),
    CONSTRAINT uk_store_table_number UNIQUE (store_id, table_number)
);

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_store FOREIGN KEY (store_id) REFERENCES stores(id)
);

CREATE TABLE menus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    price INT NOT NULL,
    image_url VARCHAR(500) NULL,
    is_sold_out BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_store FOREIGN KEY (store_id) REFERENCES stores(id)
);

CREATE TABLE menu_categories (
    menu_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (menu_id, category_id),
    CONSTRAINT fk_mc_menu FOREIGN KEY (menu_id) REFERENCES menus(id),
    CONSTRAINT fk_mc_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    session_id VARCHAR(36) NOT NULL,
    total_amount INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_store FOREIGN KEY (store_id) REFERENCES stores(id),
    CONSTRAINT fk_order_table FOREIGN KEY (table_id) REFERENCES restaurant_tables(id)
);

CREATE INDEX idx_order_table_session ON orders(table_id, session_id);
CREATE INDEX idx_order_store_created ON orders(store_id, created_at);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    menu_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit_price INT NOT NULL,
    subtotal INT NOT NULL,
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_oi_menu FOREIGN KEY (menu_id) REFERENCES menus(id)
);

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    table_id BIGINT NULL,
    admin_id BIGINT NULL,
    store_id BIGINT NOT NULL,
    role VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rt_store FOREIGN KEY (store_id) REFERENCES stores(id),
    CONSTRAINT fk_rt_table FOREIGN KEY (table_id) REFERENCES restaurant_tables(id),
    CONSTRAINT fk_rt_admin FOREIGN KEY (admin_id) REFERENCES admin_accounts(id)
);

CREATE TABLE order_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_order_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    session_id VARCHAR(36) NOT NULL,
    total_amount INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    ordered_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_oh_store FOREIGN KEY (store_id) REFERENCES stores(id),
    CONSTRAINT fk_oh_table FOREIGN KEY (table_id) REFERENCES restaurant_tables(id)
);

CREATE TABLE order_history_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_history_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    menu_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit_price INT NOT NULL,
    subtotal INT NOT NULL,
    CONSTRAINT fk_ohi_history FOREIGN KEY (order_history_id) REFERENCES order_history(id)
);
