## Feature http-client documentation

- [Micronaut Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)

## Feature kafka documentation

- [Micronaut Kafka Messaging documentation](https://micronaut-projects.github.io/micronaut-kafka/latest/guide/index.html)

This project was created as a study case of a order management system based on micronaut framework and kafka topics

The project order-processor-air is related to this project.
```
curl --location --request POST 'localhost:8080/api/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
    "products" : ["AIR","HOT"],
    "air": [{"iata":"AZUL"}],
    "hotel": [{"name":"Ibis"}],
    "car": [{"name":"Gol"}]
}'
```