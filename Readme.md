# Brainstorm Backend - Compilación Nativa con GraalVM

## 1. Contexto del Proyecto
Este backend está desarrollado en Java con Spring Boot y se aloja en el plan gratuito de Render, que ofrece recursos limitados (0.1 CPU y 256 MB de RAM). El objetivo principal es optimizar los tiempos de arranque y el consumo de recursos, especialmente en entornos con restricciones severas.

## 2. Motivación: ¿Por qué GraalVM y Compilación Nativa?
- GraalVM es una máquina virtual de alto rendimiento que permite compilar aplicaciones Java en binarios nativos.
- La compilación nativa elimina la necesidad de una JVM en tiempo de ejecución, lo que reduce drásticamente el tiempo de arranque y el uso de memoria.
- Esto es especialmente útil en plataformas como Render, donde los recursos son escasos y el cold start afecta la experiencia del usuario.

## 3. Estrategia Docker

### 3.1 Dockerfile Estándar
- **Descripción:** Utiliza una imagen base de Java y empaqueta la aplicación como un JAR tradicional.
- **Ventaja:** Fácil de implementar y compatible con la mayoría de plataformas.
- **Desventaja:** Tiempos de arranque elevados (~4 minutos en Render).

### 3.2 Dockerfile.optimized (Distroless)
- **Descripción:** Usa imágenes distroless y Spring Boot Layered JARs para reducir el tamaño y mejorar el arranque.
- **Ventaja:** Arranque más rápido (~2 minutos).
- **Desventaja:** Las imágenes distroless no incluyen shell, lo que puede causar problemas en plataformas que requieren comandos adicionales.

### 3.3 Dockerfile.native (Compilación Nativa con GraalVM)
- **Descripción:** Compila la aplicación en binario nativo usando GraalVM y empaqueta en una imagen base Debian.
- **Ventaja:** Arranque casi instantáneo (<1 segundo), ColdStart de render ( con nativa unos 20s) y  menor consumo de memoria.
- **Desventaja:** El proceso de compilación requiere más recursos y configuración adicional.

## 4. Problemas Encontrados y Soluciones

### 4.1 Compilación en Render
- **Problema:** Render limita la memoria a 8GB, insuficiente para compilar nativamente.
- **Solución:** Compilar localmente y solo copiar el binario al contenedor.

### 4.2 Límite de GitHub (>100MB)
- **Problema:** No es posible hacer commit del binario nativo.
- **Solución:** Usar GitHub Actions para compilar y publicar la imagen automáticamente en GitHub Container Registry.

### 4.3 Fallo con Distroless
- **Problema:** Render requiere un shell para ejecutar el binario, pero distroless no lo incluye.
- **Solución:** Cambiar la imagen base a Debian.

## 5. Descripción de los Dockerfiles

- **Dockerfile:** Compila y ejecuta la aplicación como JAR tradicional. Base: gcr.io/distroless/java17. Uso recomendado solo para desarrollo o entornos sin restricciones de arranque.
- **Dockerfile.optimized:** Utiliza Spring Boot Layered JARs y una imagen distroless para optimizar el tamaño y el arranque. Base: eclipse-temurin:21-jre-alpine y distroless. Requiere soporte para imágenes sin shell.
- **Dockerfile.native:** Compila la aplicación en binario nativo con GraalVM. Base: ghcr.io/graalvm/native-image-community:17 para build, debian:bookworm-slim para runtime. Copia solo el binario final, expone el puerto y ejecuta directamente.

## 6. Integración con GitHub Actions
El workflow de GitHub Actions (`.github/workflows/build-native.yml`) automatiza la compilación nativa y la publicación de la imagen Docker:
- Clona el repositorio.
- Instala GraalVM y prepara el entorno.
- Compila la aplicación en binario nativo.
- Empaqueta el binario en una imagen Docker basada en Debian.
- Publica la imagen en GitHub Container Registry.
Esto permite mantener el repositorio limpio y evitar problemas con archivos binarios grandes.

## 7. Configuraciones Específicas para Compilación Nativa
La compilación nativa requiere configuración adicional para soportar reflexiones, recursos y WebSockets:
- **reflect-config.json:** Especifica las clases que requieren reflexión en tiempo de ejecución (por ejemplo, controladores, servicios, DTOs).
- **jni-config.json:** Define los drivers JDBC necesarios para la base de datos.
- **resource-config.json:** Incluye archivos de configuración y recursos requeridos por la aplicación.
Estas configuraciones son esenciales porque GraalVM elimina por defecto el soporte dinámico de reflexión y recursos, lo que puede causar errores si no se declaran explícitamente.

## 8. Comparación: JVM Tradicional vs. Binario Nativo
| Aspecto           | JVM Tradicional | Binario Nativo (GraalVM) |
|-------------------|-----------------|--------------------------|
| Tiempo de arranque| ~4 min          | <1 seg                   |
| Compatibilidad    | Alta            | Requiere configuración   |

## 9. Guía Paso a Paso para el Flujo Completo

### 9.1 Compilar localmente (opcional)
- Instalar GraalVM 17.
- Ejecutar:
  ```bash
  ./mvnw -Pnative clean compile spring-boot:process-aot native:compile-no-fork -DskipTests
  ```
- Verificar que el binario se genera en `target/Brainstorm`.

### 9.2 Automatizar con GitHub Actions
- Configurar el workflow en `.github/workflows/build-native.yml`.
- Al hacer push a la rama configurada, GitHub Actions compila y publica la imagen nativa.

### 9.3 Desplegar en Render
- Configurar Render para usar la imagen publicada en GitHub Container Registry.
- Asegurarse de exponer el puerto correcto y definir variables de entorno necesarias.

### 9.4 Verificar funcionamiento
- Comprobar tiempos de arranque y consumo de recursos.
- Validar que los endpoints y WebSockets funcionen correctamente.

## 10. Conclusión
La compilación nativa con GraalVM es una estrategia eficaz para optimizar aplicaciones Java en entornos con recursos limitados. Requiere configuración adicional, pero ofrece mejoras significativas en tiempos de arranque y eficiencia. 

