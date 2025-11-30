# Product Process

**Bulk products processing with multiple images**

## Application Structure
- **Backend:** [product-processing-api](https://github.com/ABsiddik/product-process/tree/main/product-processing-api)
- **Frontend:** [product-app](https://github.com/ABsiddik/product-process/tree/main/product-app)
- docker-compose.yml

## Tech Stack
**Frontend:** Next.js, TailwindCSS

**Backend:** Spring Boot, JPA, Redis, Kafka

**Database:** MySql

## Docker Compose
*If Docker is available on local PC, go inside project and run following command*
```bash
  docker compose up -d
```
#### Configured images
- mysql:3306
- redis:6379
- zookeeper:2181
- kafka:9092
- backend:8080 *API*
- frontend:3000

## Authors

- [@ABsiddik](https://www.github.com/ABsiddik)
- [LinkedIn/ABsiddik](https://www.linkedin.com/in/abu-bakar-siddik-062936b2)