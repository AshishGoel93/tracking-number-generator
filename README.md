# 🚚 Tracking Number Generator API

A Spring Boot WebFlux-based API for generating unique tracking numbers with Redis and PostgreSQL.

## 🚀 Quick Start with Docker

1. **Clone the repository**
```bash
git clone https://github.com/your-org/tracking-number-generator.git
cd tracking-number-generator
```

2. **Start the application**
```bash
docker-compose up --build
```

This will spin up:
- Spring Boot WebFlux app at `http://localhost:8080`
- Redis at `localhost:6379`
- PostgreSQL at `localhost:5432`

3. **Generate a tracking number**
```bash
curl "http://localhost:8080/api/v1/tracking/generate?origin_country_id=US&destination_country_id=IN&customer_slug=amazon"
```

## 🛑 Stop the application
```bash
docker-compose down
```
