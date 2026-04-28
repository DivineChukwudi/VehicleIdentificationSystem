# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN apt-get update && apt-get install -y unzip && \
    mvn clean package jpro:release -DskipTests && \
    mkdir -p /app/release && \
    unzip -qo target/*-jpro.zip -d /app/release

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

RUN chmod +x /app/release/VehicleIdentificationSystem-jpro/bin/start.sh

EXPOSE 8080

CMD ["/app/release/VehicleIdentificationSystem-jpro/bin/start.sh"]