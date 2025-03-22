FROM gradle:8.13.0-jdk21 AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew bootJar

FROM openjdk:21-jdk
VOLUME /tmp
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app.jar"]
