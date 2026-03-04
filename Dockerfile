FROM maven:3.9.11-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/financialSystem-0.0.1-SNAPSHOT.jar /app/app.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java"]
CMD ["-jar", "/app/app.jar"]