# Distributed transaction sample
This project serves as a playground to explore solutions to distributed transactions in microservice.
Code snippets are used in my thesis for the master's degree.

## Requirements
- Docker
- Docker Compose
- Maven

## Setup
- After clone `cd` into repository directory then `mvn clean package`
- Then use `docker-compose up -d` to run services as containers in their own network
- Important addresses of containers:
  - Erueka dashboard -> `localhost:8761`
  - Order-service interactive api-doc -> `localhost:8080/swagger-ui.html`
  - Customer-service interactive api-doc -> `localhost:8081/swagger-ui.html`
  - Storage-service interactive api-doc -> `localhost:8082/swagger-ui.html`
- Order-service has endpoints for the transaction cases
- After every service is up and running, it takes a while for the load-balancing to work, until that happens 500 errors are expected
- Check container logs with `docker logs container-name`