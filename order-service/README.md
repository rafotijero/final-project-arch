# 🛒 Order Service

Servicio de gestión de órdenes para el sistema de e-commerce. Procesa órdenes y publica eventos a Kafka.

## 📋 Descripción

El Order Service es responsable de:
- Creación de órdenes
- Validación de productos y stock
- Gestión de estados de orden
- Publicación de eventos a Kafka
- Cancelación y actualización de órdenes

## 🏗️ Arquitectura

### Capas

```
presentation/
└── controller/
    └── OrderController.java
│
application/
├── dto/
│   ├── CreateOrderRequest.java
│   ├── OrderDTO.java
│   └── OrderItemDTO.java
├── usecase/
│   ├── CreateOrderUseCase.java
│   ├── CancelOrderUseCase.java
│   └── UpdateOrderStatusUseCase.java
├── service/
│   ├── OrderService.java
│   └── OrderEventPublisher.java
│
domain/
├── model/
│   ├── Order.java
│   └── OrderItem.java
└── repository/
    └── OrderRepository.java
│
infrastructure/
└── config/
    ├── KafkaProducerConfig.java
    ├── WebClientConfig.java
    └── SecurityConfig.java
```

## 🚀 Tecnologías

- **Spring Boot 3.2.0**
- **Spring Kafka** - Producer de eventos
- **Spring WebClient** - Cliente HTTP reactivo
- **PostgreSQL** - Base de datos
- **Spring Security** - Autenticación JWT
- **Lombok** - Reducción de boilerplate

## ⚙️ Configuración

### Variables de Entorno

```yaml
# Database
spring.datasource.url=jdbc:postgresql://postgres-order:5432/order_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Kafka
spring.kafka.bootstrap-servers=kafka:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Product Service
product.service.url=http://product-service:8082

# JWT
jwt.secret=${JWT_SECRET}
```

## 📡 API Endpoints

### Órdenes

```http
POST /api/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "items": [
    {
      "productId": "uuid-product-1",
      "quantity": 2
    },
    {
      "productId": "uuid-product-2",
      "quantity": 1
    }
  ]
}
```

**Response:**
```json
{
  "id": "uuid-order",
  "userId": "uuid-user",
  "status": "PENDING",
  "totalAmount": 299.97,
  "items": [...],
  "createdAt": "2024-01-01T10:00:00"
}
```

```http
GET /api/orders
Authorization: Bearer {token}
```
Obtiene todas las órdenes del usuario autenticado.

```http
GET /api/orders/{id}
Authorization: Bearer {token}
```
Obtiene una orden específica.

```http
PUT /api/orders/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "PROCESSING"
}
```

```http
DELETE /api/orders/{id}
Authorization: Bearer {token}
```
Cancela una orden (restaura el stock).

## 📊 Modelo de Datos

### Order

```java
- id: UUID
- userId: UUID
- status: OrderStatus (ENUM)
- totalAmount: BigDecimal
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- items: List<OrderItem>
```

**OrderStatus:**
- PENDING - Orden creada
- PROCESSING - En procesamiento
- SHIPPED - Enviada
- DELIVERED - Entregada
- CANCELLED - Cancelada

### OrderItem

```java
- id: UUID
- orderId: UUID
- productId: UUID
- productName: String
- quantity: Integer
- price: BigDecimal
```

## 🔄 Flujo de Creación de Orden

1. **Recibir request** con lista de productos
2. **Validar productos** consultando Product Service
3. **Verificar stock** disponible
4. **Actualizar stock** en Product Service (decrementar)
5. **Calcular total** de la orden
6. **Guardar orden** en base de datos
7. **Publicar evento** a Kafka topic `order-events`
8. **Retornar respuesta** al cliente

## 📤 Eventos Kafka

### Topic: `order-events`

**Eventos publicados:**

#### ORDER_CREATED
```json
{
  "eventType": "ORDER_CREATED",
  "orderId": "uuid",
  "userId": "uuid",
  "userEmail": "user@example.com",
  "username": "username",
  "status": "PENDING",
  "totalAmount": 299.97,
  "items": [...],
  "createdAt": "2024-01-01T10:00:00"
}
```

#### ORDER_UPDATED
```json
{
  "eventType": "ORDER_UPDATED",
  "orderId": "uuid",
  "status": "PROCESSING",
  "updatedAt": "2024-01-01T11:00:00"
}
```

#### ORDER_CANCELLED
```json
{
  "eventType": "ORDER_CANCELLED",
  "orderId": "uuid",
  "userId": "uuid",
  "cancelledAt": "2024-01-01T12:00:00"
}
```

## 🔗 Integración con Product Service

### Validar Producto

```http
GET http://product-service:8082/api/products/{id}
Authorization: Bearer {service-token}
```

### Actualizar Stock

```http
PUT http://product-service:8082/api/products/{id}/stock
Authorization: Bearer {service-token}
Content-Type: application/json

{
  "quantity": -5  # Decrementa 5 unidades
}
```

## 🧪 Testing

### Crear Orden

```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"productId": "uuid-1", "quantity": 2},
      {"productId": "uuid-2", "quantity": 1}
    ]
  }'
```

### Ver Órdenes

```bash
curl -X GET http://localhost:8083/api/orders \
  -H "Authorization: Bearer {token}"
```

### Cancelar Orden

```bash
curl -X DELETE http://localhost:8083/api/orders/{id} \
  -H "Authorization: Bearer {token}"
```

## 🐛 Troubleshooting

### Stock insuficiente

```json
{
  "error": "Insufficient stock for product",
  "productId": "uuid",
  "available": 5,
  "requested": 10
}
```

### Producto no encontrado

```json
{
  "error": "Product not found",
  "productId": "uuid"
}
```

### Kafka no disponible

Si Kafka no está disponible, la orden se crea pero el evento no se publica. Revisar logs.

## 📝 Notas

- Las órdenes canceladas restauran el stock automáticamente
- Los eventos se publican de forma asíncrona
- El cálculo del total incluye todos los items
- Se requiere autenticación JWT para todas las operaciones

## 🔗 Enlaces

- [Spring Kafka](https://spring.io/projects/spring-kafka)
- [Spring WebClient](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client)
- [Kafka UI](http://localhost:8090)
