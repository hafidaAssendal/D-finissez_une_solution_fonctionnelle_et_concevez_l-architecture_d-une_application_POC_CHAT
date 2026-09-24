-- ============================================================
-- Your Car Your Way — Schema SQL (PostgreSQL 16)
-- 10 entites, 3NF, UUID v4, TIMESTAMP WITH TIME ZONE
-- ============================================================

-- ========================
-- 1. Types ENUM
-- ========================

CREATE TYPE role_enum AS ENUM ('CLIENT', 'AGENT', 'ADMIN');

CREATE TYPE reservation_status AS ENUM ('CONFIRMED', 'MODIFIED', 'CANCELLED', 'COMPLETED');

CREATE TYPE payment_status AS ENUM ('PENDING', 'SUCCEEDED', 'FAILED', 'REFUNDED');

CREATE TYPE chat_status AS ENUM ('OPEN', 'CLOSED');

CREATE TYPE sender_type AS ENUM ('USER', 'AGENT');

CREATE TYPE message_direction AS ENUM ('INBOUND', 'OUTBOUND');


-- ========================
-- 2. Extension UUID
-- ========================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- ============================================================
-- 3. Tables
-- ============================================================

-- --------------------------
-- Table : users
-- --------------------------
CREATE TABLE users (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email               VARCHAR(255) NOT NULL UNIQUE,
    password_hash       VARCHAR(255),
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    date_of_birth       DATE,
    address             VARCHAR(255),
    phone               VARCHAR(20),
    preferred_language  VARCHAR(10) NOT NULL DEFAULT 'en',
    preferred_currency  VARCHAR(3) NOT NULL DEFAULT 'EUR',
    role                role_enum NOT NULL DEFAULT 'CLIENT',
    email_verified      BOOLEAN NOT NULL DEFAULT FALSE,
    oauth_provider      VARCHAR(50),
    oauth_provider_id   VARCHAR(255),
    stripe_customer_id  VARCHAR(255),
    total_preferences   JSONB DEFAULT '{}',
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMP WITH TIME ZONE
);

-- --------------------------
-- Table : agencies
-- --------------------------
CREATE TABLE agencies (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255) NOT NULL,
    address         VARCHAR(500) NOT NULL,
    city            VARCHAR(100) NOT NULL,
    country         VARCHAR(100) NOT NULL,
    postal_code     VARCHAR(20),
    latitude        DECIMAL(10,7),
    longitude       DECIMAL(10,7),
    phone           VARCHAR(20),
    opening_hours   JSONB DEFAULT '{}',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- --------------------------
-- Table : vehicle_categories
-- --------------------------
CREATE TABLE vehicle_categories (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    acriss_code     VARCHAR(4) NOT NULL UNIQUE,
    label           VARCHAR(100) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- --------------------------
-- Table : vehicles
-- --------------------------
CREATE TABLE vehicles (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agency_id       UUID NOT NULL REFERENCES agencies(id),
    category_id     UUID NOT NULL REFERENCES vehicle_categories(id),
    model           VARCHAR(100) NOT NULL,
    brand           VARCHAR(100) NOT NULL,
    year            INTEGER NOT NULL CHECK (year >= 1900 AND year <= 2100),
    photo_url       VARCHAR(500),
    available       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- --------------------------
-- Table : offers
-- --------------------------
CREATE TABLE offers (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    departure_agency_id     UUID NOT NULL REFERENCES agencies(id),
    return_agency_id        UUID NOT NULL REFERENCES agencies(id),
    category_id             UUID NOT NULL REFERENCES vehicle_categories(id),
    start_date              TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date                TIMESTAMP WITH TIME ZONE NOT NULL,
    price_amount            DECIMAL(10,2) NOT NULL CHECK (price_amount >= 0),
    price_currency          VARCHAR(3) NOT NULL DEFAULT 'EUR',
    available               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_offer_dates CHECK (end_date > start_date)
);

-- --------------------------
-- Table : reservations
-- --------------------------
CREATE TABLE reservations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id),
    offer_id        UUID NOT NULL REFERENCES offers(id),
    status          reservation_status NOT NULL DEFAULT 'CONFIRMED',
    total_amount    DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    currency        VARCHAR(3) NOT NULL DEFAULT 'EUR',
    options         JSONB DEFAULT '{}',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- --------------------------
-- Table : payments
-- --------------------------
CREATE TABLE payments (
    id                          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reservation_id              UUID NOT NULL UNIQUE REFERENCES reservations(id),
    stripe_payment_intent_id    VARCHAR(255),
    amount                      DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    currency                    VARCHAR(3) NOT NULL DEFAULT 'EUR',
    payment_status              payment_status NOT NULL DEFAULT 'PENDING',
    refund_amount               DECIMAL(10,2) DEFAULT 0,
    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- --------------------------
-- Table : chat_conversations
-- --------------------------
CREATE TABLE chat_conversations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id),
    agent_id        UUID REFERENCES users(id),
    agent_name      VARCHAR(100),
    status          chat_status NOT NULL DEFAULT 'OPEN',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    closed_at       TIMESTAMP WITH TIME ZONE
);

-- --------------------------
-- Table : chat_messages
-- --------------------------
CREATE TABLE chat_messages (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id     UUID NOT NULL REFERENCES chat_conversations(id) ON DELETE CASCADE,
    sender_type         sender_type NOT NULL,
    content             TEXT NOT NULL,
    delivered_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- --------------------------
-- Table : messages
-- --------------------------
CREATE TABLE messages (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id),
    subject         VARCHAR(255),
    content         TEXT NOT NULL,
    direction       message_direction NOT NULL,
    attachments     JSONB DEFAULT '[]',
    read            BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);


-- ============================================================
-- 4. Index
-- ============================================================

-- Recherche utilisateur par email (login)
CREATE INDEX idx_users_email ON users(email);

-- Vehicules par agence et disponibilite
CREATE INDEX idx_vehicles_agency_available ON vehicles(agency_id, available);

-- Offres par categorie et disponibilite
CREATE INDEX idx_offers_category_available ON offers(category_id, available);

-- Offres par agence de depart
CREATE INDEX idx_offers_departure_agency ON offers(departure_agency_id);

-- Reservations par utilisateur et statut
CREATE INDEX idx_reservations_user_status ON reservations(user_id, status);

-- Paiements par statut
CREATE INDEX idx_payments_status ON payments(payment_status);

-- Conversations par utilisateur
CREATE INDEX idx_chat_conversations_user ON chat_conversations(user_id);

-- Messages de chat par conversation (tri chronologique)
CREATE INDEX idx_chat_messages_conversation ON chat_messages(conversation_id, delivered_at);

-- Messages asynchrones par utilisateur
CREATE INDEX idx_messages_user ON messages(user_id, created_at);

-- Utilisateurs soft-deleted
CREATE INDEX idx_users_deleted ON users(deleted_at) WHERE deleted_at IS NOT NULL;

-- Index GIN sur JSONB (options de reservation)
CREATE INDEX idx_reservations_options ON reservations USING GIN (options);
