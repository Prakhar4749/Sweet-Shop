# --- STAGE 1: BUILD ---
# Use the Maven image based on Eclipse Temurin (Standard & Stable)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the project files
COPY . .

# Build the JAR file
RUN mvn clean package -DskipTests

# --- STAGE 2: RUN ---
# Use Eclipse Temurin for the runtime (Replaces the broken openjdk image)
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8081
EXPOSE 8081

# The command to start the app
ENTRYPOINT ["java", "-jar", "app.jar"]