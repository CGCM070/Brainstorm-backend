# ---------- Etapa de construcción ----------
FROM maven:3.8.5-openjdk-17 AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar los archivos necesarios para descargar dependencias
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Hacer mvnw ejecutable
RUN chmod +x mvnw

# Descargar dependencias (para cacheo eficiente)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src src

# Compilar y empaquetar la aplicación (sin tests)
RUN ./mvnw clean package -DskipTests


# ---------- Etapa de runtime (Distroless) ----------
FROM gcr.io/distroless/java17

# Establecer directorio de trabajo
WORKDIR /app

# Copiar solo el jar generado desde la etapa build
COPY --from=build /app/target/Brainstorm-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8080

# Ejecutar la aplicación (ENTRYPOINT en Distroless requiere lista)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
