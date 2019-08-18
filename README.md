# SpringBatch demo with Redis

Fun to load a CSV file with SpringBatch 4 into a Redis Database.

The extent of the project is to use Spring Cloud Data Flow with the Spring Batch program.

# Build with maven

```
mvn clean install
```

# Test with maven

It needs 

```
mvn test
```

# run

Run the program on source

```
env CHUNK=1000 mvn spring-boot run input=file:MOCK_DATA.csv
```

# Package then run 

```
mvn clean package
env CHUNK=1000 java -jar ./target/batch-0.0.1-SNAPSHOT.jar input=file:MOCK_DATA.csv
```

## API Batch

A REST API for Spring Batch model.

The sub project is located within 'api'.

```
mvn spring-boot:run
curl -vvv http://localhost:8080/jobs
```