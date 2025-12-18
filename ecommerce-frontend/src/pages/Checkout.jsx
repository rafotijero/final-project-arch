import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../hooks/useCart';
import { orderService } from '../api/orderService';
import { toast } from 'react-toastify';

export const Checkout = () => {
    const [shippingAddress, setShippingAddress] = useState('');
    const [notes, setNotes] = useState('');
    const { items, clearCart, getTotal } = useCart();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        const orderData = {
            items: items.map((item) => ({
                productId: item.id,
                quantity: item.quantity,
            })),
            shippingAddress,
            notes,
        };

        try {
            await orderService.createOrder(orderData);
            clearCart();
            toast.success('Pedido creado exitosamente');
            navigate('/orders');
        } catch (error) {
            toast.error(error.response?.data?.message || 'Error al crear pedido');
        }
    };

    return (
        <div className="container mx-auto p-4 max-w-2xl">
            <h1 className="text-3xl font-bold mb-6">Finalizar Compra</h1>

            {/* Resumen de productos */}
            <div className="mb-6 border p-4 rounded">
                <h2 className="text-xl font-semibold mb-4">Resumen del Pedido</h2>
                {items.map((item) => (
                    <div key={item.id} className="flex justify-between mb-2">
                        <span>{item.name} x {item.quantity}</span>
                        <span>${(item.price * item.quantity).toFixed(2)}</span>
                    </div>
                ))}
                <div className="border-t pt-2 mt-2 font-bold">
                    <div className="flex justify-between">
                        <span>Total:</span>
                        <span>${getTotal().toFixed(2)}</span>
                    </div>
                </div>
            </div>

            {/* Formulario */}
            <form onSubmit={handleSubmit} className="border p-4 rounded">
                <div className="mb-4">
                    <label className="block mb-2 font-semibold">
                        Dirección de Envío *
                    </label>
                    <textarea
                        value={shippingAddress}
                        onChange={(e) => setShippingAddress(e.target.value)}
                        className="w-full p-2 border rounded"
                        rows={3}
                        required
                    />
                </div>

                <div className="mb-4">
                    <label className="block mb-2 font-semibold">
                        Notas (opcional)
                    </label>
                    <textarea
                        value={notes}
                        onChange={(e) => setNotes(e.target.value)}
                        className="w-full p-2 border rounded"
                        rows={2}
                    />
                </div>

                <button
                    type="submit"
                    className="w-full bg-green-500 text-white p-3 rounded hover:bg-green-600"
                >
                    Confirmar Pedido
                </button>
            </form>
        </div>
    );
};
