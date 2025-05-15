# Stage 1: Build the JAR with Maven
FROM maven:3.8.6-openjdk-8 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/bookstore-1.0.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENV PORT=8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]