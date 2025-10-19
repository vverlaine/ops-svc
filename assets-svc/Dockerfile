FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :assets-svc:clean :assets-svc:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY assets-svc/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]