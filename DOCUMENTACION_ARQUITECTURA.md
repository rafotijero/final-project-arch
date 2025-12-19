# Documentación de Arquitectura
## E-Commerce Microservices Platform

---

## 1. Introducción General

### 1.1 Propósito del documento

Este documento describe la arquitectura del sistema **E-Commerce Microservices Platform**, una plataforma de comercio electrónico distribuida diseñada con arquitectura de microservicios. El objetivo principal es proporcionar un sistema escalable, resiliente y mantenible que soporte operaciones de comercio electrónico incluyendo gestión de usuarios, catálogo de productos, procesamiento de órdenes y notificaciones.

El sistema está diseñado para:
- **Escalabilidad horizontal:** Cada servicio puede escalar independientemente según la demanda
- **Resiliencia:** Aislamiento de fallos mediante microservicios independientes
- **Mantenibilidad:** Separación clara de responsabilidades y tecnologías modernas
- **Extensibilidad:** Arquitectura que facilita la adición de nuevas funcionalidades

### 1.2 Alcance del sistema

El sistema implementa las siguientes capacidades:

**Funcionalidades Core:**
- **Gestión de Usuarios:** Registro, autenticación multi-factor (MFA), OAuth2 (Google, GitHub)
- **Catálogo de Productos:** CRUD de productos y categorías, gestión de inventario
- **Procesamiento de Órdenes:** Creación de órdenes, validación de stock, gestión de estados
- **Notificaciones:** Envío de emails transaccionales, registro de auditoría

**Interfaces de Usuario:**
- **Frontend Web:** Aplicación React SPA con interfaz moderna y responsiva
- **APIs REST:** Endpoints documentados para cada microservicio

**Servicios Backend:**
- Auth Service (Puerto 8081)
- Product Service (Puerto 8082)
- Order Service (Puerto 8083)
- Notification Service (Puerto 8084)

### 1.3 Audiencia y nivel técnico esperado

Este documento está dirigido a:

**Desarrolladores Backend:**
- Arquitectos de software que necesitan entender las decisiones de diseño
- Desarrolladores Java/Spring Boot que trabajarán en los microservicios
- Nivel técnico: Intermedio a Avanzado

**Desarrolladores Frontend:**
- Desarrolladores React que consumirán las APIs REST
- Nivel técnico: Intermedio

**Equipo DevOps:**
- Ingenieros de infraestructura responsables del despliegue y monitoreo
- Administradores de sistemas que gestionarán la infraestructura
- Nivel técnico: Avanzado

**Stakeholders Técnicos:**
- Product Managers con conocimientos técnicos
- Tech Leads y CTOs
- Nivel técnico: Básico a Intermedio

---

## 2. Visión Arquitectónica General

### 2.1 Estilo arquitectónico utilizado

El sistema implementa una **Arquitectura de Microservicios** con los siguientes patrones:

**Estilo Principal: Microservicios**
- 4 servicios independientes con bases de datos dedicadas
- Cada servicio es desplegable y escalable de forma autónoma
- Comunicación mediante protocolos estándar (REST, Kafka)

**Métodos de Comunicación:**

1. **Comunicación Síncrona (REST/HTTP):**
   - Frontend → Microservicios: APIs REST
   - Microservicio → Microservicio: HTTP con WebClient
   - Autenticación: JWT Bearer tokens

2. **Comunicación Asíncrona (Event-Driven):**
   - Message Broker: Apache Kafka 7.5.0
   - Order Service publica eventos de órdenes
   - Notification Service consume eventos para enviar notificaciones
   - Patrón: Publish-Subscribe

**Patrones Arquitectónicos Implementados:**
- ✅ Database per Service
- ✅ Event-Driven Architecture
- ✅ API Gateway Pattern (parcial - sin gateway dedicado)
- ✅ Strangler Fig Pattern (preparado para migración gradual)
- ✅ Circuit Breaker (preparado, no implementado)

### 2.2 Decisiones arquitectónicas clave

**ADR-001: Arquitectura de Microservicios**
- **Decisión:** Implementar arquitectura de microservicios en lugar de monolito
- **Justificación:** Permite escalabilidad independiente, despliegues aislados y tecnologías específicas por servicio
- **Consecuencias:** Mayor complejidad operacional, necesidad de orquestación y monitoreo distribuido

**ADR-002: Database per Service Pattern**
- **Decisión:** Cada microservicio tiene su propia base de datos PostgreSQL
- **Justificación:** Aislamiento de datos, autonomía de servicios, prevención de acoplamiento
- **Consecuencias:** Necesidad de gestionar consistencia eventual, complejidad en transacciones distribuidas

**ADR-003: Apache Kafka para Eventos Asíncronos**
- **Decisión:** Utilizar Kafka como message broker para comunicación asíncrona
- **Justificación:** Alta throughput, persistencia de eventos, escalabilidad horizontal
- **Consecuencias:** Complejidad adicional en infraestructura, necesidad de gestionar offsets y consumer groups

**ADR-004: JWT para Autenticación**
- **Decisión:** Tokens JWT stateless compartidos entre servicios
- **Justificación:** Escalabilidad sin estado de sesión, validación descentralizada
- **Consecuencias:** Tokens no revocables hasta expiración, necesidad de rotación de secrets

**ADR-005: OAuth2 para Login Social**
- **Decisión:** Integración con Google y GitHub OAuth2
- **Justificación:** Mejora experiencia de usuario, reduce fricción en registro
- **Consecuencias:** Dependencia de proveedores externos, gestión de múltiples flujos de autenticación

**ADR-006: Multi-Factor Authentication (MFA)**
- **Decisión:** Implementar MFA opcional con TOTP
- **Justificación:** Aumenta seguridad de cuentas de usuario
- **Consecuencias:** Mayor complejidad en flujo de autenticación, mejor seguridad

**ADR-007: Docker y Docker Compose**
- **Decisión:** Contenedorización con Docker, orquestación local con Docker Compose
- **Justificación:** Portabilidad, consistencia entre ambientes, facilita desarrollo local
- **Consecuencias:** Preparado para Kubernetes, pero requiere migración futura

### 2.3 Diagramas de alto nivel

**Diagrama de Contexto (C4 - Nivel 1):**

```
┌─────────────────────────────────────────────────────────┐
│                    Usuario Final                        │
│              (Navegador Web / Mobile)                   │
└────────────────────┬────────────────────────────────────┘
                     │ HTTPS
                     ▼
┌─────────────────────────────────────────────────────────┐
│         E-Commerce Microservices Platform               │
│                                                         │
│  • Autenticación y Autorización                        │
│  • Gestión de Productos                                │
│  • Procesamiento de Órdenes                            │
│  • Notificaciones por Email                            │
└──────┬──────────────────────┬──────────────────────────┘
       │                      │
       ▼                      ▼
┌──────────────┐      ┌──────────────────┐
│   Google     │      │   GitHub         │
│   OAuth2     │      │   OAuth2         │
└──────────────┘      └──────────────────┘
       │                      │
       ▼                      ▼
┌──────────────────────────────────────┐
│        MailTrap SMTP                 │
│    (Servicio de Email)               │
└──────────────────────────────────────┘
```

**Diagrama de Contenedores (C4 - Nivel 2):**

```
┌─────────────────────────────────────────────────────────────┐
│                   Frontend Application                      │
│                    React 18 + Vite                          │
│                   Puerto: 3000 (Nginx)                      │
└──────────────┬──────────────────────────────────────────────┘
               │ HTTP/REST + JWT
    ┌──────────┼──────────┬──────────┬──────────┐
    │          │          │          │          │
┌───▼────┐ ┌──▼─────┐ ┌──▼──────┐ ┌─▼─────────┐
│ Auth   │ │Product │ │ Order   │ │Notification│
│Service │ │Service │ │Service  │ │  Service  │
│:8081   │ │:8082   │ │:8083    │ │  :8084    │
│        │ │        │ │         │ │           │
│Spring  │ │Spring  │ │Spring   │ │  Spring   │
│Boot    │ │Boot    │ │Boot     │ │  Boot     │
└───┬────┘ └───┬────┘ └───┬─────┘ └─────┬─────┘
    │          │          │             │
    │          │          │ Kafka       │
    │          │          │ Events      │
    │          │          └─────────────►
    │          │                        │
┌───▼────┐ ┌──▼─────┐ ┌──▼──────┐ ┌───▼───────┐
│Postgres│ │Postgres│ │Postgres │ │ Postgres  │
│auth_db │ │product │ │order_db │ │notification│
│:5432   │ │_db     │ │:5434    │ │_db :5435  │
└────────┘ │:5433   │ └─────────┘ └───────────┘
           └────────┘
                │
        ┌───────▼────────┐
        │ Apache Kafka   │
        │   + Zookeeper  │
        │   :9092, :2181 │
        └────────────────┘
```

---

## 3. Componentes del Sistema

### 3.1 Módulos principales y responsabilidades

#### **Auth Service** (Puerto 8081)

**Responsabilidades:**
- Registro y autenticación de usuarios
- Gestión de sesiones con JWT
- Integración OAuth2 (Google, GitHub)
- Multi-Factor Authentication (MFA/TOTP)
- Gestión de roles y permisos (RBAC)

**Tecnologías:**
- Spring Boot 3.2.0
- Spring Security
- PostgreSQL (auth_db)
- JWT (jjwt library)
- TOTP (Google Authenticator compatible)

**Endpoints Principales:**
- `POST /api/auth/register` - Registro de usuarios
- `POST /api/auth/login` - Autenticación
- `POST /api/auth/mfa/setup` - Configurar MFA
- `POST /api/auth/mfa/enable` - Activar MFA
- `POST /api/auth/verify-mfa` - Verificar código MFA
- `GET /api/auth/me` - Obtener usuario actual

#### **Product Service** (Puerto 8082)

**Responsabilidades:**
- CRUD de productos
- Gestión de categorías
- Control de inventario y stock
- Búsqueda y filtrado de productos
- Actualización de stock (llamado por Order Service)

**Tecnologías:**
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL (product_db)
- WebClient (comunicación HTTP)

**Endpoints Principales:**
- `GET /api/products` - Listar productos
- `POST /api/products` - Crear producto
- `PUT /api/products/{id}` - Actualizar producto
- `DELETE /api/products/{id}` - Eliminar producto
- `GET /api/categories` - Listar categorías

#### **Order Service** (Puerto 8083)

**Responsabilidades:**
- Creación y gestión de órdenes
- Validación de stock con Product Service
- Gestión de estados de orden (PENDING, CONFIRMED, CANCELLED)
- Publicación de eventos a Kafka
- Cálculo de totales y validación de datos

**Tecnologías:**
- Spring Boot 3.2.0
- Spring Kafka (Producer)
- PostgreSQL (order_db)
- WebClient (comunicación con Product Service)

**Endpoints Principales:**
- `POST /api/orders` - Crear orden
- `GET /api/orders` - Listar órdenes del usuario
- `GET /api/orders/{id}` - Obtener detalle de orden
- `PUT /api/orders/{id}/status` - Actualizar estado

**Eventos Publicados:**
- `order.created` - Orden creada exitosamente
- `order.confirmed` - Orden confirmada
- `order.cancelled` - Orden cancelada

#### **Notification Service** (Puerto 8084)

**Responsabilidades:**
- Consumo de eventos de Kafka
- Envío de notificaciones por email (SMTP)
- Registro de auditoría de eventos
- Tracking de notificaciones enviadas
- Gestión de templates de email

**Tecnologías:**
- Spring Boot 3.2.0
- Spring Kafka (Consumer)
- Spring Mail (SMTP)
- PostgreSQL (notification_db)
- MailTrap (desarrollo)

**Endpoints Principales:**
- `GET /api/notifications` - Listar notificaciones
- `GET /api/audit` - Consultar auditoría de eventos

**Eventos Consumidos:**
- `order.created` - Envía email de confirmación de orden
- `order.confirmed` - Envía email de orden confirmada
- `order.cancelled` - Envía email de orden cancelada

### 3.2 Interfaces y APIs expuestas

**Protocolo Principal: REST/HTTP**

Todos los servicios exponen APIs RESTful siguiendo convenciones estándar:
- Métodos HTTP: GET, POST, PUT, DELETE
- Content-Type: `application/json`
- Autenticación: `Authorization: Bearer <JWT>`
- Códigos de estado HTTP estándar (200, 201, 400, 401, 404, 500)

**Documentación de APIs:**
- ⚠️ **No implementado:** OpenAPI/Swagger
- ✅ **Disponible:** READMEs por servicio con ejemplos de endpoints
- **Recomendación:** Implementar Swagger UI para documentación interactiva

**Formato de Respuestas:**

```json
// Respuesta exitosa
{
  "id": 1,
  "email": "user@example.com",
  "username": "john_doe"
}

// Respuesta de error
{
  "timestamp": "2025-12-18T20:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid email format",
  "path": "/api/auth/register"
}
```

**Autenticación de APIs:**

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "user": { ... }
}

// Uso del token
GET /api/products
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### 3.3 Comunicación entre componentes

**Comunicación Síncrona (HTTP/REST):**

**Order Service → Product Service:**
```java
// Validación de stock antes de crear orden
WebClient productClient;
ProductDTO product = productClient.get()
    .uri("/api/products/{id}", productId)
    .header("Authorization", "Bearer " + jwt)
    .retrieve()
    .bodyToMono(ProductDTO.class)
    .block();
```

**Frontend → Microservicios:**
```javascript
// Axios con interceptor para JWT
axios.get('http://localhost:8082/api/products', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

**Comunicación Asíncrona (Kafka):**

**Order Service (Producer):**
```java
@Service
public class OrderEventPublisher {
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    public void publishOrderCreated(Order order) {
        OrderEvent event = new OrderEvent(
            order.getId(),
            order.getUserId(),
            order.getTotalAmount(),
            "ORDER_CREATED"
        );
        kafkaTemplate.send("order-events", event);
    }
}
```

**Notification Service (Consumer):**
```java
@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void consumeOrderEvent(OrderEvent event) {
        // Enviar email de notificación
        emailService.sendOrderConfirmation(event);
    }
}
```

**Configuración de Kafka:**
```yaml
# Order Service (Producer)
spring:
  kafka:
    bootstrap-servers: kafka:29092

# Notification Service (Consumer)
spring:
  kafka:
    bootstrap-servers: kafka:29092
    consumer:
      group-id: notification-service-group
      auto-offset-reset: earliest
```

### 3.4 Integración con sistemas externos

**Proveedores OAuth2:**

**Google OAuth2:**
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: profile, email
            redirect-uri: "{baseUrl}/login/oauth2/code/google"
```

**GitHub OAuth2:**
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope: read:user, user:email
            redirect-uri: "{baseUrl}/login/oauth2/code/github"
```

**Servicio de Email (MailTrap):**
```yaml
spring:
  mail:
    host: sandbox.smtp.mailtrap.io
    port: 2525
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

**Integraciones Externas:**
- ✅ Google OAuth2 - Autenticación social
- ✅ GitHub OAuth2 - Autenticación social
- ✅ MailTrap SMTP - Envío de emails (desarrollo)
- 📋 **Futuro:** Pasarelas de pago (Stripe, PayPal)
- 📋 **Futuro:** Servicios de almacenamiento (AWS S3)

---

## 4. Detalle del Estilo Arquitectónico

### 4.2 Arquitectura de Microservicios

**Stack Tecnológico Backend:**

| Componente | Tecnología | Versión |
|------------|-----------|---------|
| Framework | Spring Boot | 3.2.0 |
| Lenguaje | Java | 17 (LTS) |
| Build Tool | Maven | 3.x |
| Base de Datos | PostgreSQL | 15-alpine |
| Message Broker | Apache Kafka | 7.5.0 (Confluent) |
| Seguridad | Spring Security | 6.x |
| ORM | Hibernate/JPA | 6.x |
| Validación | Jakarta Validation | 3.x |

**Bases de Datos (PostgreSQL):**

Cada microservicio tiene su propia instancia de PostgreSQL:

```yaml
# Auth Database
postgres-auth:
  image: postgres:15-alpine
  ports: "5432:5432"
  database: auth_db
  
# Product Database
postgres-product:
  image: postgres:15-alpine
  ports: "5433:5432"
  database: product_db
  
# Order Database
postgres-order:
  image: postgres:15-alpine
  ports: "5434:5432"
  database: order_db
  
# Notification Database
postgres-notification:
  image: postgres:15-alpine
  ports: "5435:5432"
  database: notification_db
```

**Configuración JPA:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Desarrollo: update, Producción: validate
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
```

**Message Broker (Apache Kafka):**

```yaml
# Kafka + Zookeeper
zookeeper:
  image: confluentinc/cp-zookeeper:7.5.0
  ports: "2181:2181"

kafka:
  image: confluentinc/cp-kafka:7.5.0
  ports: "9092:9092"
  environment:
    KAFKA_BROKER_ID: 1
    KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    KAFKA_ADVERTISED_LISTENERS: 
      - PLAINTEXT://kafka:29092
      - PLAINTEXT_HOST://localhost:9092
    KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

# Kafka UI (Monitoreo)
kafka-ui:
  image: provectuslabs/kafka-ui:latest
  ports: "8090:8080"
```

**API Gateway:**

⚠️ **No Implementado Actualmente**

**Estado Actual:**
- Frontend se comunica directamente con cada microservicio
- CORS configurado individualmente en cada servicio
- No hay punto de entrada único

**Recomendación:**
Implementar Spring Cloud Gateway:

```yaml
# Configuración sugerida
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**, /api/categories/**
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
```

**Frontend Stack:**

| Componente | Tecnología | Versión |
|------------|-----------|---------|
| Framework | React | 18.x |
| Build Tool | Vite | 5.x |
| HTTP Client | Axios | 1.x |
| Routing | React Router | 6.x |
| Styling | Tailwind CSS | 3.x |
| Server (Prod) | Nginx | Latest |

**Orquestación:**

```yaml
# Docker Compose
version: '3.8'

services: 11 servicios
  - 4 microservicios (auth, product, order, notification)
  - 4 bases de datos PostgreSQL
  - 1 Kafka + Zookeeper
  - 1 Kafka UI
  - 1 Frontend React

networks:
  - ecommerce-network (bridge)

volumes:
  - auth-data
  - product-data
  - order-data
  - notification-data
```

---

## 5. Seguridad

### 5.1 Autenticación y autorización

**Mecanismos de Autenticación:**

**1. Autenticación Tradicional (Email/Password):**

```java
// Flujo de autenticación
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}

// Si MFA está deshabilitado
Response: {
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "mfaRequired": false,
  "user": { ... }
}

// Si MFA está habilitado
Response: {
  "mfaRequired": true,
  "sessionToken": "temp_token_for_mfa"
}

// Verificación MFA
POST /api/auth/verify-mfa
{
  "sessionToken": "temp_token_for_mfa",
  "mfaCode": "123456"
}
```

**2. OAuth2 (Google y GitHub):**

```
Flujo OAuth2:
1. Usuario → Click "Login with Google"
2. Frontend → Redirect a Google OAuth
3. Google → Callback a Auth Service
4. Auth Service → Crea/actualiza usuario
5. Si MFA habilitado → Retorna session token temporal
6. Usuario → Ingresa código MFA
7. Auth Service → Retorna JWT completo
```

**3. Multi-Factor Authentication (TOTP):**

```java
// Configurar MFA
POST /api/auth/mfa/setup
Authorization: Bearer <jwt>

Response: {
  "qrCodeUrl": "data:image/png;base64,...",
  "secret": "JBSWY3DPEHPK3PXP",
  "manualEntryKey": "JBSW Y3DP EHPK 3PXP"
}

// Activar MFA
POST /api/auth/mfa/enable
{
  "code": "123456"
}
```

**JWT (JSON Web Tokens):**

```yaml
# Configuración
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000  # 24 horas en milisegundos
```

```java
// Estructura del JWT
{
  "sub": "user@example.com",
  "role": "USER",
  "userId": "123",
  "iat": 1703001234,
  "exp": 1703087634
}
```

**Autorización (RBAC):**

```java
// Roles definidos
public enum Role {
    USER,
    ADMIN
}

// Configuración de seguridad
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
    }
}
```

### 5.2 Gestión de secretos y cifrado

**Gestión de Secretos:**

**Variables de Entorno:**
```bash
# .env (excluido de Git)
GOOGLE_CLIENT_ID=xxx.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=GOCSPX-xxx
GITHUB_CLIENT_ID=xxx
GITHUB_CLIENT_SECRET=xxx
JWT_SECRET=mi-super-secreto-cambiar-en-produccion
MAIL_USERNAME=xxx
MAIL_PASSWORD=xxx
```

**Configuración en Servicios:**
```yaml
# application.yml
jwt:
  secret: ${JWT_SECRET:default-secret-only-for-dev}

spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
```

**Cifrado de Datos:**

**Passwords:**
```java
// BCrypt para hashing de contraseñas
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Almacenamiento
String hashedPassword = passwordEncoder.encode(rawPassword);
// Resultado: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

**JWT Signing:**
```java
// Firma HMAC-SHA256
String jwt = Jwts.builder()
    .setSubject(email)
    .claim("role", role)
    .signWith(SignatureAlgorithm.HS256, jwtSecret)
    .compact();
```

**Comunicaciones:**
- ⚠️ **HTTP en desarrollo:** Comunicación sin cifrar entre servicios
- 📋 **Recomendación producción:** HTTPS/TLS para todas las comunicaciones
- 📋 **Recomendación avanzada:** mTLS para comunicación inter-servicios

**Gestión de Secretos en Producción:**
```yaml
# Recomendaciones
Opciones:
  - HashiCorp Vault
  - AWS Secrets Manager
  - Azure Key Vault
  - Kubernetes Secrets (con encriptación)
```

### 5.3 Políticas de acceso

**Control de Acceso Basado en Roles (RBAC):**

**Matriz de Permisos:**

| Recurso | Endpoint | USER | ADMIN |
|---------|----------|------|-------|
| Registro | POST /api/auth/register | ✅ Público | ✅ Público |
| Login | POST /api/auth/login | ✅ Público | ✅ Público |
| Perfil | GET /api/auth/me | ✅ Propio | ✅ Todos |
| Productos (Listar) | GET /api/products | ✅ Sí | ✅ Sí |
| Productos (Crear) | POST /api/products | ❌ No | ✅ Sí |
| Productos (Actualizar) | PUT /api/products/{id} | ❌ No | ✅ Sí |
| Productos (Eliminar) | DELETE /api/products/{id} | ❌ No | ✅ Sí |
| Órdenes (Crear) | POST /api/orders | ✅ Sí | ✅ Sí |
| Órdenes (Listar) | GET /api/orders | ✅ Propias | ✅ Todas |
| Notificaciones | GET /api/notifications | ✅ Propias | ✅ Todas |

**Políticas de Red (Docker):**

```yaml
# Red aislada para microservicios
networks:
  ecommerce-network:
    driver: bridge

# Solo servicios en la red pueden comunicarse
services:
  auth-service:
    networks:
      - ecommerce-network
```

**CORS (Cross-Origin Resource Sharing):**

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        return source;
    }
}
```

**Políticas de Seguridad Adicionales:**
- ✅ CSRF protection deshabilitado (API stateless)
- ✅ XSS protection mediante validación de inputs
- ✅ SQL Injection prevention (JPA/Hibernate)
- ⚠️ Rate limiting no implementado
- ⚠️ IP whitelisting no implementado

---

## 6. Escalabilidad y Rendimiento

### 6.1 Estrategias de escalabilidad

**Escalabilidad Horizontal:**

**Estado Actual:**
```yaml
# Servicios stateless preparados para escalar
Características:
  - ✅ Sin estado de sesión (JWT stateless)
  - ✅ Base de datos por servicio
  - ✅ Contenedores Docker
  - ⚠️ No configurado auto-scaling
```

**Preparación para Kubernetes:**

```yaml
# Ejemplo de HPA (Horizontal Pod Autoscaler)
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: product-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: product-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

**Escalabilidad de Base de Datos:**

```yaml
# Estrategias recomendadas
PostgreSQL:
  - Read Replicas para consultas
  - Connection Pooling (HikariCP)
  - Índices optimizados
  - Particionamiento de tablas grandes

Kafka:
  - Múltiples particiones por topic
  - Replication factor > 1
  - Consumer groups para paralelismo
```

**Caché (No Implementado):**

```yaml
# Recomendación: Redis
Casos de uso:
  - Caché de productos frecuentes
  - Sesiones de usuario (alternativa a JWT)
  - Rate limiting
  - Caché de resultados de búsqueda
```

### 6.2 Balanceo de carga

**Estado Actual:**
⚠️ **No implementado** - Frontend se conecta directamente a servicios

**Recomendaciones:**

**Opción 1: Nginx como Load Balancer**
```nginx
upstream auth-service {
    server auth-service-1:8081;
    server auth-service-2:8081;
    server auth-service-3:8081;
}

server {
    listen 80;
    location /api/auth/ {
        proxy_pass http://auth-service;
    }
}
```

**Opción 2: Kubernetes Service**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: product-service
spec:
  type: LoadBalancer
  selector:
    app: product-service
  ports:
    - port: 80
      targetPort: 8082
```

**Opción 3: Spring Cloud Gateway + Eureka**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://product-service  # Load balanced
          predicates:
            - Path=/api/products/**
```

### 6.3 Tolerancia a fallos y alta disponibilidad

**Mecanismos Implementados:**

**Healthchecks:**
```yaml
# PostgreSQL
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U postgres"]
  interval: 10s
  timeout: 5s
  retries: 5

# Kafka
healthcheck:
  test: ["CMD", "kafka-broker-api-versions", "--bootstrap-server", "localhost:9092"]
  interval: 10s
  timeout: 10s
  retries: 5
```

**Dependencias de Servicios:**
```yaml
order-service:
  depends_on:
    postgres-order:
      condition: service_healthy
    kafka:
      condition: service_healthy
```

**Mecanismos No Implementados (Recomendados):**

**Circuit Breaker (Resilience4j):**
```java
@CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
public ProductDTO getProduct(Long id) {
    return productClient.get()
        .uri("/api/products/{id}", id)
        .retrieve()
        .bodyToMono(ProductDTO.class)
        .block();
}

public ProductDTO getProductFallback(Long id, Exception e) {
    return ProductDTO.builder()
        .id(id)
        .name("Product temporarily unavailable")
        .build();
}
```

**Retry Policy:**
```yaml
resilience4j:
  retry:
    instances:
      productService:
        maxAttempts: 3
        waitDuration: 1000ms
```

**Alta Disponibilidad:**

```yaml
# Configuración recomendada para producción
Componentes:
  Microservicios:
    - Mínimo 2 réplicas por servicio
    - Despliegue en múltiples zonas de disponibilidad
    
  Bases de Datos:
    - PostgreSQL con replicación master-slave
    - Backups automatizados cada 6 horas
    - Point-in-time recovery habilitado
    
  Kafka:
    - Cluster de 3+ brokers
    - Replication factor: 3
    - Min in-sync replicas: 2
    
  Load Balancers:
    - Balanceadores redundantes
    - Health checks activos
```

---

## 7. DevOps y Despliegue

### 7.1 Estrategia de CI/CD

**Estado Actual:**
⚠️ **No implementado** - Despliegue manual con Docker Compose

**Pipeline CI/CD Recomendado:**

```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          
      - name: Build with Maven
        run: mvn clean package -DskipTests
        
      - name: Run Unit Tests
        run: mvn test
        
      - name: Run Integration Tests
        run: mvn verify
        
      - name: SonarQube Analysis
        run: mvn sonar:sonar
        
  docker:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Build Docker Images
        run: docker-compose build
        
      - name: Push to Docker Registry
        run: |
          docker tag auth-service:latest registry.io/auth-service:${{ github.sha }}
          docker push registry.io/auth-service:${{ github.sha }}
          
  deploy-staging:
    needs: docker
    if: github.ref == 'refs/heads/develop'
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to Staging
        run: kubectl apply -f k8s/staging/
        
  deploy-production:
    needs: docker
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    environment: production
    steps:
      - name: Deploy to Production
        run: kubectl apply -f k8s/production/
```

**Etapas del Pipeline:**
1. **Build:** Compilación de servicios Java
2. **Test:** Tests unitarios y de integración
3. **Quality:** Análisis de código (SonarQube)
4. **Package:** Construcción de imágenes Docker
5. **Deploy:** Despliegue a ambientes (staging/prod)

### 7.2 Infraestructura como código

**Implementado:**

**Docker Compose (IaC Local):**
```yaml
# docker-compose.yml
version: '3.8'

services:
  # 4 microservicios
  auth-service:
    build: ./auth-service
    ports: ["8081:8081"]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-auth:5432/auth_db
    depends_on:
      postgres-auth:
        condition: service_healthy
        
  # 4 bases de datos PostgreSQL
  # 1 Kafka + Zookeeper
  # 1 Kafka UI
  # 1 Frontend React

volumes:
  auth-data:
  product-data:
  order-data:
  notification-data:

networks:
  ecommerce-network:
    driver: bridge
```

**Dockerfiles:**
```dockerfile
# Ejemplo: auth-service/Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**No Implementado (Recomendado):**

**Kubernetes Manifests:**
```yaml
# k8s/auth-service/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
    spec:
      containers:
      - name: auth-service
        image: registry.io/auth-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: auth-config
              key: database-url
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: auth-secrets
              key: jwt-secret
```

**Terraform (Infraestructura Cloud):**
```hcl
# terraform/main.tf
resource "aws_eks_cluster" "ecommerce" {
  name     = "ecommerce-cluster"
  role_arn = aws_iam_role.cluster.arn
  
  vpc_config {
    subnet_ids = aws_subnet.private[*].id
  }
}

resource "aws_rds_instance" "auth_db" {
  identifier        = "auth-db"
  engine            = "postgres"
  engine_version    = "15"
  instance_class    = "db.t3.micro"
  allocated_storage = 20
  
  multi_az = true
  backup_retention_period = 7
}
```

### 7.3 Ambientes de despliegue

**Estado Actual:**
⚠️ **Solo ambiente de desarrollo local**

**Ambiente de Desarrollo:**
```yaml
Características:
  - Docker Compose
  - Bases de datos locales
  - Kafka local
  - Hot reload (Spring DevTools)
  - Logs en consola
  - show-sql: true
```

**Ambientes Recomendados:**

**Development (Local):**
```yaml
Propósito: Desarrollo local
Infraestructura: Docker Compose
Datos: Datos de prueba
Configuración:
  - ddl-auto: update
  - show-sql: true
  - log-level: DEBUG
```

**Staging:**
```yaml
Propósito: Testing pre-producción
Infraestructura: Kubernetes cluster
Datos: Copia sanitizada de producción
Configuración:
  - ddl-auto: validate
  - show-sql: false
  - log-level: INFO
  - Réplicas: 2 por servicio
  - Auto-scaling habilitado
```

**Production:**
```yaml
Propósito: Ambiente productivo
Infraestructura: Kubernetes cluster (multi-AZ)
Datos: Datos reales
Configuración:
  - ddl-auto: validate
  - show-sql: false
  - log-level: WARN
  - Réplicas: 3+ por servicio
  - Auto-scaling habilitado
  - Backups automatizados
  - Monitoreo 24/7
  - SSL/TLS habilitado
```

**Flujo de Despliegue:**
```
Developer → Commit → CI Pipeline
                       ↓
                    Build & Test
                       ↓
                  Docker Registry
                       ↓
              ┌────────┴────────┐
              ↓                 ↓
          Staging           Production
         (Automático)      (Manual Approval)
```

---

## 8. Calidad y Mantenibilidad

### 8.1 Estrategias de pruebas

**Estado Actual:**
⚠️ **No se encontraron tests automatizados**

**Estrategia de Testing Recomendada:**

**Pirámide de Testing:**

```
        ╱╲
       ╱E2E╲         10% - Tests End-to-End
      ╱──────╲
     ╱ Integr ╲      20% - Tests de Integración
    ╱──────────╲
   ╱  Unitarios ╲    70% - Tests Unitarios
  ╱──────────────╲
```

**1. Tests Unitarios (JUnit 5 + Mockito):**

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AuthService authService;
    
    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        RegisterRequest request = new RegisterRequest("user@example.com", "password123");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        
        // When
        authService.register(request);
        
        // Then
        verify(userRepository).save(any(User.class));
    }
}
```

**2. Tests de Integración (Spring Boot Test + Testcontainers):**

```java
@SpringBootTest
@Testcontainers
class OrderServiceIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
    
    @Autowired
    private OrderService orderService;
    
    @Test
    void shouldCreateOrderAndPublishEvent() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(...);
        
        // When
        Order order = orderService.createOrder(request);
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        // Verificar que el evento fue publicado a Kafka
    }
}
```

**3. Tests de Contrato (Spring Cloud Contract):**

```groovy
Contract.make {
    description "should return product by id"
    request {
        method GET()
        url("/api/products/1")
        headers {
            header("Authorization", "Bearer token")
        }
    }
    response {
        status 200
        body([
            id: 1,
            name: "Product 1",
            price: 99.99
        ])
        headers {
            contentType(applicationJson())
        }
    }
}
```

**4. Tests E2E (Cypress para Frontend):**

```javascript
describe('Order Flow', () => {
  it('should create order successfully', () => {
    cy.login('user@example.com', 'password123');
    cy.visit('/products');
    cy.get('[data-testid="product-1"]').click();
    cy.get('[data-testid="add-to-cart"]').click();
    cy.get('[data-testid="checkout"]').click();
    cy.get('[data-testid="confirm-order"]').click();
    cy.contains('Order created successfully').should('be.visible');
  });
});
```

**Cobertura de Código:**
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

### 8.2 Observabilidad

**Estado Actual:**
⚠️ **Solo logs básicos** - `show-sql: true` en JPA

**Stack de Observabilidad Recomendado:**

**1. Logging (ELK Stack):**

```yaml
# docker-compose.yml (agregado)
elasticsearch:
  image: elasticsearch:8.11.0
  ports: ["9200:9200"]
  
logstash:
  image: logstash:8.11.0
  ports: ["5000:5000"]
  
kibana:
  image: kibana:8.11.0
  ports: ["5601:5601"]
```

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"service":"auth-service"}</customFields>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="JSON"/>
    </root>
</configuration>
```

**2. Métricas (Prometheus + Grafana):**

```yaml
# Prometheus
prometheus:
  image: prom/prometheus:latest
  ports: ["9090:9090"]
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml

# Grafana
grafana:
  image: grafana/grafana:latest
  ports: ["3001:3000"]
```

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

**Métricas Clave:**
- Request rate (requests/second)
- Response time (p50, p95, p99)
- Error rate (%)
- JVM metrics (heap, GC, threads)
- Database connection pool
- Kafka consumer lag

**3. Tracing Distribuido (Jaeger):**

```yaml
jaeger:
  image: jaegertracing/all-in-one:latest
  ports:
    - "16686:16686"  # UI
    - "14268:14268"  # Collector
```

```xml
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
</dependency>
```

**4. Dashboards Sugeridos:**

```yaml
Grafana Dashboards:
  - JVM Metrics (heap, GC, threads)
  - HTTP Metrics (latency, throughput, errors)
  - Database Metrics (connections, query time)
  - Kafka Metrics (consumer lag, throughput)
  - Business Metrics (orders/hour, revenue)
```

**Kafka UI (Implementado):**
```yaml
kafka-ui:
  image: provectuslabs/kafka-ui:latest
  ports: ["8090:8080"]
  # Permite monitorear:
  # - Topics y particiones
  # - Consumer groups y lag
  # - Mensajes en topics
  # - Broker health
```

### 8.3 Gestión de deuda técnica

**Estado Actual:**
⚠️ **No hay procesos formales de revisión técnica**

**Estrategia Recomendada:**

**1. Análisis Estático de Código:**

```yaml
# SonarQube
sonarqube:
  image: sonarqube:community
  ports: ["9000:9000"]
```

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
</plugin>
```

**Métricas de Calidad:**
- Code Coverage > 80%
- Duplicación < 3%
- Complejidad Ciclomática < 10
- Code Smells: 0 bloqueantes
- Vulnerabilidades de Seguridad: 0

**2. Revisión de Código:**

```yaml
# .github/pull_request_template.md
## Descripción
[Descripción de los cambios]

## Checklist
- [ ] Tests unitarios agregados/actualizados
- [ ] Tests de integración agregados/actualizados
- [ ] Documentación actualizada
- [ ] No hay warnings de compilación
- [ ] SonarQube pasa sin issues críticos
- [ ] Performance evaluado (si aplica)

## Revisores
@team-backend
```

**Reglas de PR:**
- Mínimo 2 aprobaciones
- CI/CD debe pasar (green)
- Code coverage no debe disminuir
- SonarQube Quality Gate debe pasar

**3. Gestión de Deuda Técnica:**

```yaml
Proceso:
  1. Identificación:
     - Durante code reviews
     - Análisis de SonarQube
     - Retrospectivas de sprint
     
  2. Priorización:
     - Crítico: Vulnerabilidades de seguridad
     - Alto: Performance issues
     - Medio: Code smells
     - Bajo: Mejoras de estilo
     
  3. Planificación:
     - 20% del sprint dedicado a deuda técnica
     - Tech debt sprints trimestrales
     
  4. Tracking:
     - Issues etiquetados como "tech-debt"
     - Dashboard de métricas de calidad
     - Revisión mensual con stakeholders
```

**4. Documentación de Código:**

```java
/**
 * Servicio de autenticación que maneja registro, login y MFA.
 * 
 * @author E-Commerce Team
 * @version 1.0
 * @since 2025-01-01
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param request Datos del usuario a registrar
     * @throws UserAlreadyExistsException si el email ya está registrado
     */
    public void register(RegisterRequest request) {
        // Implementation
    }
}
```

---

## 9. Anexos y Referencias

### 9.1 Glosario

| Término | Definición |
|---------|-----------|
| **Microservicio** | Servicio independiente con responsabilidad única que puede ser desplegado y escalado de forma autónoma |
| **JWT (JSON Web Token)** | Estándar abierto (RFC 7519) para crear tokens de acceso que permiten autenticación stateless |
| **MFA (Multi-Factor Authentication)** | Método de autenticación que requiere dos o más factores de verificación |
| **TOTP (Time-based OTP)** | Algoritmo que genera contraseñas de un solo uso basadas en tiempo (RFC 6238) |
| **OAuth2** | Protocolo de autorización que permite a aplicaciones obtener acceso limitado a cuentas de usuario |
| **Apache Kafka** | Plataforma de streaming distribuida para publicar, suscribir, almacenar y procesar streams de eventos |
| **Event-Driven Architecture** | Patrón arquitectónico donde los componentes se comunican mediante eventos asíncronos |
| **CORS (Cross-Origin Resource Sharing)** | Mecanismo que permite solicitudes HTTP desde un dominio diferente al del recurso |
| **DTO (Data Transfer Object)** | Objeto que transporta datos entre procesos para reducir llamadas remotas |
| **RBAC (Role-Based Access Control)** | Control de acceso basado en roles asignados a usuarios |
| **API Gateway** | Punto de entrada único que enruta solicitudes a microservicios apropiados |
| **Circuit Breaker** | Patrón de diseño que previene cascadas de fallos en sistemas distribuidos |
| **Healthcheck** | Endpoint o mecanismo para verificar el estado de salud de un servicio |
| **Idempotencia** | Propiedad donde múltiples ejecuciones de una operación producen el mismo resultado |
| **Eventual Consistency** | Modelo de consistencia donde los datos se sincronizan eventualmente |
| **Saga Pattern** | Patrón para gestionar transacciones distribuidas mediante eventos |
| **Service Mesh** | Capa de infraestructura para gestionar comunicación entre microservicios |
| **Sidecar Pattern** | Contenedor auxiliar que se ejecuta junto a un servicio principal |
| **Strangler Fig Pattern** | Patrón para migrar gradualmente de un sistema legacy a uno nuevo |

### 9.2 Referencias y normativas

**Estándares Seguidos:**

**Protocolos y Especificaciones:**
- ✅ **HTTP/1.1** - RFC 7230-7235
- ✅ **REST** - Architectural Style (Roy Fielding)
- ✅ **JSON** - RFC 8259
- ✅ **JWT** - RFC 7519
- ✅ **OAuth 2.0** - RFC 6749
- ✅ **TOTP** - RFC 6238
- ✅ **CORS** - W3C Recommendation

**Frameworks y Librerías:**
- ✅ **Spring Boot** - 3.2.0 (Spring Framework 6.x)
- ✅ **Spring Security** - 6.x
- ✅ **Hibernate/JPA** - 6.x (Jakarta Persistence)
- ✅ **Apache Kafka** - 7.5.0 (Confluent Platform)

**Buenas Prácticas:**
- ✅ **12-Factor App** - Metodología para aplicaciones SaaS
- ✅ **SOLID Principles** - Principios de diseño orientado a objetos
- ✅ **Clean Architecture** - Arquitectura hexagonal/limpia
- ✅ **Domain-Driven Design** - Diseño dirigido por dominio (parcial)

**Seguridad:**
- ⚠️ **OWASP Top 10** - Principales riesgos de seguridad web (no auditado formalmente)
- ✅ **BCrypt** - Algoritmo de hashing de contraseñas
- ⚠️ **TLS 1.3** - Protocolo de seguridad de transporte (no implementado)

**Calidad de Código:**
- ⚠️ **SonarQube Quality Gates** - No configurado
- ⚠️ **Google Java Style Guide** - No aplicado formalmente
- ⚠️ **Checkstyle** - No configurado

### 9.3 Documentación técnica relacionada

**Documentación del Proyecto:**

- 📄 **README.md** - Guía de inicio rápido y comandos útiles
- 📄 **ARQUITECTURA_PRESENTACION.html** - Presentación interactiva de arquitectura
- 📄 **auth-service/README.md** - Documentación específica del servicio de autenticación
- 📄 **product-service/README.md** - Documentación del servicio de productos
- 📄 **order-service/README.md** - Documentación del servicio de órdenes
- 📄 **notification-service/README.md** - Documentación del servicio de notificaciones
- 📄 **ecommerce-frontend/README.md** - Documentación del frontend React

**Recursos Externos:**

**Spring Framework:**
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Kafka](https://docs.spring.io/spring-kafka/reference/html/)

**Apache Kafka:**
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Confluent Platform](https://docs.confluent.io/)

**PostgreSQL:**
- [PostgreSQL Documentation](https://www.postgresql.org/docs/15/)

**React:**
- [React Documentation](https://react.dev/)
- [Vite Guide](https://vitejs.dev/guide/)

**Docker:**
- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Reference](https://docs.docker.com/compose/)

**OAuth2 Providers:**
- [Google OAuth2 Documentation](https://developers.google.com/identity/protocols/oauth2)
- [GitHub OAuth Apps](https://docs.github.com/en/apps/oauth-apps)

**Herramientas Recomendadas:**
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Jaeger Documentation](https://www.jaegertracing.io/docs/)

---

**Documento generado:** 18 de diciembre de 2025  
**Versión:** 1.0  
**Autor:** Equipo de Arquitectura E-Commerce Platform  
**Próxima revisión:** Trimestral o ante cambios arquitectónicos significativos
