# ShopEase — Product Management & Order Processing System

A role-based e-commerce backend built with Spring Boot, handling product
catalog management, shopping cart operations, and order processing with
real-time inventory validation.

## Tech Stack

- Java 21
- Spring Boot
- Spring Security + JWT
- Spring Data JPA / Hibernate
- MySQL
- Maven

## Roles

| Role | Permissions |
|------|-------------|
| USER | Browse products, manage cart, manage addresses, checkout |
| ADMIN | Add/update products, manage price & inventory, enable/disable products, assign categories |
| SUPER_ADMIN | All ADMIN permissions + manage users/roles + manage categories + view all orders |

## Features

- JWT-based authentication with role-based access control
- Product catalog with category filtering and search
- Shopping cart with price snapshotting and stock validation
- Checkout flow with atomic inventory reduction (rollback on failure)
- Order cancellation with automatic inventory restoration
- Admin dashboard for product, category, and order management
- Super Admin dashboard for user/role management
- Global exception handling with structured error responses
- DTO-based request/response separation (no entity leakage)

## Architecture