# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN apt-get update && apt-get install -y unzip && \
    mvn clean package jpro:release -DskipTests && \
    mkdir -p /app/release && \
    unzip -qo target/*-jpro.zip -d /app/release

# Debug: show what was extracted
RUN echo "=== Release contents ===" && find /app/release -maxdepth 5 && \
    echo "=== Shell scripts ===" && find /app/release -name "*.sh" && \
    echo "=== Files without extension ===" && find /app/release -type f ! -name "*.*"

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/release /app/release

RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libglu1-mesa \
    libasound2 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libfontconfig1 \
    && rm -rf /var/lib/apt/lists/* || true

EXPOSE 8080

CMD ["sh", "-c", "\
    echo '=== Searching for startup script ===' && \
    find /app/release -maxdepth 5 -type f | sort && \
    \
    EXE=$(find /app/release -maxdepth 4 -name 'VehicleIdentificationSystem' -type f | head -n 1); \
    \
    if [ -z \"$EXE\" ]; then \
        echo 'Exact name not found, trying shell scripts...'; \
        EXE=$(find /app/release -maxdepth 4 -name '*.sh' -type f | head -n 1); \
    fi; \
    \
    if [ -z \"$EXE\" ]; then \
        echo 'No .sh found, trying any non-jar non-zip file in bin/...'; \
        EXE=$(find /app/release -maxdepth 5 -type f \
              ! -name '*.jar' ! -name '*.zip' ! -name '*.class' \
              ! -name '*.properties' ! -name '*.xml' ! -name '*.css' \
              ! -name '*.fxml' ! -name '*.png' ! -name '*.ico' \
              | head -n 1); \
    fi; \
    \
    if [ -n \"$EXE\" ]; then \
        chmod +x \"$EXE\"; \
        echo \"Starting: $EXE\"; \
        exec \"$EXE\"; \
    else \
        echo 'ERROR: No executable found. Full tree:'; \
        find /app/release -maxdepth 6; \
        exit 1; \
    fi"]