# 📦 Product Service

Servicio de gestión de productos e inventario para el sistema de e-commerce.

## 📋 Descripción

El Product Service es responsable de:
- CRUD de productos
- Gestión de categorías
- Control de inventario/stock
- Búsqueda y filtrado de productos
- Actualización de stock (consumido por Order Service)

## 🏗️ Arquitectura

### Capas

```
presentation/
└── controller/
    └── ProductController.java
│
application/
├── dto/
│   ├── ProductDTO.java
│   └── UpdateStockRequest.java
├── usecase/
│   ├── CreateProductUseCase.java
│   ├── UpdateStockUseCase.java
│   └── SearchProductsUseCase.java
└── service/
    └── ProductService.java
│
domain/
├── model/
│   ├── Product.java
│   └── Category.java
└── repository/
    └── ProductRepository.java
│
infrastructure/
└── config/
    └── SecurityConfig.java
```

## 🚀 Tecnologías

- **Spring Boot 3.2.0**
- **Spring Data JPA** - Persistencia
- **PostgreSQL** - Base de datos
- **Spring Security** - Autenticación JWT
- **Lombok** - Reducción de boilerplate

## ⚙️ Configuración

### Variables de Entorno

```yaml
# Database
spring.datasource.url=jdbc:postgresql://postgres-product:5432/product_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT
jwt.secret=${JWT_SECRET}

# CORS
cors.allowed-origins=http://localhost:3000
```

## 📡 API Endpoints

### Productos

```http
GET /api/products
```
Obtiene todos los productos.

```http
GET /api/products/{id}
```
Obtiene un producto por ID.

```http
POST /api/products
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Producto Ejemplo",
  "description": "Descripción del producto",
  "price": 99.99,
  "stock": 100,
  "category": "ELECTRONICS",
  "imageUrl": "https://example.com/image.jpg"
}
```

```http
PUT /api/products/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Producto Actualizado",
  "price": 89.99,
  "stock": 150
}
```

```http
DELETE /api/products/{id}
Authorization: Bearer {token}
```

### Stock

```http
PUT /api/products/{id}/stock
Authorization: Bearer {token}
Content-Type: application/json

{
  "quantity": -5
}
```
Actualiza el stock (valores negativos decrementan, positivos incrementan).

```http
GET /api/products/{id}/stock
```
Obtiene el stock actual de un producto.

### Búsqueda

```http
GET /api/products/search?name={name}
```
Busca productos por nombre.

```http
GET /api/products/category/{category}
```
Filtra productos por categoría.

## 📊 Modelo de Datos

### Product

```java
- id: UUID
- name: String
- description: String (TEXT)
- price: BigDecimal
- stock: Integer
- category: String
- imageUrl: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

### Category

Categorías predefinidas:
- ELECTRONICS
- CLOTHING
- BOOKS
- HOME
- SPORTS
- TOYS

## 🔄 Integración con Order Service

El Order Service consume este servicio para:
1. **Validar productos** antes de crear una orden
2. **Actualizar stock** cuando se crea una orden
3. **Restaurar stock** cuando se cancela una orden

### Endpoint de Integración

```http
PUT /api/products/{id}/stock
Authorization: Bearer {service-token}
Content-Type: application/json

{
  "quantity": -10  # Decrementa 10 unidades
}
```

## 🧪 Testing

### Crear Producto

```bash
curl -X POST http://localhost:8082/api/products \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High performance laptop",
    "price": 999.99,
    "stock": 50,
    "category": "ELECTRONICS"
  }'
```

### Listar Productos

```bash
curl -X GET http://localhost:8082/api/products
```

### Actualizar Stock

```bash
curl -X PUT http://localhost:8082/api/products/{id}/stock \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"quantity": -5}'
```

## 🐛 Troubleshooting

### Stock insuficiente

Si el stock es menor que la cantidad solicitada, el servicio retorna:
```json
{
  "error": "Insufficient stock",
  "available": 10,
  "requested": 15
}
```

### Producto no encontrado

```json
{
  "error": "Product not found",
  "productId": "uuid-here"
}
```

## 📝 Notas

- El stock nunca puede ser negativo
- Los precios se almacenan con 2 decimales de precisión
- Las actualizaciones de stock son transaccionales
- Se requiere autenticación JWT para crear/actualizar/eliminar

## 🔗 Enlaces

- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
