# 🛒 ShopEase - Product Management & Order Processing System

A role-based e-commerce backend built using **Spring Boot**, featuring JWT authentication, secure REST APIs, shopping cart management, inventory tracking, and transactional order processing.

---

## 🚀 Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- JWT Authentication
- Spring Data JPA (Hibernate)
- MySQL
- Maven

---

## ✨ Features

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (RBAC)
- Stateless security
- BCrypt password encryption

### Customer Features
- User Registration & Login
- Browse products
- Search products
- Filter products by category
- Shopping cart management
- Multiple delivery addresses
- Checkout
- View order history
- Cancel orders

### Admin Features
- Product CRUD
- Inventory management
- Price management
- Enable/Disable products
- Assign categories

### Super Admin Features
- User management
- Role management
- Category management
- View all orders
- Update order status

---

# 👥 User Roles

| Role | Permissions |
|------|-------------|
| USER | Browse products, manage cart, addresses, checkout, view orders |
| ADMIN | Manage products, inventory and pricing |
| SUPER_ADMIN | Full system access including user, category and order management |

---

# 🏗 Architecture

```
Controller
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
MySQL Database
```

Project Structure

```
src
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── security
└── service
```

---

# 🗄 Database Schema

```
users
roles
categories
products
inventory
cart
cart_items
addresses
orders
order_items
```

### Entity Relationships

```
User
 ├── Cart (1:1)
 ├── Address (1:N)
 └── Orders (1:N)

Category
 └── Products (1:N)

Product
 └── Inventory (1:1)

Order
 └── OrderItems (1:N)

Cart
 └── CartItems (1:N)
```

---

# 🔐 Security

- JWT Authentication
- Stateless Sessions
- BCrypt Password Encoding
- Role-Based Authorization
- Spring Security Filter Chain
- Custom JWT Filter
- Custom UserDetailsService

---

# ⚙️ Setup

## 1. Clone

```bash
git clone https://github.com/<your-username>/shopease.git
cd shopease
```

---

## 2. Create Database

```sql
CREATE DATABASE shopease_db;
```

---

## 3. Configure

Update `application.properties`

```properties
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=

jwt.secret=
```

---

## 4. Run

```bash
mvn spring-boot:run
```

Application starts at

```
http://localhost:8080
```

---

# 📌 API Overview

## Authentication

| Method | Endpoint |
|---------|----------|
| POST | /api/auth/register |
| POST | /api/auth/login |

---

## Products

| Method | Endpoint |
|---------|----------|
| GET | /api/products |
| GET | /api/products/{id} |
| GET | /api/products/search |
| GET | /api/products/category/{id} |

---

## Cart

| Method | Endpoint |
|---------|----------|
| GET | /api/cart |
| POST | /api/cart/items |
| PATCH | /api/cart/items/{id} |
| DELETE | /api/cart/items/{id} |

---

## Orders

| Method | Endpoint |
|---------|----------|
| POST | /api/orders/checkout |
| GET | /api/orders |
| PATCH | /api/orders/{id}/cancel |

---

## Admin

| Method | Endpoint |
|---------|----------|
| POST | /api/admin/products |
| PUT | /api/admin/products/{id} |
| PATCH | /api/admin/products/{id}/inventory |
| PATCH | /api/admin/products/{id}/price |

---

## Super Admin

| Method | Endpoint |
|---------|----------|
| GET | /api/super-admin/users |
| PATCH | /api/super-admin/users/{id}/role |
| GET | /api/super-admin/orders |
| PATCH | /api/super-admin/orders/{id}/status |

---

# 💡 Design Decisions

### Transactional Checkout

Checkout is executed within a single `@Transactional` block.

If any operation fails:

- Inventory rollback
- Cart rollback
- Order rollback

ensuring data consistency.

---

### Price Snapshotting

Order items store the product price at purchase time, preventing later price updates from affecting historical orders.

---

### Inventory Validation

Inventory is validated:

- When adding items to cart
- Again during checkout

to prevent overselling.

---

### Order Cancellation

Cancelling an order automatically restores reserved inventory.

---

### DTO-Based Architecture

Controllers expose DTOs instead of JPA entities, keeping persistence models isolated from API contracts.

---

# 📷 Screenshots

You can add screenshots here later.

```
Login Screen

Admin Dashboard

User Dashboard

Order Flow
```

---

# 🔮 Future Improvements

- Redis Caching
- Docker
- Kafka Event Notifications
- Payment Gateway Integration
- Email Notifications
- Swagger/OpenAPI Documentation
- Unit & Integration Testing
- CI/CD Pipeline

---

# 👨‍💻 Author

**Advait Bothe**

GitHub: https://github.com/AdvaitBothe1105
