# Stage 1: Build
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /app

# 1. Copy the Parent POM
COPY pom.xml .

# 2. Copy the entire project structure
# This ensures all sub-folders (sdk, server, client) are present
COPY . .

# 3. Build the entire project
# We use 'install' so the server can find the sdk artifact
RUN mvn clean install -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre
WORKDIR /app

# 4. Copy the JAR from the server module's target
# Ensure the path matches: module_folder/target/jar_name
COPY --from=builder /app/server/target/kvstore-server.jar kvstore-srv.jar

ENV PORT=6379
EXPOSE 6379

# Using ZGC for high throughput
CMD ["java", "-XX:+UseZGC", "-XX:+ZGenerational", "-jar", "kvstore-srv.jar"]