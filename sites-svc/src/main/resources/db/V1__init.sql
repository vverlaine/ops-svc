-- Respeta exactamente tu definición original
CREATE TABLE IF NOT EXISTS app.customer_sites (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id  UUID NOT NULL REFERENCES app.customers(id) ON DELETE CASCADE,
  name         TEXT NOT NULL,
  address      TEXT,
  city         TEXT,
  state        TEXT,
  country      TEXT,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS ix_customer_sites_customer
  ON app.customer_sites(customer_id);