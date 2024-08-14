## Spring Boot Microservices - Ecommerce for test BVK

# How to run the application using Docker

1. Run `mvn clean package -DskipTests` to build the applications and create the docker image locally.
2. Run `docker compose rm -f` (OPTIONAL)
3. Run `docker compose up -d` to start the applications.

# API Documentation
**Discovery Service : http://localhost:8081/eureka/web**

**Health : http://localhost:8081/actuator/health**

**Base URL : http://localhost:8081**

**1. Create Product**

Endpoint : /api/product

Method : POST

Request
```json
{
    "name": "Jaket Jeans",
    "stock": 100,
    "price": 55000
}
```

Response
```json
{
    "data": {
        "productId": 2,
        "productName": "Jaket Jeans",
        "stock": 100,
        "price": 55000.0
    },
    "errors": null
}
```

**2. Get Inventories**

Endpoint : /api/inventory?pid=1&pid=2 => pid : product id

Method : GET

Response
```json
{
    "data": [
        {
            "productId": 1,
            "productName": "Jaket Jeans",
            "stock": 100,
            "price": 5000.0
        },
        {
            "productId": 2,
            "productName": "Celana Jeans",
            "stock": 100,
            "price": 55000.0
        }
    ],
    "errors": null
}
```

**3. Update Inventory**

Endpoint : /api/inventory

Method : PATCH

Request
```json
[
    {
        "productId": 1,
        "stockChange": -2
    }
]
```

Response
```json
{
    "data": "OK",
    "errors": null
}
```

**3. Order**

Endpoint : /api/order

Method : PATCH

Request
```json
{
    "orderDetails": [
        {
            "productId": 1,
            "quantity": 5,
            "price": 10000
        },
        {
            "productId": 2,
            "quantity": 5,
            "price": 5000
        }
    ]
}
```

Response
```json
{
    "data": {
        "orderId": 1,
        "orderDate": "2024-08-14T12:18:12.692+00:00",
        "amount": 15000.0,
        "orderDetails": [
            {
                "productId": 1,
                "quantity": 5,
                "price": 10000.0
            },
            {
                "productId": 2,
                "quantity": 5,
                "price": 5000.0
            }
        ]
    },
    "errors": null
}
```
