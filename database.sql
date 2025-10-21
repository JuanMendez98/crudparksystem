-- ============================================
-- CrudPark - PostgreSQL Database
-- ============================================


-- ============================================
-- TABLES
-- ============================================

-- 1. Operators (operadores)
CREATE TABLE operators (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Monthly Subscriptions (mensualidades)
CREATE TABLE subscriptions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN DEFAULT true,
    notification_sent BOOLEAN DEFAULT false,  -- NEW: For C# email notification control
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_dates CHECK (end_date >= start_date)
);

-- 3. Rates/Pricing (tarifas)
CREATE TABLE rates (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL CHECK (vehicle_type IN ('CAR', 'MOTORCYCLE')),
    hourly_rate DECIMAL(10,2) NOT NULL CHECK (hourly_rate >= 0),
    grace_period INT DEFAULT 30 CHECK (grace_period >= 0),
    fraction_value DECIMAL(10,2) DEFAULT 0,  -- NEW: Additional charge per fraction (C# requirement)
    daily_limit DECIMAL(10,2),               -- NEW: Maximum daily charge (C# requirement)
    active BOOLEAN DEFAULT true
);

-- 4. Tickets
CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL,
    customer_type VARCHAR(20) NOT NULL CHECK (customer_type IN ('SUBSCRIPTION', 'GUEST')),
    vehicle_type VARCHAR(20) NOT NULL CHECK (vehicle_type IN ('CAR', 'MOTORCYCLE')),
    entry_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    exit_date TIMESTAMP,
    folio VARCHAR(20) UNIQUE,
    entry_operator_id INT NOT NULL REFERENCES operators(id),
    exit_operator_id INT REFERENCES operators(id),
    status VARCHAR(20) DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'CLOSED', 'GRACE')),
    qr_code TEXT,
    amount_charged DECIMAL(10,2) DEFAULT 0 CHECK (amount_charged >= 0),
    CONSTRAINT chk_exit CHECK (exit_date IS NULL OR exit_date >= entry_date)
);

-- 5. Payments (pagos)
CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL REFERENCES tickets(id),
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    payment_method VARCHAR(50) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operator_id INT NOT NULL REFERENCES operators(id)
);

-- 6. Shifts (turnos) - Operator work shifts
CREATE TABLE shifts (
    id SERIAL PRIMARY KEY,
    operator_id INT NOT NULL REFERENCES operators(id),
    opening_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closing_date TIMESTAMP,
    opening_cash DECIMAL(10,2) DEFAULT 0,
    closing_cash DECIMAL(10,2),
    total_revenue DECIMAL(10,2),
    total_tickets INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'CLOSED')),
    notes TEXT,
    CONSTRAINT chk_shift_closing CHECK (closing_date IS NULL OR closing_date >= opening_date)
);

-- ============================================
-- IMPORTANT INDEXES
-- ============================================

-- Only one open ticket per license plate
CREATE UNIQUE INDEX idx_ticket_open_unique ON tickets(license_plate) WHERE status = 'OPEN';

-- Performance indexes
CREATE INDEX idx_tickets_license_plate ON tickets(license_plate);
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_subscriptions_license_plate ON subscriptions(license_plate);
CREATE INDEX idx_tickets_entry_date ON tickets(entry_date);
CREATE INDEX idx_payments_date ON payments(payment_date);
CREATE INDEX idx_shifts_operator ON shifts(operator_id);
CREATE INDEX idx_shifts_status ON shifts(status);

-- Only one open shift per operator
CREATE UNIQUE INDEX idx_shift_open_unique ON shifts(operator_id) WHERE status = 'OPEN';

-- ============================================
-- USEFUL VIEWS
-- ============================================

-- Vehicles currently inside the parking lot
CREATE VIEW vehicles_inside AS
SELECT
    t.id,
    t.license_plate,
    t.vehicle_type,
    t.customer_type,
    t.entry_date,
    o.name AS operator_name,
    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - t.entry_date))/60 AS minutes_inside
FROM tickets t
JOIN operators o ON t.entry_operator_id = o.id
WHERE t.status = 'OPEN';

-- Active subscriptions
CREATE VIEW active_subscriptions AS
SELECT * FROM subscriptions
WHERE active = true AND CURRENT_DATE BETWEEN start_date AND end_date;

-- Subscriptions expiring soon (3 days)
CREATE VIEW subscriptions_expiring_soon AS
SELECT
    id,
    name,
    email,
    license_plate,
    end_date,
    (end_date - CURRENT_DATE) AS days_remaining
FROM subscriptions
WHERE active = true
  AND CURRENT_DATE < end_date
  AND (end_date - CURRENT_DATE) <= 3
  AND notification_sent = false;

-- Daily revenue report
CREATE VIEW daily_revenue AS
SELECT
    DATE(p.payment_date) AS date,
    COUNT(p.id) AS total_payments,
    SUM(p.amount) AS total_revenue,
    AVG(p.amount) AS average_payment
FROM payments p
GROUP BY DATE(p.payment_date)
ORDER BY date DESC;

-- ============================================
-- TEST DATA
-- ============================================

INSERT INTO operators (name, username, password, email, active) VALUES
('Admin', 'admin', '123456', 'admin@crudpark.com', true),
('Juan Pérez', 'jperez', '123456', 'jperez@crudpark.com', true),
('María López', 'mlopez', '123456', 'mlopez@crudpark.com', true);

INSERT INTO rates (name, vehicle_type, hourly_rate, grace_period, fraction_value, daily_limit, active) VALUES
('Standard Car Rate', 'CAR', 5000.00, 30, 1250.00, 50000.00, true),
('Standard Motorcycle Rate', 'MOTORCYCLE', 3000.00, 30, 750.00, 30000.00, true);

INSERT INTO subscriptions (name, email, license_plate, start_date, end_date, active, notification_sent) VALUES
('Carlos Mendez', 'cmendez@example.com', 'ABC123', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', true, false),
('Ana García', 'agarcia@example.com', 'XYZ789', CURRENT_DATE - INTERVAL '25 days', CURRENT_DATE + INTERVAL '5 days', true, false);

-- ============================================
-- HELPFUL COMMENTS
-- ============================================

-- Field mapping (Spanish -> English):
-- operadores -> operators
-- mensualidades -> subscriptions
-- tarifas -> rates
-- pagos -> payments
-- placa -> license_plate
-- fecha_ingreso -> entry_date
-- fecha_salida -> exit_date
-- tipo_cliente -> customer_type
-- tipo_vehiculo -> vehicle_type
-- monto_cobrado -> amount_charged
-- tiempo_gracia -> grace_period
-- valor_hora -> hourly_rate
-- activo -> active

-- New fields for C# requirements:
-- subscriptions.notification_sent -> Control email notifications (line 112 of crudpark.md)
-- rates.fraction_value -> Additional charge per fraction (line 120 of crudpark.md)
-- rates.daily_limit -> Maximum daily charge (line 121 of crudpark.md)

-- Enum values mapping:
-- CARRO -> CAR
-- MOTO -> MOTORCYCLE
-- MENSUALIDAD -> SUBSCRIPTION
-- INVITADO -> GUEST
-- ABIERTO -> OPEN
-- CERRADO -> CLOSED
