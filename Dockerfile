# Use a base image with Java 8
FROM openjdk:8-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the Maven-built JAR file
COPY target/bookstore-1.0.jar app.jar

# Expose port 8080 (or PORT environment variable)
EXPOSE 8080

# Set environment variables for memory and port
ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENV PORT=8080

# Run the JAR file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]