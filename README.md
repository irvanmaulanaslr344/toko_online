# Toko Online API

REST API untuk E-Commerce Toko Online.

## Tech Stack
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- MySQL

## Project Structure
- `config/` - Konfigurasi security, JWT, CORS, file upload
- `controller/` - REST API endpoints (public & seller)
- `service/` - Business logic
- `repository/` - Database access (JPA)
- `entity/` - Database entities
- `dto/` - Request & response objects
- `exception/` - Custom exceptions
- `enums/` - Enum definitions

## Setup
1. Buat database `tokoonline` di MySQL
2. Sesuaikan `application.properties`
3. Run `mvn spring-boot:run`

## Default Port
`http://localhost:8080`

## API Base URL
`http://localhost:8080/api`

## API Documentation
Coming soon...