FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :sites-svc:clean :sites-svc:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
# Copia el artefacto JAR generado durante la fase de construcción ("build")
# desde la imagen temporal hacia la imagen final de ejecución.
# - La ruta `/workspace/sites-svc/build/libs/*.jar` apunta al archivo JAR compilado del microservicio "sites-svc".
# - Se copia dentro del contenedor final en `/app/app.jar`, que será ejecutado por el comando ENTRYPOINT.
# Este paso empaqueta la aplicación lista para ejecutarse en el entorno de producción.
COPY --from=build /workspace/sites-svc/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]