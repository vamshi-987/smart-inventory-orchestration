# Architecture

## High-Level Architecture

Frontend
↓
API Layer
↓
Service Layer
↓
Repository Layer
↓
PostgreSQL Database

## Backend Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Validation
- Lombok
- Docker

## Frontend Tech Stack

- React
- Axios
- React Router
- Tailwind CSS

## Backend Package Style

The project uses feature-based packages.

Example:

product/
- controller
- service
- service/impl
- repository
- domain
- dto
- mapper

warehouse/
inventory/
order/
notification/
common/

## Important Design Patterns

- Layered architecture
- DTO pattern
- Mapper pattern
- Interface-based service design
- Transaction management
- Global exception handling
- Warehouse allocation strategy pattern