# Price Service Platform

Price Service is a backend platform to manage **product prices** with date range and priority rules.  
It is built with **Spring Boot** using an **OpenAPI-first** multi-module architecture.

---

## Project Structure

This repository is structured as a Maven multi-module project:

- **products-api**  
  OpenAPI specification (`products.yaml`), models, and contracts for authentication and price retrieval.

- **products-api-impl**  
  Business logic, controllers, validation, and service implementations for the API.

---

## Key Features

- **OpenAPI 3.1** API-first development (auto-generated interfaces and models).
- **Spring Boot 3 / Java 17+** backend with modular separation of concerns.
- **JWT Authentication** with login endpoint (`/api/v1/auth/login`).
- **Price Retrieval** endpoint (`/api/v1/prices`) with priority/date rules.
- **Validation** for query parameters (`brandId`, `productId`, `date`, `currency`).

---

## Requirements

- **Java** 21 or later
- **Maven** 3.9+
- **Postman** (optional, for API testing)

---

## Getting Started

### 1. Clone the Repository
```bash
git clone <repo-url>
cd price-service
```

### 2. Build All Modules
```bash
mvn clean install
```

### 3. Run Backend (API Implementation)
```bash
cd products-api
mvn spring-boot:run -pl products-api-impl -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Dlogging.level.com.mgm.inditex=DEBUG -Dlogging.level.org.springframework.security=DEBUG

```
The backend will start at `http://localhost:8080/inditex`.

---

## API Overview

### Base Path
```
http://localhost:8080/inditex/api/v1
```

### Endpoints
- `POST /auth/login` – User authentication (returns JWT)
- `GET /prices` – Retrieve the applicable price for a product at a given date/time

---

## Postman Collection

A Postman collection is available in the repository to test:
- Login
- Price retrieval (valid and invalid scenarios)

Import `Price Service API — Login & Prices.postman_collection.json` into Postman.

---

## Development Notes

- `/api/v1/prices` is **public** (no JWT required) by design.
- All other endpoints require a valid JWT (`Authorization: Bearer <token>`).
- Price calculation rules:
  - **Priority**: if multiple records overlap, the one with the highest `priority` wins.
  - **Date Range**: price is only valid between its `startDate` and `endDate`.

---

## Future Improvements

- Add CRUD endpoints for brands, products, and price lists.
- Re-enable full JWT validation against external identity providers (e.g., Keycloak).
- Add integration, performance, and end-to-end tests.
- Provide Docker image and CI/CD pipeline support.

---

## License

This project is for demonstration and educational purposes.  
