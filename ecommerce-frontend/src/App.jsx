import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import { Navbar } from './components/layout/Navbar';
import { PrivateRoute } from './components/layout/PrivateRoute';

import { Home } from './pages/Home';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { Products } from './pages/Products';
import { OAuth2Callback } from './pages/OAuth2Callback';
import { OAuth2MFAVerification } from './pages/OAuth2MFAVerification';
// import { ProductDetails } from './pages/ProductDetails';
import { Cart } from './pages/Cart';
import { Checkout } from './pages/Checkout';
import { Orders } from './pages/Orders';
import { Profile } from './pages/Profile';
import { Dashboard } from './pages/admin/Dashboard';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <Navbar />

          <Routes>
            <Route path="/" element={<Login />} />
            <Route path="/home" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/oauth2/callback" element={<OAuth2Callback />} />
            <Route path="/oauth2/mfa-verify" element={<OAuth2MFAVerification />} />
            <Route path="/products" element={<Products />} />
            {/* <Route path="/products/:id" element={<ProductDetails />} /> */}
            <Route path="/cart" element={<Cart />} />

            {/* Rutas protegidas */}
            <Route
              path="/checkout"
              element={
                <PrivateRoute>
                  <Checkout />
                </PrivateRoute>
              }
            />
            <Route
              path="/orders"
              element={
                <PrivateRoute>
                  <Orders />
                </PrivateRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <PrivateRoute>
                  <Profile />
                </PrivateRoute>
              }
            />

            {/* Rutas de admin */}
            <Route
              path="/admin/*"
              element={
                <PrivateRoute adminOnly>
                  <Dashboard />
                </PrivateRoute>
              }
            />
          </Routes>

          <ToastContainer position="bottom-right" />
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
