# build stage
FROM gradle:8.8-jdk17 AS build
WORKDIR /src
COPY . .
RUN gradle --no-daemon clean bootJar

# runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/build/libs/*.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","/app/app.jar"]