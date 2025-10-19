# ====== Build ======
FROM gradle:8.10.2-jdk17 AS builder
WORKDIR /app
COPY . .
# Solo construimos el submódulo visits-svc para rapidez
RUN gradle :visits-svc:bootJar --no-daemon

# ====== Runtime ======
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Script de entrypoint: convierte DATABASE_URL a SPRING_DATASOURCE_URL y arranca
COPY visits-svc/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Copiamos el jar
COPY --from=builder /app/visits-svc/build/libs/*.jar /app/app.jar

# Render expone el puerto que escucha el proceso en el contenedor
ENV PORT=8094
EXPOSE 8094

ENTRYPOINT ["/app/entrypoint.sh"]