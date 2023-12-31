# Use the official Gradle image as a build-time image
FROM gradle:jdk17 as builder

# Set the current working directory inside the image
WORKDIR /app

# Copy the build.gradle and settings.gradle files to download dependencies
COPY build.gradle settings.gradle ./

# Download dependencies (this is an optimization to only download dependencies if they haven't changed)
RUN gradle dependencies

# Copy the local code to the container
COPY src ./src/

# Build a release artifact.
RUN gradle bootJar --no-daemon

# Use the official Java image as the base image
FROM eclipse-temurin:17

# Set the current working directory inside the image
WORKDIR /app

# Copy the jar file built in the builder image
COPY --from=builder /app/build/libs/accounting-0.0.1-SNAPSHOT.jar ./app.jar


# Specify the command to run on container start
CMD ["java", "-jar", "app.jar"]
