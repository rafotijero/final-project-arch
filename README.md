# 🛒 E-Commerce Microservices Platform

Sistema de comercio electrónico basado en arquitectura de microservicios con comunicación asíncrona mediante Apache Kafka.

## 📋 Tabla de Contenidos

- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Servicios](#servicios)
- [Inicio Rápido](#inicio-rápido)
- [Configuración](#configuración)
- [Documentación](#documentación)

## 🏗️ Arquitectura

Sistema distribuido compuesto por 4 microservicios independientes que se comunican mediante:
- **REST APIs** para comunicación síncrona
- **Apache Kafka** para eventos asíncronos
- **PostgreSQL** (base de datos independiente por servicio)

### Diagrama de Arquitectura

```
Frontend (React) :3000
        ↓
┌───────────────────────────────────────────────────┐
│  Auth Service  │  Product  │  Order  │  Notification │
│    :8081       │  :8082    │  :8083  │    :8084      │
└───────────────────────────────────────────────────┘
        ↓
    Kafka :9092
        ↓
PostgreSQL (4 instancias)
```

## 🚀 Tecnologías

### Backend
- **Spring Boot 3.2.0** - Framework principal
- **Java 17** - Lenguaje de programación
- **PostgreSQL 15** - Base de datos relacional
- **Apache Kafka 7.5.0** - Message broker
- **Spring Security** - Autenticación y autorización
- **JWT** - Tokens de autenticación

### Frontend
- **React 18** - Librería UI
- **Vite** - Build tool
- **Axios** - Cliente HTTP
- **React Router** - Navegación SPA

### DevOps
- **Docker** - Contenedorización
- **Docker Compose** - Orquestación local

## 📦 Servicios

### 1. Auth Service (:8081)
Gestión de usuarios y autenticación.

**Características:**
- Login con email/password
- OAuth2 (Google, GitHub)
- MFA (Two-Factor Authentication)
- Generación y validación de JWT

### 2. Product Service (:8082)
Catálogo e inventario de productos.

**Características:**
- CRUD de productos
- Gestión de categorías
- Control de stock
- Búsqueda y filtrado

### 3. Order Service (:8083)
Procesamiento de órdenes.

**Características:**
- Creación de órdenes
- Validación de stock
- Estados de orden
- Publicación de eventos a Kafka

### 4. Notification Service (:8084)
Notificaciones y auditoría.

**Características:**
- Consumo de eventos Kafka
- Envío de emails (SMTP)
- Registro de auditoría
- Tracking de notificaciones

## 🚀 Inicio Rápido

### Prerrequisitos

- Docker Desktop
- Git

### Instalación

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd proyecto-final
```

2. **Levantar todos los servicios**
```bash
docker-compose up -d
```

3. **Verificar que todos los servicios estén corriendo**
```bash
docker-compose ps
```

### Acceso a los Servicios

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Frontend | http://localhost:3000 | Aplicación React |
| Auth Service | http://localhost:8081 | API de autenticación |
| Product Service | http://localhost:8082 | API de productos |
| Order Service | http://localhost:8083 | API de órdenes |
| Notification Service | http://localhost:8084 | API de notificaciones |
| Kafka UI | http://localhost:8090 | Interfaz de Kafka |

## ⚙️ Configuración

### Variables de Entorno

Cada servicio tiene su archivo `application.yml` con configuraciones específicas:

**Auth Service:**
- JWT Secret
- OAuth2 credentials (Google, GitHub)
- Database connection

**Product Service:**
- Database connection
- CORS configuration

**Order Service:**
- Database connection
- Kafka producer config
- Product Service URL

**Notification Service:**
- Database connection
- Kafka consumer config
- SMTP configuration (MailTrap)

### Base de Datos

Cada servicio tiene su propia base de datos PostgreSQL:
- `auth_db` - Auth Service
- `product_db` - Product Service
- `order_db` - Order Service
- `notification_db` - Notification Service

## 📚 Documentación

### Presentación de Arquitectura

Abre `ARQUITECTURA_PRESENTACION.html` en tu navegador para ver una presentación interactiva de la arquitectura del sistema.

### READMEs de Servicios

Cada servicio tiene su propio README con detalles específicos:
- [Auth Service](./auth-service/README.md)
- [Product Service](./product-service/README.md)
- [Order Service](./order-service/README.md)
- [Notification Service](./notification-service/README.md)
- [Frontend](./ecommerce-frontend/README.md)

## 🔧 Comandos Útiles

### Docker

```bash
# Levantar todos los servicios
docker-compose up -d

# Ver logs de un servicio específico
docker-compose logs -f <service-name>

# Detener todos los servicios
docker-compose down

# Reconstruir un servicio
docker-compose build <service-name>

# Reiniciar un servicio
docker-compose restart <service-name>
```

### Kafka

```bash
# Ver topics
docker exec -it proyecto-final-kafka-1 kafka-topics --list --bootstrap-server localhost:9092

# Ver mensajes de un topic
docker exec -it proyecto-final-kafka-1 kafka-console-consumer --topic order-events --from-beginning --bootstrap-server localhost:9092
```

## 🎯 Patrones Implementados

- ✅ **Microservicios** - Servicios independientes y escalables
- ✅ **Event-Driven Architecture** - Comunicación asíncrona con Kafka
- ✅ **Database per Service** - Aislamiento de datos
- ✅ **API Gateway Pattern** - Punto de entrada único
- ✅ **CQRS** - Separación de comandos y consultas
- ✅ **Circuit Breaker** - Tolerancia a fallos

## 📝 Licencia

Este proyecto es parte de un trabajo académico para el curso de Arquitectura de Software.

## 👥 Autor

Proyecto Final - Arquitectura de Software
