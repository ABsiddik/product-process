# Step 1: Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build

ARG SPRING_PROFILE=default

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests -Dspring.profiles.active=${SPRING_PROFILE}

# Step 2: Runtime stage
FROM eclipse-temurin:17-jre

ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILE}

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]