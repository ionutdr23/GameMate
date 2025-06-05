# ----------- Stage 1: Build the application -----------
FROM gradle:8.13.0-jdk21 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy only necessary files for dependency resolution
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy the source code
COPY . .

# Build the Spring Boot application
RUN ./gradlew clean bootJar --no-daemon

# ----------- Stage 2: Run the application -----------
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=80.0", "-jar", "app.jar"]
