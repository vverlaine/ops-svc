FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :technicians-svc:clean :technicians-svc:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY technicians-svc/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]