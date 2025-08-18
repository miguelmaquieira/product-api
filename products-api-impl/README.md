# Price Service API Implementation

This module (`products-api-impl`) contains the **business logic**, **controllers**, **validation**, and **service layer** for the Price Service API.  
It implements the OpenAPI-generated interfaces from the `products-api` module.

---

## Responsibilities

- Implements endpoints defined in the OpenAPI specification (`products.yaml`).
- Contains service layer and mapping logic for retrieving applicable prices.
- Validates request parameters (`brandId`, `productId`, `date`, `currency`).
- Configures security (JWT authentication) — with `/api/v1/prices` left public as per spec.

---

## Key Features

- **Spring Boot 3** backend using Java 17+.
- **Validation** utilities for query parameters.
- **JWT Authentication** with login endpoint (`/api/v1/auth/login`).
- Public price retrieval endpoint (`/api/v1/prices`).

---

## Endpoints

### **Login**
`POST /api/v1/auth/login`  
Authenticates a user and returns a JWT token.

**Example Request:**
```json
{
  "email": "user@example.com",
  "password": "secret"
}
```

**Example Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

### **Get Price**
`GET /api/v1/prices`  
Retrieves the applicable price for a product at a given date/time.

**Query Parameters:**
- `brandId` *(required, integer)*
- `productId` *(required, integer)*
- `date` *(required, ISO-8601 UTC)*
- `currency` *(optional, default `EUR`)*

**Example Request:**
```
GET /api/v1/prices?brandId=1&productId=35455&date=2020-06-14T16:00:00Z&currency=EUR
```

**Example Response:**
```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2025-01-01T00:00:00Z",
  "endDate": "2025-12-31T23:59:59Z",
  "price": "35.50",
  "currency": "EUR"
}
```

---

## Validation Rules

- `brandId`, `productId`, and `date` are **required**.
- `date` must be a valid **ISO-8601 UTC** timestamp.
- `currency` defaults to `EUR` if not provided.
- Invalid or missing parameters return **400 Bad Request**.
- If no price is found, returns **404 Not Found**.

---

## Running the Module

### Run via Maven
```bash
mvn spring-boot:run -pl products-api-impl -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Dlogging.level.com.mgm.inditex=DEBUG -Dlogging.level.org.springframework.security=DEBUG
"
```

### Run via docker
```bash
docker build -t product-api--app .
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e LOGGING_LEVEL=DEBUG \
  product-api-app
```

### Run via IDE
- Import the multi-module project in your IDE.
- Run `ProductRateApplication` main class.

Backend will be available at:  
`http://localhost:8080/inditex`

---

## Security Notes

- JWT authentication is **enabled** by default.
- `/api/v1/prices` is public (no JWT required).
- To call secured endpoints, include:
  ```
  Authorization: Bearer <token>
  ```

---

## Postman Collection

Import `Price Service API — Login & Prices.postman_collection.json` into Postman to test:
- Login
- Price retrieval (valid and negative scenarios)
- Actuator

---

## Future Enhancements

- Add CRUD endpoints for brands, products, and price lists.
- Add integration with external identity provider for JWT validation.
- Extend error model with standardized problem details.
- Complete integration and performance tests.
- Run `staging` and `performance` profiles with postgres instead of H2
- Deploy application to AWS
