# Price Service API

This module (`price-service-api`) contains the **OpenAPI contract** for the Price Service.  
It documents the authentication flow and the public price retrieval endpoint that clients can consume.

---

## Responsibilities

- Defines endpoints in the OpenAPI 3.1 specification (`products.yaml`).
- Describes request/response models (DTOs) for authentication and price retrieval.
- Specifies security scheme (**JWT Bearer**) and which endpoints require it.
- Provides a stable contract for client generation and server implementation.

---

## Key Features

- **OpenAPI 3.1** contract-first design.
- **JWT Authentication** via `bearerAuth`; all endpoints require JWT **except** `/api/v1/prices` (public for testing 
  in the local, dev and performance spring profile).
- Clear request/response schemas: `LoginRequest`, `JwtResponse`, and `PriceResponse`.
- Server base URL in spec: `/inditex`.

---

## Endpoints

### **Login**
`POST /api/v1/auth/login`  
Authenticates a user and returns a signed JWT token.

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

**Usage:**  
Include the token on subsequent requests:
```
Authorization: Bearer <token>
```

---

### **Get Price**
`GET /api/v1/prices`  
Returns the applicable price for a product at a given date/time.

**Query Parameters:**
- `brandId` *(required, integer)*
- `productId` *(required, int64)*
- `date` *(required, ISO-8601 date-time in UTC)* — e.g. `2020-06-14T16:00:00Z`
- `currency` *(optional, string, default: `EUR`)*

**Example Request:**
```
GET /inditex/api/v1/prices?brandId=1&productId=35455&date=2020-06-14T16:00:00Z&currency=EUR
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

> Note: `price` is represented as a **string** in the schema to preserve decimal scale.

---

## Validation Rules

- `brandId`, `productId`, and `date` are **required** for `/api/v1/prices`.
- `date` must be a valid **ISO-8601** UTC timestamp.
- `currency` defaults to `EUR` if omitted.
- Authentication:
    - JWT is **required** for all endpoints **except** `/api/v1/prices`.

---

## Running the Module

This is an **API contract** module. Common workflows:

- **View the API**: Import `products.yaml` into Swagger UI, ReDoc, or Postman.
- **Generate clients/servers**:
  ```bash
  # Example with OpenAPI Generator (adjust languages as needed)
  openapi-generator generate -i products.yaml -g spring -o generated-server
  ```
- **Build** (if part of a Maven multi-module project):
  ```bash
  mvn -q clean install
  ```

---

## Security Notes

- Security scheme: `bearerAuth` (HTTP bearer, **JWT**).
- Public endpoint: `GET /api/v1/prices`.
- All other endpoints should be called with:
  ```
  Authorization: Bearer <token>
  ```

---

## Postman / Tooling

- Import **`products.yaml`** directly into Postman/Insomnia to test:
    - `POST /api/v1/auth/login`
    - `GET /api/v1/prices`

---

## Future Enhancements

- Add endpoints for price catalogs, product metadata, and brand discovery.
- Pagination and filtering for historical price ranges.
- Error model unification and extended problem details.
