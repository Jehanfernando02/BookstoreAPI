FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/bookstore-1.0.war /app/bookstore-1.0.war
EXPOSE 8080
ENV INIT_SAMPLE_DATA=true
CMD ["java", "-jar", "/app/bookstore-1.0.war"]