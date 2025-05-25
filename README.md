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
curl "http://localhost:8080/next-tracking-number?origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2018-11-20T19%3A29%3A32%2B08%3A00&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics"
```

## 🛑 Stop the application
```bash
docker-compose down
```
