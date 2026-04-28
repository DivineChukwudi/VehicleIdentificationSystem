# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the jPro release - we use package to ensure all artifacts are built
RUN mvn clean package jpro:release -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
# Copy the entire target directory to find the release wherever it is
COPY --from=build /app/target /app/target

# Install necessary libraries for JavaFX/jPro and unzip for potential zip releases
RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libglu1-mesa \
    libasound2 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    unzip \
    || apt-get install -y libasound2t64 \
    && rm -rf /var/lib/apt/lists/* || true

EXPOSE 8080

# The startup script might be in target/jpro/bin/ or target/jpro-release/bin/ or inside a zip
# We use a robust script to find it, unzip if necessary, and execute
CMD ["sh", "-c", "\
    echo 'Searching for jPro executable...'; \
    ZIP=$(find /app/target -name '*jpro.zip' | head -n 1); \
    if [ -n \"$ZIP\" ]; then \
        echo \"Found release zip: $ZIP. Extracting...\"; \
        mkdir -p /app/release; \
        unzip -qo \"$ZIP\" -d /app/release; \
    fi; \
    # Search for the executable in extracted release or target folder \
    SEARCH_PATH=\"/app/target\"; \
    if [ -d /app/release ]; then SEARCH_PATH=\"/app/release /app/target\"; fi; \
    EXE=$(find $SEARCH_PATH -name 'VehicleIdentificationSystem' -type f -not -name '*.jar' -not -name '*.zip' | head -n 1); \
    if [ -z \"$EXE\" ]; then \
        EXE=$(find $SEARCH_PATH -name 'VehicleIdentificationSystem*' -type f -not -name '*.jar' -not -name '*.zip' | head -n 1); \
    fi; \
    if [ -n \"$EXE\" ]; then \
        chmod +x \"$EXE\"; \
        echo \"Starting application: $EXE\"; \
        exec \"$EXE\"; \
    else \
        echo 'ERROR: Executable not found.'; \
        echo 'Directory structure of /app/target:'; \
        find /app/target -maxdepth 3; \
        if [ -d /app/release ]; then \
            echo 'Directory structure of /app/release:'; \
            find /app/release -maxdepth 4; \
        fi; \
        exit 1; \
    fi"]
