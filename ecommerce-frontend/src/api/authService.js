import { authAPI } from './axios';

export const authService = {
    // Registro local
    register: async (userData) => {
        const response = await authAPI.post('/api/auth/register', userData);
        return response.data;
    },

    // Login local
    login: async (credentials) => {
        const response = await authAPI.post('/api/auth/login', credentials);
        return response.data;
    },

    // Verificar MFA
    verifyMFA: async (code) => {
        // Note: The auth-service endpoint is /api/auth/verify-mfa based on AuthController created earlier
        const response = await authAPI.post('/api/auth/verify-mfa', { code });
        return response.data;
    },

    // Setup MFA
    setupMFA: async () => {
        const response = await authAPI.post('/api/auth/mfa/setup'); // Changed to POST if Controller uses POST, let's check AuthController. 
        // Wait, AuthController code: @PostMapping("/mfa/setup") public ResponseEntity<MFASetupResponse> setupMfa(...)
        // So it is POST. Instructions said GET but code is truth.
        return response.data;
    },

    // Activar MFA
    enableMFA: async (code) => {
        const response = await authAPI.post('/api/auth/mfa/enable', { code });
        return response.data;
    },

    // Desactivar MFA
    disableMFA: async () => {
        const response = await authAPI.post('/api/auth/mfa/disable');
        return response.data;
    },

    // Obtener usuario actual
    getCurrentUser: async () => {
        const response = await authAPI.get('/api/auth/me');
        return response.data;
    },

    // Google OAuth (redirigir)
    loginWithGoogle: () => {
        window.location.href = `${import.meta.env.VITE_AUTH_SERVICE_URL}/oauth2/authorization/google`;
    },

    // GitHub OAuth (redirigir)
    loginWithGithub: () => {
        window.location.href = `${import.meta.env.VITE_AUTH_SERVICE_URL}/oauth2/authorization/github`;
    },
};
