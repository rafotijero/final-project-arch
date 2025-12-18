import axios from 'axios';

const authAPI = axios.create({
    baseURL: import.meta.env.VITE_AUTH_SERVICE_URL,
});

const productAPI = axios.create({
    baseURL: import.meta.env.VITE_PRODUCT_SERVICE_URL,
});

const orderAPI = axios.create({
    baseURL: import.meta.env.VITE_ORDER_SERVICE_URL,
});

// Interceptor para agregar JWT a todas las peticiones
const addAuthInterceptor = (api) => {
    api.interceptors.request.use(
        (config) => {
            const token = localStorage.getItem('token');
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
            return config;
        },
        (error) => Promise.reject(error)
    );

    // Interceptor para manejar errores 401 (token expirado)
    api.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response?.status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                // Redirigir SOLO si no estamos ya en login para evitar bucles
                if (!window.location.pathname.startsWith('/login')) {
                    window.location.href = '/login';
                }
            }
            return Promise.reject(error);
        }
    );
};

addAuthInterceptor(authAPI);
addAuthInterceptor(productAPI);
addAuthInterceptor(orderAPI);

export { authAPI, productAPI, orderAPI };
