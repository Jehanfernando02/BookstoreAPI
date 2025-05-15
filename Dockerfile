FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -X > mvn_build.log 2>&1 || (cat mvn_build.log && exit 1)
RUN ls -l /app/target/ && test -f /app/target/bookstore-1.0.war || (echo "WAR file not found" && exit 1)

FROM tomcat:9.0-jdk11-openjdk-slim
COPY --from=build /app/target/bookstore-1.0.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
ENV INIT_SAMPLE_DATA=true
CMD ["catalina.sh", "run"]