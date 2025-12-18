import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const Home = () => {
    const { user, isAuthenticated } = useAuth();

    // Helper function to get display name
    const getDisplayName = () => {
        if (user.username) return user.username;
        // Extract name from email (everything before @)
        return user.email.split('@')[0];
    };

    // Helper function to get initial for avatar
    const getInitial = () => {
        if (user.username) return user.username.charAt(0).toUpperCase();
        return user.email.charAt(0).toUpperCase();
    };

    if (isAuthenticated && user) {
        const displayName = getDisplayName();
        const initial = getInitial();

        return (
            <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
                <div className="max-w-4xl w-full bg-white rounded-2xl shadow-2xl p-8 md:p-12">
                    <div className="text-center mb-8">
                        <div className="mb-4">
                            <div className="w-24 h-24 bg-gradient-to-r from-blue-600 to-indigo-600 rounded-full mx-auto flex items-center justify-center text-white text-4xl font-bold">
                                {initial}
                            </div>
                        </div>
                        <h1 className="text-4xl md:text-5xl font-extrabold text-gray-900 mb-2">
                            ¡Bienvenido, {displayName}! 👋
                        </h1>
                        <p className="text-xl text-gray-600">
                            Nos alegra verte de nuevo en E-Commerce
                        </p>
                    </div>

                    <div className="grid md:grid-cols-2 gap-6 mb-8">
                        <div className="bg-blue-50 p-6 rounded-lg border border-blue-200">
                            <h3 className="font-semibold text-lg text-blue-900 mb-2">Tu Cuenta</h3>
                            <p className="text-sm text-gray-700 mb-1">
                                <span className="font-medium">Email:</span> {user.email}
                            </p>
                            <p className="text-sm text-gray-700 mb-1">
                                <span className="font-medium">Rol:</span> {user.role}
                            </p>
                            {user.provider && (
                                <p className="text-sm text-gray-700 mb-1">
                                    <span className="font-medium">Proveedor:</span> {user.provider}
                                </p>
                            )}
                            {user.mfaEnabled && (
                                <div className="mt-3 flex items-center gap-2 text-green-700">
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                                    </svg>
                                    <span className="text-sm font-medium">MFA Activado</span>
                                </div>
                            )}
                        </div>

                        <div className="bg-indigo-50 p-6 rounded-lg border border-indigo-200">
                            <h3 className="font-semibold text-lg text-indigo-900 mb-2">Accesos Rápidos</h3>
                            <div className="space-y-2">
                                <Link to="/profile" className="block text-sm text-indigo-700 hover:text-indigo-900 hover:underline">
                                    → Ver mi perfil
                                </Link>
                                <Link to="/orders" className="block text-sm text-indigo-700 hover:text-indigo-900 hover:underline">
                                    → Mis pedidos
                                </Link>
                                <Link to="/cart" className="block text-sm text-indigo-700 hover:text-indigo-900 hover:underline">
                                    → Mi carrito
                                </Link>
                            </div>
                        </div>
                    </div>

                    <div className="flex flex-col sm:flex-row justify-center gap-4">
                        <Link
                            to="/products"
                            className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-8 py-4 rounded-lg text-lg font-semibold hover:from-blue-700 hover:to-indigo-700 transition shadow-lg text-center"
                        >
                            🛍️ Explorar Productos
                        </Link>
                        <Link
                            to="/profile"
                            className="bg-white border-2 border-gray-300 text-gray-700 px-8 py-4 rounded-lg text-lg font-semibold hover:bg-gray-50 transition text-center"
                        >
                            ⚙️ Configurar Perfil
                        </Link>
                    </div>
                </div>
            </div>
        );
    }

    // Vista para usuarios no autenticados
    return (
        <div className="container mx-auto p-4 text-center">
            <h1 className="text-4xl font-bold mb-4">Bienvenido a E-Commerce</h1>
            <p className="text-xl mb-8">
                La mejor tienda de microservicios.
            </p>

            <div className="flex justify-center gap-4">
                <Link
                    to="/products"
                    className="bg-blue-600 text-white px-6 py-3 rounded-lg text-lg hover:bg-blue-700"
                >
                    Ver Productos
                </Link>
                <Link
                    to="/register"
                    className="bg-green-600 text-white px-6 py-3 rounded-lg text-lg hover:bg-green-700"
                >
                    Registrarse
                </Link>
            </div>
        </div>
    );
};
