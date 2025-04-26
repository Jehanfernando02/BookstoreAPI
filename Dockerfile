# Stage 1: Build the WAR file
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

# Stage 2: Deploy to Tomcat
FROM tomcat:9.0-jdk17-temurin
COPY --from=builder /app/target/BookstoreAPI.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]