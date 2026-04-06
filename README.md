# Financial Management System (FMS)

A Spring Boot application for managing financial records with role-based access control.

## Tech Stack

- Java 17
- Spring Boot 3.3.5
- Spring Security (JWT + Session)
- H2 Database (in-memory)
- Thymeleaf (frontend)

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Setup & Run

```bash
# Clone repository
git clone <repository-url>
cd FMS

# Build and run
mvn clean install
mvn spring-boot:run
```

### Access Application

- **Web UI**: http://localhost:8080

## Default Users

| Email | Password | Role |
|-------|----------|------|
| admin@fms.com | admin123 | ADMIN |
| analyst@fms.com | analyst123 | ANALYST |
| viewer@fms.com | viewer123 | VIEWER |

## User Permissions

| Action | ADMIN | ANALYST | VIEWER |
|--------|-------|---------|--------|
| View Records | ✅ | ✅ | ✅ |
| Create/Edit/Delete Records | ✅ | ❌ | ❌ |
| Manage Users | ✅ | ❌ | ❌ |

# Architecture & Design Decisions

## Architecture Pattern
```
Controllers → Use Cases (Ports) → Services (Adapters) → Repositories → Database
```

- **Ports**: Interfaces defining business logic (`RecordUseCase`, `UserUseCase`)
- **Adapters**: Implementations (`FinancialRecordService`, `UserService`)
- **Controllers**: REST API and Web UI endpoints

---

## Key Assumptions

### 1. No Idempotency handling
- we assumed that client sends *Single* api call for a record

### 2. Single Currency
- All amounts in one currency (₹ INR assumed)
- No currency conversion or multi-currency support

### 3. Admin-Only User Creation
- Users cannot self-register
- Only ADMIN can create new user accounts
- Suitable for internal organizational use

### 4. Soft Delete
- Records marked as `deleted=true`, not physically removed
- Enables data recovery and audit trail
- Queries filter `deleted=false`

### 5. Development Database
- H2 in-memory for development (data lost on restart)
- PostgreSQL recommended for production
- Schema auto-created in development
---

## Critical Tradeoffs

### 1. Database: H2 → PostgreSQL

**Decision**: H2 for dev, PostgreSQL for production

**Why**:
- Zero config for local development
- Fast setup and testing
- No external dependencies

**Tradeoff**:
- SQL dialect differences between H2 and PostgreSQL
- Must test on production database before deployment
---

### 2. Role-Based Access (RBAC)

**Decision**: Fixed roles (VIEWER, ANALYST, ADMIN)

**Why**:
- Simple to implement and understand
- Covers 90% of use cases
- Easy to enforce with Spring Security

**Limitation**: Cannot customize per-user permissions

**Implementation**:
```java
@PreAuthorize("hasRole('ADMIN')")
public void delete(Long id) { ... }
```

---

## Future Enhancements

- Advanced filtering and search
- Data export (CSV, Excel, PDF)
- Recurring transactions

# API Documentation

Base URL: `http://localhost:8080/api`

## Authentication

All API requests require authentication via JWT token.

### Login

**POST** `/api/auth/login`

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@fms.com",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

### Register User (ADMIN only)

**POST** `/api/auth/register`

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@fms.com",
    "password": "password123",
    "name": "User Name",
    "role": "VIEWER"
  }'
```

---

## Records

### Get All Records

**GET** `/api/records?page=0&size=10&type=INCOME&from=2024-01-01&to=2024-12-31`

```bash
curl -X GET "http://localhost:8080/api/records?page=0&size=10" \
  -H "Authorization: Bearer <token>"
```

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Items per page (default: 10)
- `type` - Filter by INCOME or EXPENSE
- `category` - Filter by category name
- `from` - Start date (YYYY-MM-DD)
- `to` - End date (YYYY-MM-DD)

### Get Record by ID

**GET** `/api/records/{id}`

```bash
curl -X GET http://localhost:8080/api/records/1 \
  -H "Authorization: Bearer <token>"
```

### Create Record (ADMIN only)

**POST** `/api/records`

```bash
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000,
    "type": "INCOME",
    "category": "Salary",
    "date": "2024-01-15",
    "notes": "Monthly salary"
  }'
```

**Required Fields:**
- `amount` - Decimal (min: 0.01)
- `type` - "INCOME" or "EXPENSE"
- `category` - String (max: 100 chars)
- `date` - String (YYYY-MM-DD)
- `notes` - String (optional, max: 500 chars)

### Update Record (ADMIN only)

**PUT** `/api/records/{id}`

```bash
curl -X PUT http://localhost:8080/api/records/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 6000,
    "type": "INCOME",
    "category": "Salary",
    "date": "2024-01-15",
    "notes": "Updated salary"
  }'
```

### Delete Record (ADMIN only)

**DELETE** `/api/records/{id}`

```bash
curl -X DELETE http://localhost:8080/api/records/1 \
  -H "Authorization: Bearer <token>"
```

---

## Users

### Get All Users (ADMIN only)

**GET** `/api/users`

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer <token>"
```

### Create User (ADMIN only)

**POST** `/api/users`

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@fms.com",
    "password": "password123",
    "name": "New User",
    "role": "ANALYST"
  }'
```

### Update User Role (ADMIN only)

**PATCH** `/api/users/{id}/role`

```bash
curl -X PATCH http://localhost:8080/api/users/2/role \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"role": "ANALYST"}'
```

### Toggle User Status (ADMIN only)

**PATCH** `/api/users/{id}/toggle-status`

```bash
curl -X PATCH http://localhost:8080/api/users/2/toggle-status \
  -H "Authorization: Bearer <token>"
```

---

## Error Responses

### Common Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 204 | No Content (successful delete) |
| 400 | Validation error |
| 401 | Unauthorized (invalid/missing token) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not found |

### Error Format

```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: amount must be greater than 0"
}
```

