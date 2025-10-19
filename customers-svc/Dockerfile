FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :customers-svc:clean :customers-svc:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/customers-svc/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]