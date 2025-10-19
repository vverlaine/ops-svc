# -----------------------------------------------------------------------------
# Dockerfile - tickets-svc
# -----------------------------------------------------------------------------
# Propósito:
#   Define la imagen Docker para el microservicio "tickets-svc".
#
# Diseño:
#   - Utiliza un proceso multi-stage build para reducir el tamaño final de la imagen.
#   - Primera etapa ("build"): compila el proyecto Java y genera el archivo .jar.
#   - Segunda etapa ("runtime"): crea una imagen ligera para ejecutar el servicio.
#
# Requisitos:
#   - Eclipse Temurin JDK 17 (para compilación y ejecución).
#   - Gradle Wrapper incluido en el repositorio.
#
# Mantenibilidad:
#   - Si cambia la versión de Java o Gradle, debe actualizarse aquí.
#   - Evita incluir dependencias de desarrollo o pruebas en la imagen final.
# -----------------------------------------------------------------------------

# -------------------- Etapa 1: Compilación del proyecto --------------------
# Se usa una imagen con JDK completo para poder ejecutar Gradle y compilar el código fuente.
FROM eclipse-temurin:17-jdk AS build

# Define el directorio de trabajo dentro del contenedor para la compilación.
WORKDIR /workspace

# Copia todo el contenido del repositorio al directorio de trabajo.
COPY . .

# Ejecuta Gradle para limpiar y compilar el microservicio "tickets-svc",
# generando el archivo ejecutable .jar dentro de build/libs.
# La opción "-x test" omite la ejecución de pruebas unitarias para agilizar la construcción.
RUN ./gradlew :tickets-svc:clean :tickets-svc:bootJar -x test

# -------------------- Etapa 2: Imagen final de ejecución --------------------
# Usa una imagen base más ligera con solo el JRE, suficiente para ejecutar el servicio.
FROM eclipse-temurin:17-jre

# Define el directorio de trabajo donde se colocará la aplicación compilada.
WORKDIR /app

# Copia el archivo .jar generado en la etapa de compilación hacia la imagen final.
COPY --from=build /workspace/tickets-svc/build/libs/*.jar /app/app.jar

# Expone el puerto 8080, donde el microservicio escucha peticiones HTTP.
EXPOSE 8080

# Define el comando de entrada que ejecuta la aplicación Spring Boot.
# Usa la instrucción ENTRYPOINT para mantener el proceso en primer plano.
ENTRYPOINT ["java","-jar","/app/app.jar"]