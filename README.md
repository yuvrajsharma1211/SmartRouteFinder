# SmartRouteFinder

Simple Spring Boot web application that serves a static front-end and provides routing endpoints.

## CI/CD Pipeline

This project includes a GitHub Actions workflow at [.github/workflows/ci-cd.yml](.github/workflows/ci-cd.yml).

The pipeline does the following:

- Checks out the code on every push and pull request to `main`.
- Sets up Java 11 with Maven caching.
- Runs the test suite with `mvn test`.
- Builds the application package with `mvn -DskipTests package`.
- Builds the Docker image with `docker build -t smart-route-finder:ci .`.

The workflow is meant to catch build or test issues before deployment.

## Docker Container

The app is containerized with [Dockerfile](Dockerfile) and [docker-compose.yml](docker-compose.yml).

Use Docker Compose to build and run the container locally:

```powershell
docker compose up --build
```

To run it in the background:

```powershell
docker compose up --build -d
```

The application will be available at:

- http://localhost:8081/

## Prerequisites

- Java JDK 11 installed and `JAVA_HOME` set.
- Apache Maven installed and on your `PATH`.

Verify with:

```powershell
java -version
mvn -v
```

## Build

From the project root run:

```powershell
mvn clean package
```

This produces a runnable JAR in the `target/` directory named `smart-route-finder-1.0.0.jar`.

## Run (recommended)

Run the Spring Boot application using the Spring Boot Maven plugin (development):

```powershell
mvn spring-boot:run
```

Or run the packaged JAR directly:

```powershell
java -jar target/smart-route-finder-1.0.0.jar
```

The application listens on port `8081` by default. Open your browser to:

- http://localhost:8081/ — serves the static `index.html` located in `src/main/resources/static`.

If you prefer Docker Compose, use the commands in the Docker Container section above.

## Run from an IDE

Import the project as a Maven project (IntelliJ IDEA, Eclipse, VS Code). Run the `main` method in:

- `src/main/java/com/routefinder/SmartRouteFinderApplication.java`

## Troubleshooting

- If the build fails, run `mvn -e clean package` to see the full error trace.
- If port `8080` is in use, set a different port by creating `src/main/resources/application.properties` with:

```properties
server.port=8081
```

- Ensure `JAVA_HOME` points to a Java 11 JDK installation.

## Notes

- This project uses Spring Boot 2.7.x and `spring-boot-starter-web`.
- Artifact produced: `smart-route-finder-1.0.0.jar` (see `pom.xml`).


