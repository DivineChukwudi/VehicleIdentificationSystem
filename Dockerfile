# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the jPro release
RUN mvn clean jpro:release -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
# Copy the released jPro application from the build stage
COPY --from=build /app/target/jpro /app/jpro

# Install necessary libraries for JavaFX/jPro
RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libglu1-mesa \
    libasound2 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    || apt-get install -y libasound2t64 \
    && rm -rf /var/lib/apt/lists/* || true

EXPOSE 8080

# The startup script is in jpro/bin/
# We use a more robust startup command to find the executable and ensure it's runnable
CMD ["sh", "-c", "EXE=$(find /app/jpro -name 'VehicleIdentificationSystem*' -type f -not -name '*.jar' | head -n 1); if [ -n \"$EXE\" ]; then chmod +x \"$EXE\"; exec \"$EXE\"; else echo 'Executable not found in /app/jpro'; ls -R /app/jpro; exit 1; fi"]
