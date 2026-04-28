# Build stage — Java 21 to match jPro 2025.3.3 / Helidon 4.x requirements
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN apt-get update && apt-get install -y unzip && \
    mvn clean package jpro:release -DskipTests && \
    mkdir -p /app/release && \
    unzip -qo target/*-jpro.zip -d /app/release

# Run stage — must also be Java 21
FROM eclipse-temurin:21-jdk
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
    && rm -rf /var/lib/apt/lists/*

RUN chmod +x /app/release/VehicleIdentificationSystem-jpro/bin/start.sh

EXPOSE 8080

CMD ["/app/release/VehicleIdentificationSystem-jpro/bin/start.sh"]