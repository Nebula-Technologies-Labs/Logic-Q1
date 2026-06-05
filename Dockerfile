# syntax=docker/dockerfile:1

# ---- Build stage (JDK 25) ----
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Build the app
COPY src ./src
RUN mvn -B -q -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/crm-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
