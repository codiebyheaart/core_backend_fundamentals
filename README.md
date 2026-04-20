# 🏦 KYC Profile Service — Spring Boot

> **Minimal KYC (Know Your Customer) Profile API**  
> Built as a 1-hour baseline assessment for banking/fintech backend fundamentals.

---

## 📋 Table of Contents

1. [High-Level Requirements](#high-level-requirements)
2. [Architecture & Design](#architecture--design)
3. [Project Structure](#project-structure)
4. [Tech Stack](#tech-stack)
5. [How to Run](#how-to-run)
6. [API Reference with cURL Examples & Responses](#api-reference-with-curl-examples--responses)
7. [Error Scenarios](#error-scenarios)
8. [H2 Console — View Database](#h2-console--view-database)
9. [Running Unit Tests](#running-unit-tests)
10. [Demo Guide for Presenters](#demo-guide-for-presenters)

---

## High-Level Requirements

| # | Requirement | Status |
|---|-------------|--------|
| 1 | `POST /api/kyc` — Create a KYC profile | ✅ Done |
| 2 | `GET /api/kyc/{customerId}` — Retrieve a KYC profile | ✅ Done |
| 3 | `customerId` must be unique across all records | ✅ Done |
| 4 | Default `kycStatus` = `PENDING` on every creation | ✅ Done |
| 5 | Return clear error if customer not found | ✅ Done |
| 6 | Return conflict error if customerId already exists | ✅ Done |
| 7 | Bean validation on all required fields | ✅ Done |
| 8 | Layered architecture: Controller → Service → Repository | ✅ Done |
| 9 | No business logic in the controller | ✅ Done |
| 10 | Predictable, structured error responses | ✅ Done |
| 11 | KycStatus enum: `PENDING`, `VERIFIED`, `EXPIRED` | ✅ Done |
| 12 | Audit timestamp (`createdAt`) set on persist, immutable | ✅ Done |
| 13 | Unit tests for service logic (JUnit 5 + Mockito) | ✅ Done |
| 14 | H2 in-memory database (no external DB setup needed) | ✅ Done |

---

## Architecture & Design

```
┌─────────────────────────────────────────────────────────┐
│                    HTTP Client (cURL / Postman)          │
└─────────────────────────┬───────────────────────────────┘
                          │ HTTP Request
                          ▼
┌─────────────────────────────────────────────────────────┐
│              KycProfileController  (REST Layer)          │
│  - Accepts @Valid request bodies                        │
│  - Delegates ALL logic to service                       │
│  - Returns correct HTTP status codes                    │
└─────────────────────────┬───────────────────────────────┘
                          │ Method call
                          ▼
┌─────────────────────────────────────────────────────────┐
│            KycProfileServiceImpl  (Business Layer)       │
│  - Duplicate customerId guard                           │
│  - Sets default KycStatus = PENDING                     │
│  - @Transactional boundaries                            │
│  - Throws domain exceptions on errors                   │
└─────────────────────────┬───────────────────────────────┘
                          │ JPA
                          ▼
┌─────────────────────────────────────────────────────────┐
│            KycProfileRepository  (Data Layer)            │
│  - Spring Data JPA (extends JpaRepository)              │
│  - findByCustomerId / existsByCustomerId                │
└─────────────────────────┬───────────────────────────────┘
                          │ SQL
                          ▼
                   H2 In-Memory DB
                   (kyc_profiles table)

Error path:
┌────────────────────────────────────────────────────────┐
│            GlobalExceptionHandler  (@RestControllerAdvice)
│  - CustomerNotFoundException     → 404 Not Found       │
│  - CustomerAlreadyExistsException→ 409 Conflict        │
│  - MethodArgumentNotValidException→ 400 Bad Request    │
│  - Generic Exception              → 500 Internal Error │
└────────────────────────────────────────────────────────┘
```

### Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| **Interface + Impl** for Service | Decouples controller from implementation; easy to mock in tests |
| **@Builder** on Entity | Immutability-first; no accidental field mutation |
| **@PrePersist** for defaults | DB-level defaults are fragile; Java-level is explicit and testable |
| **`updatable = false`** on createdAt | Audit integrity — timestamps must never change |
| **`existsByCustomerId`** before save | Cleaner than catching DataIntegrityViolationException |
| **`KycProfileResponse.from(entity)`** | Keeps entity fields off the wire; separates persistence from API contract |

---

## Project Structure

```
core_backend_fundamentals/
├── pom.xml
├── README.md
├── requirement.md
└── src/
    ├── main/
    │   ├── java/com/fintech/kyc/
    │   │   ├── KycProfileApplication.java          ← Entry point
    │   │   ├── controller/
    │   │   │   └── KycProfileController.java       ← REST endpoints
    │   │   ├── service/
    │   │   │   ├── KycProfileService.java          ← Interface (contract)
    │   │   │   └── KycProfileServiceImpl.java      ← Business logic
    │   │   ├── repository/
    │   │   │   └── KycProfileRepository.java       ← Data access
    │   │   ├── entity/
    │   │   │   └── KycProfile.java                 ← JPA entity
    │   │   ├── dto/
    │   │   │   ├── KycProfileRequest.java          ← Inbound payload
    │   │   │   ├── KycProfileResponse.java         ← Outbound payload
    │   │   │   └── ErrorResponse.java              ← Error envelope
    │   │   ├── enums/
    │   │   │   └── KycStatus.java                  ← PENDING/VERIFIED/EXPIRED
    │   │   └── exception/
    │   │       ├── CustomerNotFoundException.java
    │   │       ├── CustomerAlreadyExistsException.java
    │   │       └── GlobalExceptionHandler.java     ← Centralised error mapping
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/fintech/kyc/service/
            └── KycProfileServiceImplTest.java      ← Unit tests (JUnit 5 + Mockito)
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.4 |
| Web | Spring Web (REST) |
| Persistence | Spring Data JPA + Hibernate |
| Database | H2 (in-memory) |
| Validation | Jakarta Bean Validation |
| Boilerplate | Lombok |
| Testing | JUnit 5 + Mockito |
| Build | Maven |

---

## How to Run

### Prerequisites
- Java 17+ installed (`java -version`)
- Maven 3.8+ installed (`mvn -version`)

### Steps

```bash
# 1. Clone / navigate to the project
cd core_backend_fundamentals

# 2. Build the project
mvn clean install

# 3. Run the application
mvn spring-boot:run
```

You should see:
```
Started KycProfileApplication in X.XXX seconds
Tomcat started on port(s): 8080
```

The API is now live at: **`http://localhost:8080`**

---

## API Reference with cURL Examples & Responses

### 📘 Swagger UI Navigation
You can view and test the interactive API documentation via Swagger UI. Once the app is running, open your browser and navigate to:
**👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

### ✅ POST `/api/kyc` — Create KYC Profile

**Request:**
```bash
curl -X POST http://localhost:8080/api/kyc \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C001",
    "fullName": "John Doe",
    "documentType": "PASSPORT",
    "documentNumber": "A1234567"
  }'
```

**Response — `201 Created`:**
```json
{
  "customerId": "C001",
  "fullName": "John Doe",
  "documentType": "PASSPORT",
  "documentNumber": "A1234567",
  "kycStatus": "PENDING",
  "createdAt": "2026-04-20T17:45:00"
}
```

> 🔑 **Note:** `kycStatus` is always `PENDING` on creation. `createdAt` is auto-set and immutable.

---

### ✅ GET `/api/kyc/{customerId}` — Get KYC Profile

**Request:**
```bash
curl -X GET http://localhost:8080/api/kyc/C001
```

**Response — `200 OK`:**
```json
{
  "customerId": "C001",
  "fullName": "John Doe",
  "documentType": "PASSPORT",
  "documentNumber": "A1234567",
  "kycStatus": "PENDING",
  "createdAt": "2026-04-20T17:45:00"
}
```

---

## Error Scenarios

---

### ❌ Duplicate customerId — `409 Conflict`

**Request:** (C001 already created above)
```bash
curl -X POST http://localhost:8080/api/kyc \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C001",
    "fullName": "Jane Smith",
    "documentType": "ID_CARD",
    "documentNumber": "B9876543"
  }'
```

**Response — `409 Conflict`:**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "KYC profile already exists for customerId: C001",
  "timestamp": "2026-04-20T17:46:00"
}
```

---

### ❌ Customer Not Found — `404 Not Found`

**Request:**
```bash
curl -X GET http://localhost:8080/api/kyc/X999
```

**Response — `404 Not Found`:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "KYC profile not found for customerId: X999",
  "timestamp": "2026-04-20T17:46:30"
}
```

---

### ❌ Missing Required Fields — `400 Bad Request`

**Request:** (missing `documentNumber`)
```bash
curl -X POST http://localhost:8080/api/kyc \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C002",
    "fullName": "Jane Smith",
    "documentType": "ID_CARD"
  }'
```

**Response — `400 Bad Request`:**
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "documentNumber: documentNumber is required",
  "timestamp": "2026-04-20T17:47:00"
}
```

---

### ❌ Empty Body — `400 Bad Request`

**Request:**
```bash
curl -X POST http://localhost:8080/api/kyc \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Response — `400 Bad Request`:**
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "customerId: customerId is required; documentNumber: documentNumber is required; documentType: documentType is required; fullName: fullName is required",
  "timestamp": "2026-04-20T17:47:10"
}
```

---

## H2 Console — View Database

The H2 web console is enabled for development inspection.

1. Open browser → `http://localhost:8080/h2-console`
2. Set these values:
   - **JDBC URL:** `jdbc:h2:mem:kycdb`
   - **Username:** `sa`
   - **Password:** _(leave blank)_
3. Click **Connect**
4. Run: `SELECT * FROM KYC_PROFILES;`

---

## Running Unit Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=KycProfileServiceImplTest
```

**Tests covered:**

| Test | Scenario |
|------|----------|
| `createProfile_success` | Creates profile, verifies PENDING status returned |
| `createProfile_duplicate_throwsConflict` | Throws `CustomerAlreadyExistsException` for existing customerId |
| `getProfile_success` | Returns correct profile for known customerId |
| `getProfile_notFound_throwsException` | Throws `CustomerNotFoundException` for unknown customerId |

---

## Demo Guide for Presenters

> Use this guide to walk through a live demo in front of an audience or client.

---

### 🎬 Step 1 — Start the Application

```bash
cd core_backend_fundamentals
mvn spring-boot:run
```

✅ Wait for: `Started KycProfileApplication`

---

### 🎬 Step 2 — Create a KYC Profile (Happy Path)

Open a terminal and run:

```bash
curl -X POST http://localhost:8080/api/kyc \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C001",
    "fullName": "John Doe",
    "documentType": "PASSPORT",
    "documentNumber": "A1234567"
  }'
```

**Point out:**
- Status `201 Created` — not just `200 OK`
- `kycStatus` is automatically set to `PENDING`
- `createdAt` is auto-populated

---

### 🎬 Step 3 — Retrieve the Profile (Happy Path)

```bash
curl -X GET http://localhost:8080/api/kyc/C001
```

**Point out:**
- `200 OK` with full profile details
- No database config — all in-memory with H2

---

### 🎬 Step 4 — Show Duplicate Protection (Business Rule)

```bash
curl -X POST http://localhost:8080/api/kyc \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C001",
    "fullName": "Duplicate User",
    "documentType": "ID_CARD",
    "documentNumber": "X0000000"
  }'
```

**Point out:**
- `409 Conflict` — banking systems must never create duplicate KYC profiles
- Clear error message identifies which `customerId` caused the conflict

---

### 🎬 Step 5 — Show Not Found Error

```bash
curl -X GET http://localhost:8080/api/kyc/UNKNOWN123
```

**Point out:**
- `404 Not Found` — clean, predictable, structured error response
- `status`, `error`, `message`, `timestamp` — consistent envelope for all errors

---

### 🎬 Step 6 — Show Validation in Action

```bash
curl -X POST http://localhost:8080/api/kyc \
  -H "Content-Type: application/json" \
  -d '{"customerId": "C002"}'
```

**Point out:**
- `400 Bad Request` — all missing fields reported in one response
- Validation happens at the API boundary, not inside business logic

---

### 🎬 Step 7 — Inspect the Database (Bonus)

Open browser → `http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:kycdb` | User: `sa` | Password: _(blank)_

```sql
SELECT * FROM KYC_PROFILES;
```

**Point out:**
- The `KYC_STATUS` column shows `PENDING` as the default
- `CREATED_AT` is populated automatically

---

### 🎬 Step 8 — Run Unit Tests

```bash
mvn test
```

**Point out:**
- 4 tests covering success and failure paths
- Mockito is used — repository is mocked, only service logic is tested
- No database needed for unit tests

---

### 🗣️ Key Talking Points for the Demo

| Topic | What to say |
|-------|-------------|
| **Layered Architecture** | "Controller does nothing except accept the request and return a response. All logic lives in the service." |
| **Enum for Status** | "Using an enum prevents invalid status values from ever entering the system — type-safety by design." |
| **Immutable audit field** | "`createdAt` has `updatable = false` — once set, it can never be changed. This is a banking-grade audit requirement." |
| **Interface + Impl** | "The controller depends on `KycProfileService` interface, not the concrete class. This makes it fully mockable and testable." |
| **Centralised errors** | "One `GlobalExceptionHandler` handles all errors. There are zero try/catch blocks anywhere in the business code." |
| **H2 for dev** | "H2 means zero setup for reviewers — just run the app, everything works. Production would swap in PostgreSQL with one config change." |

---

*Built in compliance with the 1-Hour Developer Baseline Assessment spec.*