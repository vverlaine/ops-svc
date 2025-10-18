CREATE TABLE IF NOT EXISTS app.assets (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id   UUID NOT NULL REFERENCES app.customers(id) ON DELETE CASCADE,
  site_id       UUID REFERENCES app.customer_sites(id) ON DELETE SET NULL,
  serial_number TEXT,
  model         TEXT,
  type          TEXT,
  installed_at  DATE,
  notes         TEXT,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);