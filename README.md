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

## SpringCloud Data Flow


Based on : https://dataflow.spring.io/docs/installation/local/docker/

```
wget https://raw.githubusercontent.com/spring-cloud/spring-cloud-dataflow/v2.2.0.RELEASE/spring-cloud-dataflow-server/docker-compose.yml
env DATAFLOW_VERSION=2.2.0.RELEASE SKIPPER_VERSION=2.1.0.RELEASE COMPOSE_HTTP_TIMEOUT=200 docker-compose up -d
```

Then open you navigator here : http://localhost:9393/dashboard/index.html

You can also launch the SpringCloudDataFlow Shell :

```
docker exec -it dataflow-server java -jar shell.jar
```
