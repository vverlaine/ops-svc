
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS app.tickets (
  id           UUID PRIMARY KEY,
  title        VARCHAR(200) NOT NULL,
  description  TEXT,
  status       VARCHAR(32)  NOT NULL DEFAULT 'OPEN',
  priority     VARCHAR(32)  NOT NULL DEFAULT 'MEDIUM',
  customer_id  UUID,
  asset_id     UUID,
  created_by   VARCHAR(200) NOT NULL,
  created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE OR REPLACE FUNCTION app.set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_tickets_updated_at ON app.tickets;
CREATE TRIGGER trg_tickets_updated_at
BEFORE UPDATE ON app.tickets
FOR EACH ROW
EXECUTE FUNCTION app.set_updated_at();