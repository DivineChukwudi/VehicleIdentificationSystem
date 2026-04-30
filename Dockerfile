# Build stage — Java 23 
 FROM maven:3.9.9-eclipse-temurin-23 AS build 
 WORKDIR /app 
 COPY pom.xml . 
 COPY src ./src 
 
 RUN apt-get update && apt-get install -y unzip && \ 
     mvn clean package jpro:release -DskipTests && \ 
     mkdir -p /app/release && \ 
     unzip -qo target/*-jpro.zip -d /app/release 
 
 # Run stage — Java 23 
 FROM eclipse-temurin:23-jdk 
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
 
 # Print env var presence (not values) at startup for debugging 
 CMD echo "DB_URL set: $([ -n \"$DB_URL\" ] && echo YES || echo NO)" && \ 
     echo "DB_USER set: $([ -n \"$DB_USER\" ] && echo YES || echo NO)" && \ 
     echo "DB_PASS set: $([ -n \"$DB_PASS\" ] && echo YES || echo NO)" && \ 
     exec /app/release/VehicleIdentificationSystem-jpro/bin/start.sh 
