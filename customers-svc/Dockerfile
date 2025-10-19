FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :customers-svc:clean :customers-svc:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
# Copia el artefacto JAR generado durante la fase de construcción (`build`) 
# desde la imagen temporal hacia la imagen final de ejecución.
# - La ruta `/workspace/customers-svc/build/libs/*.jar` apunta al JAR compilado del servicio customers-svc.
# - Se coloca en `/app/app.jar` dentro del contenedor final.
# Este paso empaqueta la aplicación lista para ejecución en el entorno de producción.
COPY --from=build /workspace/customers-svc/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]