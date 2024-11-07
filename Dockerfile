FROM gradle:7.6.0-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle
COPY src/main/java /app/src/main/java
COPY src/main/generated-db-entities /app/src/main/generated-db-entities
RUN gradle bootJar -x test --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
COPY src/main/resources /app/resources
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=default,k8s,secrets", "--spring.config.location=file:/app/resources/", "--spring.liquibase.change-log=file:/app/resources/db/changelog/master.yaml"]
