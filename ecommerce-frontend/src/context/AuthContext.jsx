import { createContext, useState, useEffect } from 'react';
import { authService } from '../api/authService';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);
    const [mfaRequired, setMfaRequired] = useState(false);

    useEffect(() => {
        // Cargar token y usuario del localStorage al inicio
        const storedToken = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');

        if (storedToken && storedUser) {
            setToken(storedToken);
            setUser(JSON.parse(storedUser));
        }

        setLoading(false);
    }, []);

    const login = async (credentials) => {
        try {
            console.log('[AuthContext] Calling authService.login with:', credentials);
            const response = await authService.login(credentials);
            console.log('[AuthContext] Received response:', response);

            if (response.mfaRequired) {
                console.log('[AuthContext] MFA required, setting flag');
                setMfaRequired(true);
                return { mfaRequired: true };
            }

            console.log('[AuthContext] Saving auth data:', { token: response.token, user: response.user });
            saveAuthData(response.token, response.user);
            return { success: true };
        } catch (error) {
            console.error('[AuthContext] Login error:', error);
            throw error;
        }
    };

    const verifyMFA = async (code) => {
        try {
            const response = await authService.verifyMFA(code);
            saveAuthData(response.token, response.user);
            setMfaRequired(false);
            return { success: true };
        } catch (error) {
            throw error;
        }
    };

    const register = async (userData) => {
        try {
            const response = await authService.register(userData);
            saveAuthData(response.token, response.user);
            return { success: true };
        } catch (error) {
            throw error;
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setToken(null);
        setUser(null);
        setMfaRequired(false);
    };

    const saveAuthData = (newToken, newUser) => {
        localStorage.setItem('token', newToken);
        localStorage.setItem('user', JSON.stringify(newUser));
        setToken(newToken);
        setUser(newUser);
    };

    const isAdmin = () => {
        return user?.role === 'ADMIN';
    };

    const refreshUser = async () => {
        try {
            const userData = await authService.getCurrentUser();
            setUser(userData);
            localStorage.setItem('user', JSON.stringify(userData));
        } catch (error) {
            console.error('Error refreshing user:', error);
        }
    };

    const value = {
        user,
        token,
        loading,
        mfaRequired,
        login,
        register,
        logout,
        refreshUser,
        isAdmin,
        isAuthenticated: !!token,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
