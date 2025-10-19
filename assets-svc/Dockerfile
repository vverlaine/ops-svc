FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :assets-svc:clean :assets-svc:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
# Copia el archivo JAR generado durante la fase de build desde la imagen temporal
# hacia el contenedor final en /app/app.jar. 
# - "--from=build" indica que toma el artefacto de la etapa anterior llamada "build".
# - "*.jar" permite copiar el archivo JAR construido por Gradle (bootJar).
# Este paso empaqueta la aplicación lista para ejecución en el contenedor final.
COPY --from=build /workspace/assets-svc/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]