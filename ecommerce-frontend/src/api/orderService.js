import { orderAPI } from './axios';

export const orderService = {
    // Crear orden
    createOrder: async (orderData) => {
        const response = await orderAPI.post('/api/orders', orderData);
        return response.data;
    },

    // Listar mis órdenes
    getMyOrders: async (status) => {
        const params = status ? `?status=${status}` : '';
        const response = await orderAPI.get(`/api/orders${params}`);
        return response.data;
    },

    // Obtener orden por ID
    getOrder: async (id) => {
        const response = await orderAPI.get(`/api/orders/${id}`);
        return response.data;
    },

    // Cancelar orden
    cancelOrder: async (id) => {
        const response = await orderAPI.delete(`/api/orders/${id}`);
        return response.data;
    },

    // Actualizar estado (ADMIN)
    updateOrderStatus: async (id, status) => {
        const response = await orderAPI.patch(`/api/orders/${id}/status`, { status });
        return response.data;
    },
};
