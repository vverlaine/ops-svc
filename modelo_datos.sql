-- ============================================================
--  Esquema base SkyNet (PostgreSQL)
-- ============================================================

CREATE SCHEMA IF NOT EXISTS app AUTHORIZATION CURRENT_USER;
SET search_path TO app, public;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ------------------------------------------------------------
--  Tipos enumerados usados por visitas y notas
-- ------------------------------------------------------------
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'visit_state' AND pg_type.typnamespace = 'app'::regnamespace) THEN
    CREATE TYPE app.visit_state AS ENUM ('PLANNED','STARTED','DONE','CANCELLED','NO_SHOW');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'visit_priority' AND pg_type.typnamespace = 'app'::regnamespace) THEN
    CREATE TYPE app.visit_priority AS ENUM ('LOW','MEDIUM','HIGH');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'note_visibility' AND pg_type.typnamespace = 'app'::regnamespace) THEN
    CREATE TYPE app.note_visibility AS ENUM ('INTERNAL','CUSTOMER');
  END IF;
END $$;

-- ------------------------------------------------------------
--  Usuarios (módulo de autenticación/roles)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app.users (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email         TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  role          TEXT NOT NULL CHECK (role IN ('ADMIN','SUPERVISOR','TECNICO')),
  name          TEXT NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO app.users (email, password_hash, role, name)
VALUES ('admin@demo.com', crypt('admin123', gen_salt('bf')), 'ADMIN', 'Admin Demo')
ON CONFLICT (email) DO NOTHING;

-- ------------------------------------------------------------
--  Clientes + sitios opcionales (lat/lng para Google Maps)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app.customers (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name       TEXT NOT NULL,
  tax_id     TEXT,
  email      TEXT,
  phone      TEXT,
  address    TEXT,
  city       TEXT,
  state      TEXT,
  country    TEXT,
  latitude   NUMERIC(9,6),
  longitude  NUMERIC(9,6),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_customers_tax_id ON app.customers(tax_id) WHERE tax_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS app.customer_sites (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id UUID NOT NULL REFERENCES app.customers(id) ON DELETE CASCADE,
  name        TEXT NOT NULL,
  address     TEXT,
  city        TEXT,
  state       TEXT,
  country     TEXT,
  latitude    NUMERIC(9,6),
  longitude   NUMERIC(9,6),
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_customer_sites_customer ON app.customer_sites(customer_id);

-- ------------------------------------------------------------
--  Técnicos y supervisores
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app.technicians (
  user_id    UUID PRIMARY KEY REFERENCES app.users(id) ON DELETE CASCADE,
  user_name  VARCHAR(255),
  active     BOOLEAN NOT NULL DEFAULT TRUE,
  skills     TEXT[],
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS app.supervisors (
  user_id    UUID PRIMARY KEY REFERENCES app.users(id) ON DELETE CASCADE,
  user_name  VARCHAR(255),
  team_id    UUID UNIQUE,
  active     BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
--  Visitas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app.visits (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id       UUID NOT NULL REFERENCES app.customers(id) ON DELETE RESTRICT,
  site_id           UUID REFERENCES app.customer_sites(id) ON DELETE SET NULL,
  technician_id     UUID NOT NULL REFERENCES app.technicians(user_id) ON DELETE RESTRICT,
  state             app.visit_state NOT NULL DEFAULT 'PLANNED',
  priority          app.visit_priority NOT NULL DEFAULT 'MEDIUM',
  purpose           VARCHAR(200),
  scheduled_start_at TIMESTAMPTZ NOT NULL,
  scheduled_end_at   TIMESTAMPTZ NOT NULL,
  notes_planned     VARCHAR(2000),
  check_in_at       TIMESTAMPTZ,
  check_out_at      TIMESTAMPTZ,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- trigger para updated_at
CREATE OR REPLACE FUNCTION app.set_updated_at()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at := now();
  RETURN NEW;
END $$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_visits_updated_at') THEN
    CREATE TRIGGER trg_visits_updated_at
      BEFORE UPDATE ON app.visits
      FOR EACH ROW EXECUTE FUNCTION app.set_updated_at();
  END IF;
END $$;

CREATE INDEX IF NOT EXISTS ix_visits_technician_start ON app.visits(technician_id, scheduled_start_at);
CREATE INDEX IF NOT EXISTS ix_visits_state ON app.visits(state);

-- ------------------------------------------------------------
--  Eventos, notas y correos de visitas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app.visit_events (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  visit_id   UUID NOT NULL REFERENCES app.visits(id) ON DELETE CASCADE,
  type       VARCHAR(50) NOT NULL,
  actor_id   UUID,
  geo_lat    DOUBLE PRECISION,
  geo_lng    DOUBLE PRECISION,
  payload    TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_events_visit ON app.visit_events(visit_id);

CREATE TABLE IF NOT EXISTS app.visit_notes (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  visit_id    UUID NOT NULL REFERENCES app.visits(id) ON DELETE CASCADE,
  author_id   UUID NOT NULL,
  visibility  app.note_visibility NOT NULL DEFAULT 'INTERNAL',
  body        VARCHAR(4000) NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_notes_visit ON app.visit_notes(visit_id);

CREATE TABLE IF NOT EXISTS app.visit_emails (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  visit_id      UUID NOT NULL REFERENCES app.visits(id) ON DELETE CASCADE,
  to_email      VARCHAR(320) NOT NULL,
  subject       VARCHAR(300),
  status        VARCHAR(30),
  error_message VARCHAR(1000),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_emails_visit ON app.visit_emails(visit_id);
