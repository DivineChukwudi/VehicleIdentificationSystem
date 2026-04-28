# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the jPro release and unzip it in the build stage to ensure it's successful
RUN mvn clean package jpro:release -DskipTests && \
    mkdir -p /app/release && \
    unzip -qo target/*-jpro.zip -d /app/release

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
# Copy the extracted release directly
COPY --from=build /app/release /app/release

# Install necessary libraries for JavaFX/jPro (including fontconfig)
RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libglu1-mesa \
    libasound2 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libfontconfig1 \
    || apt-get install -y libasound2t64 \
    && rm -rf /var/lib/apt/lists/* || true

EXPOSE 8080

# The startup script will be inside the extracted release
CMD ["sh", "-c", "\
    echo 'Searching for jPro executable in /app/release...'; \
    EXE=$(find /app/release -name 'VehicleIdentificationSystem' -type f -not -name '*.jar' -not -name '*.zip' | head -n 1); \
    if [ -z \"$EXE\" ]; then \
        EXE=$(find /app/release -name 'VehicleIdentificationSystem*' -type f -not -name '*.jar' -not -name '*.zip' | head -n 1); \
    fi; \
    if [ -n \"$EXE\" ]; then \
        chmod +x \"$EXE\"; \
        echo \"Starting application: $EXE\"; \
        exec \"$EXE\"; \
    else \
        echo 'ERROR: Executable not found in /app/release.'; \
        echo 'Directory structure:'; \
        find /app/release -maxdepth 4; \
        exit 1; \
    fi"]
