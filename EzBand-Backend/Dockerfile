# Etapa 1: build do JAR
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: imagem final
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copia o JAR gerado da etapa anterior (garante consistência de versão)
COPY --from=builder /app/target/ezband-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=docker -jar app.jar || tail -f /dev/null"]
