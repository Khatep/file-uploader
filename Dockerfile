# ===== STAGE 1: build =====
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# копируем pom.xml для кеширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходники
COPY src ./src

# Собираем JAR
RUN mvn clean package -DskipTests

# ===== STAGE 2: runtime =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем JAR из builder-стадии
COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
