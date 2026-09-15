# Stage 1: Build the application using Maven + JDK
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application using a lightweight JRE-only image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Tells the app to use the docker profile (application-docker.properties) at startup
ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]


