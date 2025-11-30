# Product Processing API

**Bulk products processing with multiple images**

## Features

- Upload size is large (10K+ products)
- Many users uploading at the same time
- Uploading Progress
- Products and Images stored in File System

## Tech Stack
- Maven
- Spring Boot
- JPA
- Kafka
- Redis
- MySql

## Flow
#### Step 1 - Upload bulk products with multiple images from frontend
Calling single api
```http
  POST /api/v1/products
```
#### Step 2 - Accept the request and respond immediately and start asynchronous process
#### Step 3 - Check uploaded progress in frontend
Calling this api
```http
  GET /api/v1/products/{batchId}/status
```
#### Step 4 - Iterate products and store images in filesystem and send product to Kafka
#### Step 5 - Consume product and check SKU exist in DB then store as json in filesystem and update progress in Redis

## Application Properties

To run this project, you will need to update the following properties to application-dev.properties file

`spring.datasource.url` with database name

`spring.datasource.username`

`spring.datasource.password`

`app.data.storage` to store product's json file and images

#### For redis and kafka
`spring.kafka.bootstrap-servers=localhost:9092`

`redis.host=localhost`

`redis.port=6379`

`redis.database=0`
> *Make sure Redis and Kafka are available on PC*

## Package and Run Project
Go inside the project and run with mvn
```bash
  mvn clean
  mvn package
  mvn spring-boot:run
```
## API Reference

#### Upload Bulk Products

```http
  POST /api/v1/products
```

| Req Body          | Type                   | Description                             |
|:------------------|:-----------------------|:----------------------------------------|
| `products`        | `List<ProductRequest>` | **Required**. <br/>At least one product |
| `products[0].sku` | `String`               | **Required**. <br/>SKU is required      |
| `products[0].name` | `String`               | **Required**. <br/>Name is required     |


#### Get progress status

```http
  GET /api/v1/products/{batchId}/status
```

| Parameter | Type     | Description                                                                      |
| :-------- | :------- |:---------------------------------------------------------------------------------|
| `batchId`      | `string` | **Required**. <br/>valid `batchId` is required to get grogress status from Redis |

#### Get all products

```http
  GET /api/v1/products
```

## Authors

- [@ABsiddik](https://www.github.com/ABsiddik)
- [LinkedIn/ABsiddik](https://www.linkedin.com/in/abu-bakar-siddik-062936b2)