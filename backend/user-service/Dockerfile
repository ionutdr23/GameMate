# ----------- Stage 1: Build the application -----------
FROM gradle:8.13.0-jdk21 AS builder

WORKDIR /app

# Copy Gradle config and wrapper, then set permissions
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew

# Pre-download dependencies (cached separately from source changes)
RUN ./gradlew dependencies --no-daemon

# Now copy the rest of the source code
COPY . .
RUN chmod +x gradlew

# Build the Spring Boot JAR
RUN ./gradlew clean bootJar --no-daemon

# ----------- Stage 2: Run the application -----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the generated JAR from the build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
