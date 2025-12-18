# 🔐 Auth Service

Servicio de autenticación y autorización para el sistema de e-commerce. Gestiona usuarios, JWT, OAuth2 y MFA.

## 📋 Descripción

El Auth Service es responsable de:
- Registro y login de usuarios
- Generación y validación de tokens JWT
- Autenticación OAuth2 (Google, GitHub)
- Multi-Factor Authentication (MFA/2FA)
- Gestión de perfiles de usuario

## 🏗️ Arquitectura

### Capas

```
presentation/
└── controller/
    └── AuthController.java
│
application/
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── AuthResponse.java
└── service/
    └── AuthService.java
│
domain/
├── model/
│   └── User.java
└── repository/
    └── UserRepository.java
│
infrastructure/
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
└── oauth2/
    └── OAuth2SuccessHandler.java
```

## 🚀 Tecnologías

- **Spring Boot 3.2.0**
- **Spring Security 6** - Seguridad y autenticación
- **JWT** - Tokens de autenticación
- **OAuth2** - Google & GitHub
- **PostgreSQL** - Base de datos
- **Lombok** - Reducción de boilerplate

## ⚙️ Configuración

### Variables de Entorno

```yaml
# Database
spring.datasource.url=jdbc:postgresql://postgres-auth:5432/auth_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000  # 24 horas

# OAuth2 - Google
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}

# OAuth2 - GitHub
spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}

# CORS
cors.allowed-origins=http://localhost:3000
```

## 📡 API Endpoints

### Autenticación

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user@example.com",
  "email": "user@example.com",
  "password": "password123"
}
```

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "user@example.com",
  "email": "user@example.com"
}
```

### OAuth2

```http
GET /oauth2/authorization/google
```
Redirige a Google para autenticación.

```http
GET /oauth2/authorization/github
```
Redirige a GitHub para autenticación.

### MFA

```http
POST /api/auth/mfa/setup
Authorization: Bearer {token}
```
Genera QR code para configurar MFA.

```http
POST /api/auth/mfa/verify
Authorization: Bearer {token}
Content-Type: application/json

{
  "code": "123456"
}
```

### Usuario

```http
GET /api/auth/me
Authorization: Bearer {token}
```
Obtiene información del usuario autenticado.

## 🔒 Seguridad

### JWT

Los tokens JWT contienen:
- `sub`: Email del usuario
- `username`: Nombre de usuario
- `iat`: Fecha de emisión
- `exp`: Fecha de expiración

### Password Encoding

Las contraseñas se almacenan usando **BCrypt** con factor de costo 10.

### CORS

Configurado para permitir requests desde `http://localhost:3000` (frontend).

## 📊 Modelo de Datos

### User

```java
- id: UUID
- username: String (unique)
- email: String (unique)
- password: String (BCrypt)
- provider: AuthProvider (LOCAL, GOOGLE, GITHUB)
- mfaEnabled: Boolean
- mfaSecret: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

## 🧪 Testing

### Registro de Usuario

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Verificar Token

```bash
curl -X GET http://localhost:8081/api/auth/me \
  -H "Authorization: Bearer {your-token}"
```

## 🐛 Troubleshooting

### OAuth2 no funciona

1. Verificar que las credenciales de Google/GitHub estén configuradas
2. Comprobar la URL de callback en la consola de OAuth
3. Revisar logs del servicio

### JWT inválido

1. Verificar que el secret sea el mismo en todos los servicios
2. Comprobar que el token no haya expirado
3. Revisar el formato del header Authorization

## 📝 Notas

- Los tokens JWT expiran en 24 horas
- OAuth2 crea automáticamente un usuario si no existe
- MFA es opcional y se configura por usuario
- Las contraseñas deben tener mínimo 8 caracteres

## 🔗 Enlaces

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io)
- [Google OAuth2](https://console.cloud.google.com)
- [GitHub OAuth Apps](https://github.com/settings/developers)
