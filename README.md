# Kafka Integration Shop (Simple Academic Example)

This is a very simple example of communication and integration using Apache Kafka Streams.
In this example the Apache Kafka will be running in a Docker Container

## Installation

First you need to install [Docker](https://www.docker.com/)
You can set up your own container with Kafka, but for this example it's easier to use one of the free available online. 

Get the Container:
```bash
docker pull spotify/kafka
```

## Start Kafka Streaming Service
Run the Container, starting hte Kafka Service
```bash
docker run -d -p 2181:2181 -p 9092:9092 --env ADVERTISED_HOST=127.0.0.1 --env ADVERTISED_PORT=9092 --hostname kafka --name kafka spotify/kafka
```

## Check the Configuration
Check the persistence.xml file configuration and adapt to your database.

## Start the Services
### Start The Shop
```bash
./shop_start.sh
```

### Start The Supplier
```bash
./supplier_start.sh
```

### Start The Customers
```bash
./customer_auto1_start.sh
./customer_auto2_start.sh
```

### Start The REST Server
```bash
./rest_server_start.sh
```

### Owner Orders Products
```bash
java -cp target/project3.jar is.project3.core.owner.Owner [ProductName] [ProductAmount]
```


### Get Shipment Details (REST Client)
```bash
./shipment_details.sh
```


## Contributing
Pull requests are welcome.

## License
[MIT](https://choosealicense.com/licenses/mit/)