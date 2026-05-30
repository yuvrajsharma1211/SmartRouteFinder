FROM maven:3.9.9-eclipse-temurin-11 AS build
WORKDIR /app

# Copy the Maven descriptor first to maximize layer caching.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package \
    && JAR_FILE=$(find target -maxdepth 1 -name '*.jar' ! -name '*.original' | head -n 1) \
    && cp "$JAR_FILE" target/app.jar

FROM eclipse-temurin:11-jre-jammy AS runtime
WORKDIR /app

RUN useradd --create-home --uid 10001 appuser

COPY --from=build /app/target/app.jar /app/app.jar

EXPOSE 8081

USER appuser
ENTRYPOINT ["java", "-jar", "/app/app.jar"]