FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :contacts-svc:clean :contacts-svc:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
# Copia el artefacto JAR generado por Gradle durante la fase de build
# desde la imagen temporal ("build") hacia la imagen final de ejecución.
# - La ruta /workspace/contacts-svc/build/libs/*.jar corresponde al JAR del proyecto contacts-svc.
# - Se coloca en /app/app.jar dentro del contenedor final listo para ejecución.
# Este paso empaqueta la aplicación para ser ejecutada con el comando ENTRYPOINT.
COPY --from=build /workspace/contacts-svc/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]