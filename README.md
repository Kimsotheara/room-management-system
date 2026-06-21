# Room Management System

A hotel/room management REST API built with **Spring Boot 3.5**, **Spring Data JPA**, **Spring Security (JWT)** and **PostgreSQL**. It covers the full operational lifecycle of a property: hotels, rooms, room types, guests, reservations (booking → check-in → check-out), payments, in-stay services, promotions, users/roles, and reporting.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Authentication](#authentication)
- [API Conventions](#api-conventions)
- [API Reference](#api-reference)
  - [Authentication](#1-authentication--apiauth)
  - [Users](#2-users--apiusers)
  - [Roles](#3-roles--apiroles)
  - [Hotels](#4-hotels--apihotels)
  - [Room Types](#5-room-types--apiroom-types)
  - [Rooms](#6-rooms--apirooms)
  - [Guests](#7-guests--apiguests)
  - [Reservations](#8-reservations--apireservations)
  - [Payments](#9-payments--apipayments)
  - [Service Usages](#10-service-usages--apiservice-usages)
  - [Services](#11-services--apiservices)
  - [Promotions](#12-promotions--apipromotions)
  - [Promotion Room Types](#13-promotion-room-types--apipromotion-room-types)
  - [Reports](#14-reports--apireports)
- [Error Handling](#error-handling)
- [Enums Reference](#enums-reference)

---

## Tech Stack

| Concern | Technology |
|---------|-----------|
| Language / Runtime | Java 21 |
| Framework | Spring Boot 3.5.0 |
| Persistence | Spring Data JPA, Hibernate 6.6 |
| Database | PostgreSQL |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Mapping | MapStruct 1.6.3 |
| API Docs | springdoc-openapi (Swagger UI) 2.8.6 |
| Build | Gradle (wrapper, Gradle 8.11) |
| Boilerplate | Lombok |

---

## Getting Started

### Prerequisites
- JDK 21
- PostgreSQL running locally (a database named `room_management`)

### Run

```bash
# create the database (one time)
createdb room_management

# start the application
./gradlew bootRun
```

The app starts on **http://localhost:8080**.

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |


---
 
---

## Authentication

The API uses **JWT Bearer tokens**.

1. `POST /api/auth/login` with username/password → returns an `accessToken` and `refreshToken`.
2. Send the access token on every protected request:
   ```
   Authorization: Bearer <accessToken>
   ```
3. When the access token expires, call `POST /api/auth/refresh` with the refresh token to get a new pair.
4. `POST /api/auth/logout` revokes all tokens for the user.

**Public endpoints** (no token required): `login`, `refresh`, `logout`. Every other endpoint requires a valid token, and access is governed per-endpoint by the permission codes attached to the user's roles (see `@AuthResource` annotations in the controllers).

---
## API Reference

> Base URL for all endpoints: `http://localhost:8080`
> All endpoints below (except where noted **Public**) require `Authorization: Bearer <token>`.

---

### 1. Authentication — `/api/auth`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/login` | **Public.** Authenticate with username/password; returns access + refresh tokens, user info and permission codes. |
| POST | `/api/auth/refresh` | **Public.** Exchange a valid refresh token for a fresh access token. |
| POST | `/api/auth/logout` | **Public.** Revoke all tokens for the user identified by the `Authorization` header. |
| GET | `/api/auth/me` | Return the authenticated user's profile, roles and permission codes. |

**Login request**
```json
{
  "username": "admin",
  "password": "secret",
  "deviceId": "optional",
  "deviceName": "optional",
  "deviceType": "optional"
}
```
**Login response (`data`)** — `accessToken`, `refreshToken`, `expiresIn`, `tokenType`, `userId`, `username`, `email`, `fullName`, `isActive`, `permissionCodes`.

**Refresh request:** `{ "refreshToken": "<token>" }`

---

### 2. Users — `/api/users`

Manage application users and their role assignments.

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/users/list/filter` | List users with pagination + optional filter. Filter by `username`, `email`, `fullName`, `phoneNumber`, `isActive`, `roleId` (all optional). |
| GET | `/api/users/{id}` | Get a single user by ID. |
| POST | `/api/users` | Create a user. Body: `username`, `password`, `email`, `fullName`, `phoneNumber`. → `201`. |
| PUT | `/api/users/{id}` | Update a user's details. |
| DELETE | `/api/users/{id}` | Soft delete a user. |
| POST | `/api/users/{id}/roles` | Assign a role to the user. Body: `AssignRoleRequestDto`. |
| DELETE | `/api/users/{id}/roles/{roleId}` | Remove a role from the user. |

---

### 3. Roles — `/api/roles`

CRUD for roles (which carry permission codes).

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/roles` | List all roles. |
| GET | `/api/roles/{id}` | Get a role by ID. |
| POST | `/api/roles` | Create a role. → `201`. |
| PUT | `/api/roles/{id}` | Update a role. |
| DELETE | `/api/roles/{id}` | Soft delete a role. |

---

### 4. Hotels — `/api/hotels`

CRUD for hotel/property records.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/hotels` | List all hotels. |
| GET | `/api/hotels/{id}` | Get a hotel by ID. |
| POST | `/api/hotels` | Create a hotel. → `201`. |
| PUT | `/api/hotels/{id}` | Update a hotel. |
| DELETE | `/api/hotels/{id}` | Soft delete a hotel. |

---

### 5. Room Types — `/api/room-types`

CRUD for room categories (e.g. Standard, Deluxe, Suite) which define base pricing and capacity.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/room-types` | List all room types. |
| GET | `/api/room-types/{id}` | Get a room type by ID. |
| POST | `/api/room-types` | Create a room type. → `201`. |
| PUT | `/api/room-types/{id}` | Update a room type. |
| DELETE | `/api/room-types/{id}` | Soft delete a room type. |

---

### 6. Rooms — `/api/rooms`

CRUD for physical rooms plus image management. Images are stored as Base64 in the database.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/rooms` | List all rooms. |
| GET | `/api/rooms/{id}` | Get a room by ID. |
| POST | `/api/rooms` | Create a room. → `201`. |
| PUT | `/api/rooms/{id}` | Update a room. |
| DELETE | `/api/rooms/{id}` | Soft delete a room. |
| POST | `/api/rooms/{id}/images` | Add one or more images. Body: `{ "images": ["<base64>", ...] }`. → `201`. |
| DELETE | `/api/rooms/{id}/images/{imageId}` | Remove a specific image from a room. |
| GET | `/api/rooms/{id}/images/{imageId}/file` | **Binary.** Stream the decoded image as `image/jpeg`. |

---

### 7. Guests — `/api/guests`

CRUD for guests. The profile image is a Base64 field on the create/update body (no separate upload endpoint).

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/guests` | List all guests. |
| POST | `/api/guests/list/filter` | List guests with pagination + optional filter. Filter by `firstName`, `lastName`, `email`, `phoneNumber`, `nationality`, `identityType`, `isActive`. |
| GET | `/api/guests/{id}` | Get a guest by ID. |
| POST | `/api/guests` | Create a guest (includes `profileImage` as Base64). → `201`. |
| PUT | `/api/guests/{id}` | Update a guest. |
| DELETE | `/api/guests/{id}` | Soft delete a guest. |
| GET | `/api/guests/{id}/profile-image` | **Binary.** Stream the guest's profile image as `image/jpeg`. |

---

### 8. Reservations — `/api/reservations`

The core booking workflow. A reservation books one or more rooms for a guest and tracks its lifecycle through status transitions.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/reservations` | List all reservations. |
| GET | `/api/reservations/{id}` | Get a reservation by ID. |
| POST | `/api/reservations` | Create a reservation. Validates room availability, calculates prices and applies promotions. → `201`. |
| PUT | `/api/reservations/{id}` | Update reservation notes. |
| POST | `/api/reservations/{id}/check-in` | `CONFIRMED → CHECKED_IN`; marks all booked rooms `OCCUPIED`. |
| POST | `/api/reservations/{id}/check-out` | `CHECKED_IN → CHECKED_OUT`; marks all rooms `AVAILABLE`. |
| POST | `/api/reservations/{id}/cancel` | Cancel a `CONFIRMED` or `CHECKED_IN` reservation. |
| GET | `/api/reservations/{id}/invoice` | Generate a full invoice (room charges + service charges + payment summary). |
| DELETE | `/api/reservations/{id}` | Soft delete a reservation. |

**Create request**
```json
{
  "guestId": 1,
  "checkInDate": "2026-07-01T14:00:00",
  "checkOutDate": "2026-07-05T12:00:00",
  "rooms": [
    { "roomId": 10, "promotionId": 3 },
    { "roomId": 11, "promotionId": null }
  ],
  "notes": "Late arrival"
}
```
Each entry in `rooms` is a `roomId` with an optional `promotionId` to apply a discount to that room.

**Status lifecycle:** `CONFIRMED` → `CHECKED_IN` → `CHECKED_OUT`, with `CANCELLED` as a terminal branch.

---

### 9. Payments — `/api/payments`

Record payments against a reservation. Recording payments automatically advances the reservation's `paymentStatus`.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/payments/reservation/{reservationId}` | List all payments for a reservation. |
| GET | `/api/payments/{id}` | Get a payment by ID. |
| POST | `/api/payments` | Record a payment; auto-updates `paymentStatus` (`UNPAID → PARTIAL → PAID`). → `201`. |
| DELETE | `/api/payments/{id}` | Void a payment (soft delete) and subtract its amount from the reservation's paid amount. |

**Add payment request**
```json
{
  "reservationId": 1,
  "paymentMethod": "cash",   // cash | card | transfer
  "paymentType": "deposit",  // deposit | final
  "amount": 150.00
}
```

---

### 10. Service Usages — `/api/service-usages`

Record extra services a guest consumes during their stay (laundry, food, etc.). Each usage adds to the reservation total.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/service-usages/reservation/{reservationId}` | List all service usages for a reservation. |
| GET | `/api/service-usages/{id}` | Get a service usage by ID. |
| POST | `/api/service-usages` | Record a service for a `CHECKED_IN` reservation and add it to the total. → `201`. |
| DELETE | `/api/service-usages/{id}` | Soft delete a usage and subtract its amount from the reservation total. |

**Add service usage request**
```json
{
  "reservationId": 1,
  "serviceId": 5,
  "quantity": 2
}
```

---

### 11. Services — `/api/services`

Catalog of billable services (the menu of things a `service-usage` can reference).

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/services` | List all services. |
| GET | `/api/services/{id}` | Get a service by ID. |
| POST | `/api/services` | Create a service. → `201`. |
| PUT | `/api/services/{id}` | Update a service. |
| DELETE | `/api/services/{id}` | Soft delete a service. |

---

### 12. Promotions — `/api/promotions`

Discount campaigns that can be applied to reservations (per room) through assigned room types.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/promotions` | List all promotions. |
| GET | `/api/promotions/{id}` | Get a promotion by ID. |
| POST | `/api/promotions` | Create a promotion. → `201`. |
| PUT | `/api/promotions/{id}` | Update a promotion. |
| DELETE | `/api/promotions/{id}` | Soft delete a promotion. |

---

### 13. Promotion Room Types — `/api/promotion-room-types`

Link table that defines which room types a promotion applies to.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/promotion-room-types/promotion/{promotionId}` | List the room types assigned to a promotion. |
| GET | `/api/promotion-room-types/{id}` | Get an assignment by its ID. |
| POST | `/api/promotion-room-types` | Assign one or more room types to a promotion (already-assigned types are skipped). → `201`. |
| DELETE | `/api/promotion-room-types/{id}` | Remove a single room-type assignment (soft delete). |

**Assign request**
```json
{
  "promotionId": 3,
  "roomTypeIds": [1, 2, 5]
}
```

---

### 14. Reports — `/api/reports`

Operational dashboards and date-range reports.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/reports/dashboard` | Real-time overview: room status breakdown, occupancy rate, today's check-ins/check-outs, active reservation counts, pending payments, and current-month revenue. |
| GET | `/api/reports/revenue?from=&to=` | Revenue for a check-in date range: total revenue, room vs. service charge split, paid/balance totals, and a per-payment-method summary. |
| GET | `/api/reports/reservations?from=&to=` | Reservation counts grouped by status (`CONFIRMED`, `CHECKED_IN`, `CHECKED_OUT`, `CANCELLED`) for a check-in date range. |

`from` and `to` are ISO dates (`yyyy-MM-dd`). `to` must not be before `from` (otherwise `400`).

**Example**
```
GET /api/reports/revenue?from=2026-06-01&to=2026-06-30
```

---

## Error Handling

All errors are returned in the standard `ApiResponse` envelope with `success: false`. Handled by a global exception handler:

| Exception | HTTP Status | Notes |
|-----------|-------------|-------|
| `AuthenticationException` | `401 Unauthorized` | Invalid credentials / token |
| `ResourceNotFoundException` | `404 Not Found` | Entity missing |
| `DuplicateResourceException` | `409 Conflict` | Unique constraint violated |
| `MethodArgumentNotValidException` | `400 Bad Request` | Bean-validation failure; `data` contains a field→message map |
| `IllegalArgumentException` | `400 Bad Request` | Bad input (e.g. `to` before `from`) |
| `Exception` (fallback) | `500 Internal Server Error` | Unexpected error |

**Validation error example**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": { "username": "Username is required" },
  "status": 400,
  "timestamp": "2026-06-21 14:03:49"
}
```

---

## Enums Reference

| Enum | Values |
|------|--------|
| `RoomStatus` | `AVAILABLE`, `RESERVED`, `OCCUPIED`, `CLEANING`, `MAINTENANCE` |
| `ReservationStatus` | `CONFIRMED`, `CHECKED_IN`, `CHECKED_OUT`, `CANCELLED` |
| `PaymentStatus` | `UNPAID`, `PARTIAL`, `PAID` |
