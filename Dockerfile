# Usar imagen base de Java 17
FROM openjdk:17-jdk-slim

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de Maven (sin Brainstorm/ porque ya estamos dentro)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Hacer mvnw ejecutable
RUN chmod +x mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Exponer puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/Brainstorm-0.0.1-SNAPSHOT.jar"]
