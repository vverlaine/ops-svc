# ---------- Multi-service Gradle build (generic Dockerfile) ----------
# Usage: build each service by passing the ARG SVC, e.g. SVC=visits-svc

# 1) Build stage
FROM gradle:8.9.0-jdk17 AS build
ARG SVC
WORKDIR /app

# Copiamos todo el monorepo (Gradle cachea y solo recompila lo necesario)
COPY . .

# Construimos SOLO el bootJar del subproyecto indicado
RUN --mount=type=cache,target=/root/.gradle \
    gradle :${SVC}:bootJar --no-daemon

# 2) Runtime stage
FROM eclipse-temurin:17-jre AS runtime
ARG SVC
ENV APP_HOME=/app \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0" \
    SPRING_PROFILES_ACTIVE=default
WORKDIR ${APP_HOME}

# Copiamos el jar generado
COPY --from=build /app/${SVC}/build/libs/*.jar app.jar

# Render inyecta $PORT; para local, fallback 8080
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar" ]