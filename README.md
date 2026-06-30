5. API runs on `http://localhost:8080`

### Creating Admin/Super Admin accounts

Register normally creates a `USER`. To get ADMIN or SUPER_ADMIN access,
update the role directly in the database after registration:

```sql
UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'SUPER_ADMIN')
WHERE email = 'youremail@example.com';
```

## API Endpoints

### Auth
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | /api/auth/register | Public |
| POST | /api/auth/login | Public |

### Categories
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | /api/categories | Authenticated |
| GET | /api/super-admin/categories | SUPER_ADMIN |
| POST | /api/super-admin/categories | SUPER_ADMIN |
| PUT | /api/super-admin/categories/{id} | SUPER_ADMIN |
| PATCH | /api/super-admin/categories/{id}/toggle | SUPER_ADMIN |
| DELETE | /api/super-admin/categories/{id} | SUPER_ADMIN |

### Products
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | /api/products | Authenticated |
| GET | /api/products/{id} | Authenticated |
| GET | /api/products/category/{categoryId} | Authenticated |
| GET | /api/products/search?name= | Authenticated |
| GET | /api/admin/products | ADMIN, SUPER_ADMIN |
| POST | /api/admin/products | ADMIN, SUPER_ADMIN |
| PUT | /api/admin/products/{id} | ADMIN, SUPER_ADMIN |
| PATCH | /api/admin/products/{id}/price?price= | ADMIN, SUPER_ADMIN |
| PATCH | /api/admin/products/{id}/inventory?quantity= | ADMIN, SUPER_ADMIN |
| PATCH | /api/admin/products/{id}/toggle | ADMIN, SUPER_ADMIN |
| PATCH | /api/admin/products/{id}/category/{categoryId} | ADMIN, SUPER_ADMIN |

### Cart
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | /api/cart | USER |
| POST | /api/cart/items | USER |
| PATCH | /api/cart/items/{cartItemId}?quantity= | USER |
| DELETE | /api/cart/items/{cartItemId} | USER |
| DELETE | /api/cart | USER |

### Addresses
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | /api/addresses | USER |
| POST | /api/addresses | USER |
| PUT | /api/addresses/{id} | USER |
| DELETE | /api/addresses/{id} | USER |

### Orders
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | /api/orders/checkout | USER |
| GET | /api/orders | USER |
| GET | /api/orders/{id} | USER |
| PATCH | /api/orders/{id}/cancel | USER |
| GET | /api/super-admin/orders | SUPER_ADMIN |
| GET | /api/super-admin/orders/status/{status} | SUPER_ADMIN |
| PATCH | /api/super-admin/orders/{id}/status?status= | SUPER_ADMIN |

### User Management
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | /api/super-admin/users | SUPER_ADMIN |
| PATCH | /api/super-admin/users/{id}/role?role= | SUPER_ADMIN |
| PATCH | /api/super-admin/users/{id}/toggle | SUPER_ADMIN |

## Sample API Requests

### Register
```json
POST /api/auth/register
{
    "name": "Advait Bothe",
    "email": "advait@gmail.com",
    "password": "123456"
}
```

### Login
```json
POST /api/auth/login
{
    "email": "advait@gmail.com",
    "password": "123456"
}

Response:
{
    "token": "eyJhbGci...",
    "email": "advait@gmail.com",
    "name": "Advait Bothe",
    "role": "USER"
}
```

### Create Product (Admin)
```json
POST /api/admin/products
Authorization: Bearer <token>
{
    "name": "iPhone 15",
    "description": "Latest Apple iPhone",
    "price": 79999,
    "categoryId": 1,
    "quantity": 50
}
```

### Add to Cart
```json
POST /api/cart/items
Authorization: Bearer <token>
{
    "productId": 1,
    "quantity": 2
}
```

### Checkout
```json
POST /api/orders/checkout
Authorization: Bearer <token>
{
    "addressId": 1
}
```

## Key Design Decisions

- **Price snapshotting**: Cart and order items store the product price at
  the time of addition/order, so later price changes don't retroactively
  affect existing carts or orders.

- **Inventory validation**: Stock is checked at both add-to-cart time and
  again at checkout (double-check pattern) to prevent race conditions
  between adding to cart and placing the order.

- **Transactional checkout**: The entire checkout flow (order creation,
  inventory reduction, cart clearing) runs in a single `@Transactional`
  block — if any step fails, everything rolls back, ensuring inventory
  is never reduced for a failed order.

- **Order cancellation restores inventory**: Cancelling a PENDING or
  CONFIRMED order automatically returns the reserved stock.

- **Category-product cascade**: Disabling a category hides its products
  from user-facing endpoints even if the products themselves are enabled.
  Deleting a category is blocked if it still has associated products.

## Author

Advait Bothe