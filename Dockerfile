# syntax=docker/dockerfile:1.7

# Build stage: compila o projeto dentro do container para garantir build reprodutível
FROM maven:3.9.11-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package

# Runtime stage: imagem final mínima, sem Maven/fonte
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Spring Boot gera dois artefatos: .jar (executável) e .jar.original (não executável)
COPY --from=builder /app/target/financialSystem-0.0.1-SNAPSHOT.jar /app/app.jar

# Hardening básico
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

# ENTRYPOINT fixa o executável; CMD define argumentos padrão e pode ser sobrescrito no runtime
ENTRYPOINT ["java"]
CMD ["-jar", "/app/app.jar"]