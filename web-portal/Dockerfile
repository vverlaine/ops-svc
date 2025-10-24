# syntax=docker/dockerfile:1

##
## Build stage
##
FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

# Copiamos archivos Gradle y wrapper
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle gradle

# Copiamos el módulo del portal web
COPY web-portal web-portal

# Aseguramos permisos de ejecución del wrapper
RUN chmod +x gradlew

# Generamos el artefacto del portal
RUN ./gradlew :web-portal:bootJar --no-daemon

##
## Runtime stage
##
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Copiamos el jar construido
COPY --from=build /workspace/web-portal/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
