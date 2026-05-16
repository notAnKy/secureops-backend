# SecureOps — Backend API

<div align="center">

![SecureOps](https://img.shields.io/badge/SecureOps-Backend%20API-00ffd2?style=for-the-badge&logo=shield&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

**REST API backend for the SecureOps cybersecurity management platform.**  
Spring Boot · Spring Security · JWT · PostgreSQL · Spring Mail

👉 **Frontend Repository:** [secureops-frontend](https://github.com/notAnKy/secureops-frontend)

</div>

---

## Overview

This is the backend REST API for SecureOps — a full-stack cybersecurity management platform. It handles authentication, role-based access control, request lifecycle management, task assignment, report validation, and automated email notifications.

The API is consumed by the React TypeScript frontend and exposes endpoints for three user roles: **Admin**, **Client**, and **Employee**.

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Programming Language |
| Spring Boot | 3.5.13 | Application Framework |
| Spring Security | 6 | Authentication & Authorization |
| Spring Data JPA | — | Data Access Layer |
| Hibernate | 6.6 | ORM / Database Mapping |
| JWT (jjwt) | 0.11.5 | Stateless Token Authentication |
| BCrypt | — | Password Hashing |
| Spring Mail | — | Email Notifications |
| PostgreSQL | 17 | Primary Database |
| Lombok | — | Boilerplate Reduction |
| Maven | — | Build & Dependency Management |

---

## Architecture

The application follows a standard Spring Boot layered architecture:

```
HTTP Request
     ↓
Controller Layer        → Handles HTTP, extracts params, returns responses
     ↓
Service Layer           → Business logic, validation, authorization checks
     ↓
Repository Layer        → JPA data access (extends JpaRepository)
     ↓
Entity Layer            → JPA entities mapped to PostgreSQL tables
     ↓
PostgreSQL Database
```

### Package Structure

```
com.cyberplatform.backend/
├── controller/
│   ├── AuthController.java           # /api/auth/** — login, register, password reset
│   ├── AdminUserController.java      # /api/admin/users/**
│   ├── ServiceController.java        # /api/admin/services/**
│   ├── AdminRequestController.java   # /api/admin/requests/**
│   ├── AdminTaskController.java      # /api/admin/tasks/**
│   ├── AdminReportController.java    # /api/admin/reports/**
│   ├── DashboardController.java      # /api/admin/dashboard/**
│   ├── DemandeController.java        # /api/client/**
│   └── EmployeeController.java       # /api/employee/**
├── service/
│   ├── UserService.java
│   ├── AdminServiceService.java
│   ├── AdminRequestService.java
│   ├── AdminTaskService.java
│   ├── AdminReportService.java
│   ├── ClientService.java
│   ├── EmployeeService.java
│   ├── PasswordResetService.java
│   └── EmailService.java
├── repository/
│   ├── UserRepository.java
│   ├── DemandeRepository.java
│   ├── TacheRepository.java
│   ├── ServiceRepository.java
│   └── RapportRepository.java
├── entity/
│   ├── User.java
│   ├── Demande.java
│   ├── Tache.java
│   ├── Service.java
│   └── Rapport.java
├── dto/
│   ├── request/                      # Incoming request DTOs
│   └── response/                     # Outgoing response DTOs
└── security/
    ├── JwtService.java               # Token generation & validation
    ├── JwtAuthFilter.java            # Request filter
    ├── SecurityConfig.java           # Filter chain configuration
    └── AuthEntryPoint.java           # 401 handler
```

---

## Database Schema

The database consists of **7 tables** with a single `user` table storing all roles.

### Entity Relationship Overview

```
user (CLIENT) ──────────────── demande ──────── demande_service ──── service
                                  │
                               tache ──── tache_employe ──── user (EMPLOYEE)
                                  │
                               rapport
                                  │
                            user (EMPLOYEE)
```

### Table Definitions

#### `user`
Stores all platform users regardless of role. Role-specific fields are nullable.

| Column | Type | Description |
|---|---|---|
| id | BIGINT (PK) | Auto-increment primary key |
| code | VARCHAR (UNIQUE) | Unique login identifier |
| email | VARCHAR (UNIQUE) | Email address |
| mot_de_passe | VARCHAR | BCrypt hashed password |
| role | ENUM | CLIENT / ADMIN / EMPLOYEE |
| nom | VARCHAR | Last name |
| prenom | VARCHAR | First name |
| telephone | VARCHAR | Personal phone |
| specialite | VARCHAR | Technical specialty (EMPLOYEE only) |
| raison_sociale | VARCHAR | Company name (CLIENT only) |
| siret | VARCHAR | SIRET business number (CLIENT only) |
| adresse_siege | VARCHAR | Registered address (CLIENT only) |
| telephone_entreprise | VARCHAR | Company phone (CLIENT only) |
| reset_token | VARCHAR | Password reset token (temporary) |
| reset_token_expiry | TIMESTAMP | Reset token expiry (1 hour) |
| created_at | TIMESTAMP | Auto-set on creation |

#### `service`
The catalog of cybersecurity services offered by the platform.

| Column | Type | Description |
|---|---|---|
| id | BIGINT (PK) | Auto-increment primary key |
| nom | VARCHAR | Service name |
| description | TEXT | Service description |
| type | VARCHAR | Service category |
| prix | DECIMAL | Service price |
| created_at | TIMESTAMP | Creation timestamp |

#### `demande`
A client's security service request.

| Column | Type | Description |
|---|---|---|
| id_demande | BIGINT (PK) | Auto-increment primary key |
| description | TEXT | Request description |
| etat | ENUM | PENDING / IN_PROGRESS / COMPLETED / CANCELLED |
| priorite | VARCHAR | HIGH / MEDIUM / LOW |
| date_soumission | TIMESTAMP | Auto-set on creation |
| date_limite | DATE | Desired completion date |
| client_id | BIGINT (FK) | References user (CLIENT) |

#### `demande_service`
Junction table for many-to-many between `demande` and `service`.

| Column | Type | Description |
|---|---|---|
| demande_id | BIGINT (FK) | References demande |
| service_id | BIGINT (FK) | References service |

#### `tache`
A task created by admin and assigned to employees.

| Column | Type | Description |
|---|---|---|
| id_tache | BIGINT (PK) | Auto-increment primary key |
| description | TEXT | Task description |
| statut | ENUM | NOT_STARTED / IN_PROGRESS / COMPLETED / RESOLVED |
| date_debut | DATE | Task start date |
| date_fin_prevue | DATE | Expected completion |
| date_fin_reelle | DATE | Actual completion |
| demande_id | BIGINT (FK) | References demande |

#### `tache_employe`
Junction table for many-to-many between `tache` and `user` (EMPLOYEE).

| Column | Type | Description |
|---|---|---|
| tache_id | BIGINT (FK) | References tache |
| employe_id | BIGINT (FK) | References user (EMPLOYEE) |

#### `rapport`
A report submitted by an employee for a task.

| Column | Type | Description |
|---|---|---|
| id_rapport | BIGINT (PK) | Auto-increment primary key |
| contenu | TEXT | Report content |
| date_soumission | TIMESTAMP | Auto-set on creation |
| est_valide | BOOLEAN | false until admin validates |
| employe_id | BIGINT (FK) | References user (EMPLOYEE) |
| tache_id | BIGINT (FK) | References tache |
| demande_id | BIGINT (FK) | References demande (denormalized) |

---

## API Reference

### Base URL
```
http://localhost:8081/api
```

### Authentication
All protected endpoints require a JWT Bearer token:
```
Authorization: Bearer <jwt_token>
```

---

### Auth Endpoints — Public

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new CLIENT account |
| POST | `/api/auth/login` | Authenticate and receive JWT token |
| POST | `/api/auth/forgot-password` | Request password reset email |
| POST | `/api/auth/reset-password` | Reset password using token from email |

**Login Request:**
```json
{
  "code": "CLI001",
  "motDePasse": "password123"
}
```

**Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "code": "CLI001",
  "email": "client@company.com",
  "role": "CLIENT",
  "raisonSociale": "Acme Security Inc."
}
```

---

### Admin — User Management
> Requires `ADMIN` role

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/users?role=X&page=0&size=15` | Paginated users with optional role filter |
| POST | `/api/admin/users/employee` | Create a new EMPLOYEE account |
| PUT | `/api/admin/users/{id}/edit` | Update employee details |
| DELETE | `/api/admin/users/{id}` | Delete a user account |
| GET | `/api/admin/users/{id}/stats` | Get user activity statistics |

---

### Admin — Service Management
> Requires `ADMIN` role

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/services` | Get all services |
| POST | `/api/admin/services` | Create a new service |
| PUT | `/api/admin/services/{id}` | Update a service |
| DELETE | `/api/admin/services/{id}` | Delete a service |
| GET | `/api/admin/services/{id}/usage` | Get how many requests used this service |

---

### Admin — Request Management
> Requires `ADMIN` role

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/requests?status=X&page=0&size=15` | Paginated requests with optional status filter |
| GET | `/api/admin/requests/{id}` | Get single request details |
| PUT | `/api/admin/requests/{id}/status` | Update request status |

---

### Admin — Task Management
> Requires `ADMIN` role

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/requests/{id}/tasks` | Get all tasks for a request |
| POST | `/api/admin/requests/{id}/tasks` | Create a task for a request |
| DELETE | `/api/admin/tasks/{taskId}` | Delete a task |
| POST | `/api/admin/tasks/{taskId}/assign` | Assign an employee to a task |
| DELETE | `/api/admin/tasks/{taskId}/unassign/{empId}` | Remove employee from task |
| GET | `/api/admin/employees` | Get all employees (for assignment picker) |

---

### Admin — Report Management
> Requires `ADMIN` role

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/tasks/{id}/reports` | Get all reports for a task |
| GET | `/api/admin/requests/{id}/reports` | Get all reports for a request |
| PUT | `/api/admin/reports/{id}/validate` | Validate a report — sends email to client |
| PUT | `/api/admin/reports/{id}/invalidate` | Invalidate a report — sends email to client |
| GET | `/api/admin/dashboard/stats` | Get platform-wide statistics |

---

### Client Endpoints
> Requires `CLIENT` role

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/client/dashboard/stats` | Get client dashboard statistics |
| POST | `/api/client/requests` | Submit a new service request |
| GET | `/api/client/requests` | Get all requests for logged-in client |
| GET | `/api/client/requests/{id}` | Get single request (must belong to client) |
| PUT | `/api/client/requests/{id}/cancel` | Cancel a PENDING request |
| GET | `/api/client/requests/{id}/reports` | Get validated reports only |
| GET | `/api/client/profile` | Get client profile |
| PUT | `/api/client/profile` | Update contact info / change password |

---

### Employee Endpoints
> Requires `EMPLOYEE` role

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/employee/tasks` | Get tasks assigned to logged-in employee |
| GET | `/api/employee/tasks/{id}` | Get single task details |
| PUT | `/api/employee/tasks/{id}/status` | Update task status |
| POST | `/api/employee/tasks/{id}/report` | Submit a report for a task |
| GET | `/api/employee/tasks/{id}/reports` | Get all reports for a task |
| GET | `/api/employee/profile` | Get employee profile |
| PUT | `/api/employee/profile` | Update contact info / change password |

---

## Security Implementation

### JWT Authentication Flow

```
1. Client sends POST /api/auth/login with { code, motDePasse }
2. Spring Security authenticates via BCrypt password comparison
3. JwtService generates signed token (HMAC-SHA256, 24h expiry)
4. Token returned in AuthResponse
5. Client sends token as "Authorization: Bearer <token>" on every request
6. JwtAuthFilter validates token signature + expiry on every request
7. Spring Security context set with user details and role
```

### Role-Based Access Control

```java
.requestMatchers("/api/auth/**").permitAll()
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.requestMatchers("/api/client/**").hasAnyRole("CLIENT")
.requestMatchers("/api/employee/**").hasAnyRole("EMPLOYEE")
```

### Secondary Authorization
Service methods perform a second ownership check. Example — a client can only access their own requests:

```java
if (!demande.getClient().getId().equals(client.getId())) {
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
}
```

### Password Reset Flow

```
1. Client requests reset → POST /api/auth/forgot-password { email }
2. System generates UUID token, saves with 1-hour expiry on user record
3. Email sent via Mailtrap with reset link: /reset-password?token=<uuid>
4. Client submits new password → POST /api/auth/reset-password { token, newPassword }
5. System validates token exists and hasn't expired
6. Password BCrypt encoded and saved, token cleared from database
```

> Always returns 200 OK for forgot-password even if email not found — prevents email enumeration attacks.

---

## Email Notifications

The platform sends automated HTML emails for three events:

| Event | Trigger | Recipient |
|---|---|---|
| Report Validated | Admin validates a report | Client |
| Report Invalidated | Admin invalidates a report | Client |
| Password Reset | User requests password reset | User |

Emails are sent **asynchronously** using Spring's `@Async` annotation — email delivery never blocks the API response.

---

## Getting Started

### Prerequisites
- Java 21+
- Maven
- PostgreSQL 17+
- A Mailtrap account (free at [mailtrap.io](https://mailtrap.io))

### Database Setup

```sql
CREATE DATABASE cybersdb;
```

Hibernate will automatically create all tables on first run (`ddl-auto: update`).

### Configuration

Copy the example configuration file and fill in your values:

```bash
cp src/main/resources/application-example.yml src/main/resources/application.yml
```

Edit `application.yml` with your actual values:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cybersdb
    username: your_db_username
    password: your_db_password

  mail:
    username: your_mailtrap_username
    password: your_mailtrap_password

jwt:
  secret: "your_jwt_secret_key_minimum_32_characters"
```

### Running the Application

```bash
# Clone the repository
git clone https://github.com/notAnKy/secureops-backend.git
cd secureops-backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8081`

### Verify it's running

```bash
curl http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"code":"ADMIN001","motDePasse":"yourpassword"}'
```

---

## CORS Configuration

The backend accepts requests from the frontend origin only:

```
http://localhost:5173
```

If you deploy the frontend to a different URL, update the CORS configuration in `CorsConfig.java`.

---

## Frontend Repository

The React TypeScript frontend for this project is available at:  
👉 **[secureops-frontend](https://github.com/notAnKy/secureops-frontend)**

---

## Key Design Decisions

- **Single user table** for all roles — simplifies auth, nullable role-specific fields
- **`@PrePersist` hooks** — `date_soumission` and `est_valide` auto-set on entity creation
- **`est_valide` gate on reports** — clients never see unvalidated reports
- **`@Async` email sending** — email delivery never blocks API response
- **Full class name for `@Service`** — avoids conflict with `Service` entity: `@org.springframework.stereotype.Service`
- **`PagedResponse<T>` generic DTO** — consistent pagination wrapper across all paginated endpoints
- **Secondary ownership checks** in service layer — defense in depth beyond Spring Security role checks

---

## Authors

**Mohamed Ali Jemmali** - **Rami Boubakri** - **Zouhaier Karoui**  
Software Engineering — 2025/2026

---

<div align="center">

Built with ❤️ and a lot of ☕

</div>
