# 📧 Notification Service

Servicio de notificaciones y auditoría para el sistema de e-commerce. Consume eventos de Kafka y envía notificaciones por email.

## 📋 Descripción

El Notification Service es responsable de:
- Consumir eventos de Kafka (topic: `order-events`)
- Enviar notificaciones por email (SMTP)
- Registrar auditoría de todos los eventos
- Tracking del estado de las notificaciones

## 🏗️ Arquitectura

### Capas

```
presentation/
├── controller/
│   ├── NotificationController.java
│   └── AuditController.java
│
application/
├── dto/
│   ├── OrderEventDTO.java
│   └── NotificationDTO.java
├── service/
│   ├── KafkaConsumerService.java
│   ├── EmailService.java
│   ├── NotificationService.java
│   └── AuditService.java
│
domain/
├── model/
│   ├── Notification.java
│   └── AuditLog.java
└── repository/
    ├── NotificationRepository.java
    └── AuditLogRepository.java
│
infrastructure/
└── config/
    ├── KafkaConfig.java
    ├── EmailConfig.java
    └── SecurityConfig.java
```

## 🚀 Tecnologías

- **Spring Boot 3.2.0**
- **Spring Kafka** - Consumer de eventos
- **Spring Mail** - Envío de emails
- **PostgreSQL** - Base de datos
- **Lombok** - Reducción de boilerplate

## ⚙️ Configuración

### Variables de Entorno

```yaml
# Database
spring.datasource.url=jdbc:postgresql://postgres-notification:5432/notification_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Kafka
spring.kafka.bootstrap-servers=kafka:9092
spring.kafka.consumer.group-id=notification-service-group

# Email (MailTrap)
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=${MAILTRAP_USERNAME}
spring.mail.password=${MAILTRAP_PASSWORD}

# JWT
jwt.secret=${JWT_SECRET}
```

### Kafka Consumer

**Topic:** `order-events`
**Group ID:** `notification-service-group`

**Eventos consumidos:**
- `ORDER_CREATED`
- `ORDER_UPDATED`
- `ORDER_CANCELLED`

## 📡 API Endpoints

### Notificaciones

```http
GET /api/notifications
```
Obtiene todas las notificaciones.

```http
GET /api/notifications/recipient/{email}
```
Obtiene notificaciones por destinatario.

```http
GET /api/notifications/entity/{entityType}/{entityId}
```
Obtiene notificaciones por entidad relacionada.

### Auditoría

```http
GET /api/audit
```
Obtiene todos los registros de auditoría.

```http
GET /api/audit/entity/{entityType}/{entityId}
```
Obtiene auditoría por entidad.

```http
GET /api/audit/user/{userId}
```
Obtiene auditoría por usuario.

## 🔄 Flujo de Procesamiento

1. **Kafka Consumer** escucha eventos del topic `order-events`
2. **Deserialización** del evento a `OrderEventDTO`
3. **Creación de registro de auditoría** en la base de datos
4. **Generación de email HTML** con los datos del evento
5. **Envío de email** vía SMTP (MailTrap)
6. **Actualización del estado** de la notificación (SENT/FAILED)

## 📊 Modelo de Datos

### Notification

```java
- id: Long
- recipient: String
- subject: String
- body: String (TEXT)
- type: NotificationType (ENUM)
- status: NotificationStatus (ENUM)
- errorMessage: String
- relatedEntityId: Long
- relatedEntityType: String
- createdAt: LocalDateTime
- sentAt: LocalDateTime
```

**NotificationType:**
- ORDER_CREATED
- ORDER_UPDATED
- ORDER_CANCELLED
- PAYMENT_CONFIRMED
- SHIPMENT_UPDATED
- GENERAL

**NotificationStatus:**
- PENDING
- SENT
- FAILED

### AuditLog

```java
- id: Long
- entityType: String
- entityId: Long
- userId: Long
- username: String
- action: String
- details: String (TEXT)
- ipAddress: String
- status: String
- timestamp: LocalDateTime
```

## 🧪 Testing

### Verificar Kafka Consumer

```bash
# Ver logs del servicio
docker-compose logs -f notification-service

# Publicar un evento de prueba
docker exec -it proyecto-final-kafka-1 kafka-console-producer --topic order-events --bootstrap-server localhost:9092
```

### Verificar Emails

1. Accede a [MailTrap](https://mailtrap.io)
2. Revisa la bandeja de entrada
3. Verifica que los emails lleguen correctamente

### Verificar Base de Datos

```sql
-- Ver notificaciones
SELECT * FROM notifications ORDER BY created_at DESC;

-- Ver auditoría
SELECT * FROM audit_logs ORDER BY timestamp DESC;

-- Contar notificaciones por estado
SELECT status, COUNT(*) FROM notifications GROUP BY status;
```

## 🐛 Troubleshooting

### Notificaciones no se envían

1. Verificar configuración de MailTrap
2. Revisar logs del servicio
3. Verificar que Kafka esté corriendo
4. Comprobar que el topic `order-events` existe

### Kafka Consumer no consume eventos

1. Verificar que Kafka esté corriendo
2. Comprobar el `group-id` del consumer
3. Revisar la configuración de deserialización
4. Ver logs de Kafka

## 📝 Notas

- Las notificaciones se almacenan en la base de datos antes de enviarse
- Si el envío falla, el estado se actualiza a FAILED
- Todos los eventos se registran en la tabla de auditoría
- Los emails se envían en formato HTML

## 🔗 Enlaces

- [Documentación de Spring Kafka](https://spring.io/projects/spring-kafka)
- [MailTrap](https://mailtrap.io)
- [Kafka UI](http://localhost:8090)
