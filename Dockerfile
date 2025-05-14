# Build stage
FROM maven:3.8.6-openjdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/target/BookstoreAPI.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]