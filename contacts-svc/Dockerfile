FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :contacts-svc:clean :contacts-svc:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY contacts-svc/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]