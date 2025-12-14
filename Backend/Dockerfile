# --- STAGE 1: BUILD ---
# Use a Maven image to build the app
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy the project files into the container
COPY . .

# Build the JAR file (Skipping tests to speed up deployment)
RUN mvn clean package -DskipTests

# --- STAGE 2: RUN ---
# Use a smaller, lighter Java image for the actual running app
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file from the 'build' stage to this stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8081
EXPOSE 8081

# The command to start the app
ENTRYPOINT ["java", "-jar", "app.jar"]