import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';

export const Navbar = () => {
    const { user, logout, isAuthenticated, isAdmin } = useAuth();
    const { getItemCount } = useCart();

    return (
        <nav className="bg-blue-600 text-white p-4">
            <div className="container mx-auto flex justify-between items-center">
                <Link to="/" className="text-2xl font-bold">
                    E-Commerce
                </Link>

                <div className="flex gap-4 items-center">
                    <Link to="/products">Productos</Link>

                    {isAuthenticated ? (
                        <>
                            <Link to="/orders">Mis Pedidos</Link>
                            <Link to="/cart" className="relative">
                                Carrito
                                {getItemCount() > 0 && (
                                    <span className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs">
                                        {getItemCount()}
                                    </span>
                                )}
                            </Link>

                            {isAdmin() && (
                                <Link to="/admin">Admin</Link>
                            )}

                            <span>Hola, {user?.email}</span>
                            <button onClick={logout} className="bg-red-500 px-4 py-2 rounded">
                                Salir
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login">Iniciar Sesión</Link>
                            <Link to="/register" className="bg-green-500 px-4 py-2 rounded">
                                Registrarse
                            </Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
};
