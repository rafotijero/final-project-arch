import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authAPI } from '../api/axios';

export const OAuth2Callback = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [error, setError] = useState(null);

    useEffect(() => {
        const handleOAuthCallback = async () => {
            const token = searchParams.get('token');
            const fallbackUsername = searchParams.get('username');
            const fallbackEmail = searchParams.get('email');

            if (!token) {
                setError('Token no encontrado');
                return;
            }

            console.log('[OAuth2Callback] Token received:', token.substring(0, 20) + '...');
            console.log('[OAuth2Callback] Fallback data - username:', fallbackUsername, 'email:', fallbackEmail);

            // Save token first
            localStorage.setItem('token', token);

            // Try to fetch user profile with retry logic
            const userProfile = await fetchUserWithRetry(token, 3);

            if (userProfile) {
                console.log('[OAuth2Callback] User profile fetched successfully:', userProfile);
                localStorage.setItem('user', JSON.stringify(userProfile));
            } else if (fallbackUsername && fallbackEmail) {
                // Use fallback data from URL parameters
                console.log('[OAuth2Callback] Using fallback data from URL parameters');
                const fallbackUser = {
                    email: decodeURIComponent(fallbackEmail),
                    username: decodeURIComponent(fallbackUsername),
                    provider: 'GOOGLE', // Default, will be corrected on next /api/auth/me call
                    emailVerified: true,
                    mfaEnabled: false,
                    role: 'USER'
                };
                localStorage.setItem('user', JSON.stringify(fallbackUser));
            } else {
                // No user data available at all
                console.error('[OAuth2Callback] No user data available');
                setError('No se pudo obtener la información del usuario');
                return;
            }

            // Redirect to home page
            console.log('[OAuth2Callback] Redirecting to /home');
            window.location.href = '/home';
        };

        /**
         * Fetch user profile with retry logic and exponential backoff
         * @param {string} token - JWT token
         * @param {number} maxRetries - Maximum number of retry attempts
         * @returns {Promise<object|null>} User profile or null if all retries fail
         */
        const fetchUserWithRetry = async (token, maxRetries = 3) => {
            for (let attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    console.log(`[OAuth2Callback] Fetching user profile (attempt ${attempt}/${maxRetries})`);
                    const response = await authAPI.get('/api/auth/me', {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });
                    return response.data;
                } catch (error) {
                    console.error(`[OAuth2Callback] Attempt ${attempt} failed:`, error);

                    if (attempt < maxRetries) {
                        // Exponential backoff: 1s, 2s, 4s
                        const waitTime = 1000 * Math.pow(2, attempt - 1);
                        console.log(`[OAuth2Callback] Retrying in ${waitTime}ms...`);
                        await new Promise(resolve => setTimeout(resolve, waitTime));
                    }
                }
            }
            console.error('[OAuth2Callback] All retry attempts failed');
            return null;
        };

        handleOAuthCallback();
    }, [searchParams]);

    if (error) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-red-50 to-rose-100 p-4">
                <div className="max-w-md w-full space-y-8 bg-white p-10 rounded-2xl shadow-2xl text-center">
                    <div className="flex justify-center">
                        <div className="rounded-full bg-red-100 p-4">
                            <svg className="w-12 h-12 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </div>
                    </div>
                    <div>
                        <h2 className="text-3xl font-extrabold text-gray-900 mb-2">Error de Autenticación</h2>
                        <p className="text-red-600 text-sm mb-6">{error}</p>
                    </div>
                    <button
                        onClick={() => navigate('/login')}
                        className="w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150 shadow-lg"
                    >
                        Volver a Iniciar Sesión
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
            <div className="max-w-md w-full space-y-8 bg-white p-10 rounded-2xl shadow-2xl text-center">
                <div className="flex justify-center">
                    <div className="relative">
                        <div className="animate-spin rounded-full h-16 w-16 border-4 border-gray-200"></div>
                        <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-blue-600 absolute top-0 left-0"></div>
                    </div>
                </div>
                <div>
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">Procesando autenticación</h2>
                    <p className="text-gray-600 text-sm">Iniciando sesión...</p>
                </div>
            </div>
        </div>
    );
};
