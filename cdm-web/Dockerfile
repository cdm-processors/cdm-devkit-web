FROM gradle:8.14.0-jdk17 AS build
RUN umask 0000
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build -x test --no-daemon 

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/cdm-web-0.0.1-SNAPSHOT.jar cdm-web-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "cdm-web-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
EXPOSE 2375
