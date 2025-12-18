# 💻 E-Commerce Frontend

Aplicación web React para el sistema de e-commerce. Interfaz de usuario moderna y responsive.

## 📋 Descripción

El Frontend es responsable de:
- Interfaz de usuario para clientes
- Autenticación (Login, Register, OAuth2, MFA)
- Catálogo de productos
- Carrito de compras
- Gestión de órdenes
- Perfil de usuario

## 🏗️ Estructura del Proyecto

```
src/
├── components/
│   ├── Navbar.jsx
│   ├── ProductCard.jsx
│   ├── Cart.jsx
│   └── ProtectedRoute.jsx
│
├── pages/
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── Products.jsx
│   ├── ProductDetail.jsx
│   ├── Cart.jsx
│   ├── Orders.jsx
│   └── Profile.jsx
│
├── context/
│   ├── AuthContext.jsx
│   └── CartContext.jsx
│
├── services/
│   ├── authService.js
│   ├── productService.js
│   ├── orderService.js
│   └── api.js
│
├── utils/
│   └── constants.js
│
├── App.jsx
└── main.jsx
```

## 🚀 Tecnologías

- **React 18** - Librería UI
- **Vite** - Build tool y dev server
- **React Router 6** - Navegación SPA
- **Axios** - Cliente HTTP
- **Context API** - State management
- **CSS Modules** - Estilos

## ⚙️ Configuración

### Variables de Entorno

Crear archivo `.env` en la raíz del proyecto:

```env
VITE_API_AUTH_URL=http://localhost:8081/api
VITE_API_PRODUCT_URL=http://localhost:8082/api
VITE_API_ORDER_URL=http://localhost:8083/api
VITE_API_NOTIFICATION_URL=http://localhost:8084/api

VITE_GOOGLE_CLIENT_ID=your-google-client-id
VITE_GITHUB_CLIENT_ID=your-github-client-id
```

## 🚀 Inicio Rápido

### Desarrollo Local

```bash
# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm run dev

# La aplicación estará disponible en http://localhost:3000
```

### Build para Producción

```bash
# Crear build optimizado
npm run build

# Preview del build
npm run preview
```

### Docker

```bash
# Build de la imagen
docker build -t ecommerce-frontend .

# Ejecutar contenedor
docker run -p 3000:80 ecommerce-frontend
```

## 📱 Funcionalidades

### Autenticación

- **Login** con email y password
- **Registro** de nuevos usuarios
- **OAuth2** con Google y GitHub
- **MFA** con QR code (Google Authenticator)
- **Logout** y gestión de sesión

### Productos

- **Catálogo** de productos con paginación
- **Búsqueda** por nombre
- **Filtros** por categoría y precio
- **Detalle** de producto
- **Agregar al carrito**

### Carrito

- **Agregar/eliminar** productos
- **Actualizar cantidad**
- **Ver total**
- **Checkout** (crear orden)

### Órdenes

- **Historial** de órdenes
- **Detalle** de orden
- **Estados** de orden
- **Cancelar** orden

## 🔐 Autenticación

### JWT Token

El token JWT se almacena en `localStorage` y se envía en cada request:

```javascript
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
```

### Protected Routes

Las rutas protegidas requieren autenticación:

```jsx
<Route path="/orders" element={
  <ProtectedRoute>
    <Orders />
  </ProtectedRoute>
} />
```

## 🎨 Componentes Principales

### Navbar

Barra de navegación con:
- Logo
- Enlaces (Home, Products, Orders)
- Carrito (con contador)
- Usuario (Login/Logout)

### ProductCard

Tarjeta de producto con:
- Imagen
- Nombre y descripción
- Precio
- Stock disponible
- Botón "Agregar al carrito"

### Cart

Carrito de compras con:
- Lista de productos
- Cantidad y subtotal por producto
- Total general
- Botón "Checkout"

## 📡 Servicios API

### authService.js

```javascript
- login(email, password)
- register(username, email, password)
- logout()
- getCurrentUser()
- setupMFA()
- verifyMFA(code)
```

### productService.js

```javascript
- getAllProducts()
- getProductById(id)
- searchProducts(query)
- getProductsByCategory(category)
```

### orderService.js

```javascript
- createOrder(items)
- getUserOrders()
- getOrderById(id)
- cancelOrder(id)
```

## 🧪 Testing

### Flujo de Usuario

1. **Registro**: Crear cuenta nueva
2. **Login**: Iniciar sesión
3. **Productos**: Ver catálogo
4. **Carrito**: Agregar productos
5. **Checkout**: Crear orden
6. **Órdenes**: Ver historial

### OAuth2

1. Click en "Login with Google/GitHub"
2. Autorizar en la ventana emergente
3. Redirección automática con token

### MFA

1. Ir a perfil
2. Click en "Enable MFA"
3. Escanear QR con Google Authenticator
4. Ingresar código de verificación

## 🐛 Troubleshooting

### CORS Error

Verificar que los servicios backend tengan CORS configurado para `http://localhost:3000`.

### Token Expirado

El token JWT expira en 24 horas. Si expira, el usuario debe hacer login nuevamente.

### OAuth2 Redirect

Verificar que las URLs de callback estén configuradas correctamente en Google/GitHub.

## 📝 Notas

- El carrito se almacena en `localStorage`
- Los tokens JWT se almacenan en `localStorage`
- Las imágenes de productos deben ser URLs válidas
- El frontend es completamente responsive

## 🔗 Enlaces

- [React Documentation](https://react.dev)
- [Vite Documentation](https://vitejs.dev)
- [React Router](https://reactrouter.com)
- [Axios](https://axios-http.com)

## 🎨 Capturas de Pantalla

*(Agregar capturas de pantalla de la aplicación)*

## 📄 Licencia

Proyecto académico - Arquitectura de Software
