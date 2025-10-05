# syntax=docker/dockerfile:1

# ---- Build stage: use Gradle wrapper to produce the JAR ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy gradle files & wrapper first for better layer caching
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN chmod +x gradlew

# Copy source and build
COPY src ./src
RUN ./gradlew clean bootJar --no-daemon

# ---- Run stage: slim JRE image to run the JAR ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar (wildcard handles -SNAPSHOT names)
COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
