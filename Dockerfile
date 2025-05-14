FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -X > mvn_build.log 2>&1 || (cat mvn_build.log && exit 1)
RUN ls -l /app/target/ && test -f /app/target/bookstore-1.0.war || (echo "WAR file not found" && exit 1)

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/bookstore-1.0.war /app/bookstore-1.0.war
# Unpack the WAR to access Main.class and dependencies
RUN mkdir /app/webapp && \
    unzip /app/bookstore-1.0.war -d /app/webapp && \
    rm /app/bookstore-1.0.war
EXPOSE 8080
ENV INIT_SAMPLE_DATA=true
# Run Main class with classpath including WEB-INF/lib
CMD ["java", "-cp", "webapp/WEB-INF/classes:webapp/WEB-INF/lib/*", "com.bookstore.Main"]