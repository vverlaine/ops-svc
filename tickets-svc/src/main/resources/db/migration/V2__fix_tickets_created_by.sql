SET search_path TO app;

ALTER TABLE app.tickets
  ALTER COLUMN created_by TYPE varchar(255)
  USING created_by::text;