import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../hooks/useCart';

export const Cart = () => {
    const { items, removeItem, updateQuantity, getTotal, clearCart } = useCart();
    const navigate = useNavigate();

    if (items.length === 0) {
        return (
            <div className="container mx-auto p-4 text-center">
                <h2 className="text-2xl font-bold mb-4">Tu carrito está vacío</h2>
                <Link to="/products" className="text-blue-500 hover:underline">
                    Volver a la tienda
                </Link>
            </div>
        );
    }

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6">Carrito de Compras</h1>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="md:col-span-2 space-y-4">
                    {items.map((item) => (
                        <div key={item.id} className="border p-4 rounded flex justify-between items-center">
                            <div>
                                <h3 className="font-semibold">{item.name}</h3>
                                <p className="text-gray-600">${item.price}</p>
                            </div>

                            <div className="flex items-center gap-4">
                                <input
                                    type="number"
                                    min="1"
                                    value={item.quantity}
                                    onChange={(e) => updateQuantity(item.id, parseInt(e.target.value))}
                                    className="w-16 p-1 border rounded"
                                />

                                <p className="font-bold w-20 text-right">
                                    ${(item.price * item.quantity).toFixed(2)}
                                </p>

                                <button
                                    onClick={() => removeItem(item.id)}
                                    className="text-red-500 hover:text-red-700"
                                >
                                    Eliminar
                                </button>
                            </div>
                        </div>
                    ))}

                    <button
                        onClick={clearCart}
                        className="text-red-500 text-sm hover:underline mt-4"
                    >
                        Vaciar Carrito
                    </button>
                </div>

                <div className="border p-4 rounded h-fit">
                    <h2 className="text-xl font-bold mb-4">Resumen</h2>
                    <div className="flex justify-between mb-4 text-lg">
                        <span>Total:</span>
                        <span className="font-bold">${getTotal().toFixed(2)}</span>
                    </div>

                    <button
                        onClick={() => navigate('/checkout')}
                        className="w-full bg-blue-600 text-white p-3 rounded hover:bg-blue-700"
                    >
                        Proceder al Pago
                    </button>
                </div>
            </div>
        </div>
    );
};
