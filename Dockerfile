# Multi-stage build for Spring Boot application
FROM gradle:8.5-jdk17 AS build

# Set working directory
WORKDIR /app

# Copy gradle files
COPY NergetBackend/gradle/ NergetBackend/gradle/
COPY NergetBackend/gradlew NergetBackend/gradlew
COPY NergetBackend/gradlew.bat NergetBackend/gradlew.bat
COPY NergetBackend/build.gradle NergetBackend/build.gradle
COPY NergetBackend/settings.gradle NergetBackend/settings.gradle

# Copy source code
COPY NergetBackend/src/ NergetBackend/src/

# Build the application
WORKDIR /app/NergetBackend
RUN ./gradlew clean build -x test

# Runtime stage
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/NergetBackend/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Set JVM options for container environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
