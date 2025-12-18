import { useState, useEffect } from 'react';
import { orderService } from '../api/orderService';
import { OrderList } from '../components/orders/OrderList';

export const Orders = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadOrders();
    }, []);

    const loadOrders = async () => {
        try {
            const data = await orderService.getMyOrders();
            setOrders(data);
        } catch (error) {
            console.error('Error loading orders:', error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div>Cargando...</div>;

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6">Mis Pedidos</h1>

            {orders.length === 0 ? (
                <p>No tienes pedidos aún.</p>
            ) : (
                <OrderList orders={orders} />
            )}
        </div>
    );
};
