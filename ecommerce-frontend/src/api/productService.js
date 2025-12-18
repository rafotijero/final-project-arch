import { productAPI } from './axios';

export const productService = {
    // Listar productos
    getProducts: async (filters = {}) => {
        const params = new URLSearchParams(filters).toString();
        const response = await productAPI.get(`/api/products?${params}`);
        return response.data;
    },

    // Obtener producto por ID
    getProduct: async (id) => {
        const response = await productAPI.get(`/api/products/${id}`);
        return response.data;
    },

    // Crear producto (ADMIN)
    createProduct: async (productData) => {
        const response = await productAPI.post('/api/products', productData);
        return response.data;
    },

    // Actualizar producto (ADMIN)
    updateProduct: async (id, productData) => {
        const response = await productAPI.put(`/api/products/${id}`, productData);
        return response.data;
    },

    // Eliminar producto (ADMIN)
    deleteProduct: async (id) => {
        await productAPI.delete(`/api/products/${id}`);
    },

    // Listar categorías
    getCategories: async () => {
        const response = await productAPI.get('/api/categories');
        return response.data;
    },

    // Crear categoría (ADMIN)
    createCategory: async (categoryData) => {
        const response = await productAPI.post('/api/categories', categoryData);
        return response.data;
    },
};
