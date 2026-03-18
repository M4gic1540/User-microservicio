# ─── Etapa 1: Build con Maven ──────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Se va a copiar solo pom.xml primero para aprovechar cache de capas
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiar fuentes y compilar
COPY src ./src
RUN mvn clean package -DskipTests -q

# ─── Etapa 2: Imagen de producción ─────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Usuario no-root para seguridad
RUN addgroup -S shopsmart && adduser -S shopsmart -G shopsmart
USER shopsmart

# Se va a copiar JAR desde la etapa de build
COPY --from=builder /app/target/usuarios-service-*.jar app.jar

# Configuración JVM optimizada para contenedores
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75 \
               -XX:+UseG1GC \
               -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
