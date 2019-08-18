# SpringBoot REST and GraphQL API for SpringBatch db

You love SpringBatch but you can't efford to run SQL queries all the time ?

Let's introduce this project that expose SpringBatch database (SQL) with a REST and a GraphQL API for 

Enjoy !

## How it works

The server is a :
- a JPA app connected to SpringBzatch db
- a REST API based on JPA Entities and RestControllers with HATEOAS
- a graphQL server 

The server

## Requests

### Request the REST server 

With curl :

```
curl -vvv http://localhost:8080/jobs
```

With httpie :

```
http http://localhost:8080/jobs
```

### Request the graphQL server

```
curl 'http://127.0.0.1:8080/graphql' \
-H 'Content-Type: application/json' \
--data-binary '{"query":"query getAllJobs {\n  findAllJobs { id version status startTime endTime exitCode exitMessage\n  }\n}\n","variables":null,"operationName":"getAllJobs"}'
```

## References 

- https://github.com/graphql-java-kickstart/graphql-spring-boot