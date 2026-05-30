# SmartRouteFinder

Simple Spring Boot web application that serves a static front-end and provides routing endpoints.

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

If you want, I can add a simple `mvnw` wrapper and a small `README` badge. Would you like that?
