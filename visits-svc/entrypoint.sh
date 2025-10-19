#!/usr/bin/env bash
set -e

# Si Render te da DATABASE_URL=postgres://user:pass@host:port/db,
# conviértelo a JDBC si SPRING_DATASOURCE_URL no está seteado.
if [[ -n "$DATABASE_URL" && -z "$SPRING_DATASOURCE_URL" ]]; then
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DATABASE_URL#postgres://}"
fi

# Asegurar puerto que espera Render
export SERVER_PORT="${PORT:-8094}"

# Email opcional: si no hay host SMTP, apaga envío para evitar fallos en /check-out
if [[ -z "$SPRING_MAIL_HOST" && -z "$MAIL_HOST" ]]; then
  export VISITS_MAIL_ENABLED="${VISITS_MAIL_ENABLED:-false}"
fi

# Defaults razonables (tu application.yml los usa si están vacíos)
export SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH="${SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH:-false}"
export SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE="${SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE:-false}"

exec java -Dserver.port="$SERVER_PORT" -jar /app/app.jar