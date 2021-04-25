# Task Maintaining Helper app

## Onboarding Process

### Prerequisite

* 11 JDK
* Docker
* Gradle

#### Installation

Go to the root directory and run from shell:

```
$ docker-compose up
$ ./gradlew clean build jar
$ java -jar ./build/libs/erply-demo-0.0.1-SNAPSHOT.jar
```

#### Guides

* PostgreSql database is available at port 5432
* Swagger documentation is available at [this local page](http://localhost:8080/swagger-ui.html)

## Running tests

Run `./gradlew clean  test`

## Running integration tests

Run `./gradlew clean itest`


## Running against local SonarQube

If you want to see what the project quality state is, run a SonarQube check with a local SonarQube instance:

1. Start SonarQube: `docker run -p 9000:9000 -d --rm sonarqube`
2. Run `./gradlew clean build test`
3. Run `./gradlew local sonarqube`
3. Open `http://localhost:9000` and see the results
