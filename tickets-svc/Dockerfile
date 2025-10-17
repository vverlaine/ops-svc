# Build
FROM gradle:8.7-jdk17-alpine AS build
WORKDIR /src
COPY .. .
RUN gradle :tickets-svc:clean :tickets-svc:bootJar --no-daemon

# Run
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/tickets-svc/build/libs/*SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
CMD ["sh","-c","java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]