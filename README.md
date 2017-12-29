## Event Service
Simple Spring Boot RESTful service for adding and retrieving events.  An event has a type, value, time, and UUID.

### Build
```bash
./gradlew build
```

### Run
```bash
java -jar event_service-<version>.jar
```

### Overriding default configuration options
Add application.yml with overrides to same directory as the jar file.

### Build javadoc ###
    ./gradlew javadoc
    ./gradlew javadocJar # builds a javadoc jar for each module

### Run unit/integration tests ###
    ./gradlew test # runs both unit and integration tests
    
### License

Apache License, Version 2.0
