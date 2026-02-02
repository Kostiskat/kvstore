FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /app


COPY pom.xml .
COPY src ./src


RUN mvn clean package -DskipTests


FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=builder /app/target/kvstore-server.jar kvstore-srv.jar

ENV PORT=6379
EXPOSE 6379

CMD ["java", "-jar", "kvstore-srv.jar"]