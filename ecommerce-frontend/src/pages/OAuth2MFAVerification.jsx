import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authAPI } from '../api/axios';
import { toast } from 'react-toastify';

export const OAuth2MFAVerification = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [mfaCode, setMfaCode] = useState('');
    const [loading, setLoading] = useState(false);
    const [email, setEmail] = useState('');
    const [sessionToken, setSessionToken] = useState('');

    useEffect(() => {
        const token = searchParams.get('sessionToken');
        const userEmail = searchParams.get('email');

        if (!token || !userEmail) {
            toast.error('Sesión inválida');
            navigate('/login');
            return;
        }

        setSessionToken(token);
        setEmail(decodeURIComponent(userEmail));
    }, [searchParams, navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (mfaCode.length !== 6) {
            toast.error('El código debe tener 6 dígitos');
            return;
        }

        setLoading(true);

        try {
            console.log('[OAuth2MFA] Verifying MFA code');
            const response = await authAPI.post('/api/auth/oauth2/verify-mfa', {
                sessionToken,
                mfaCode
            });

            console.log('[OAuth2MFA] MFA verified successfully:', response.data);

            // Save token and user data
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data.user));

            toast.success('Verificación exitosa');

            // Redirect to home
            window.location.href = '/home';
        } catch (error) {
            console.error('[OAuth2MFA] Verification error:', error);

            if (error.response?.status === 401 || error.response?.data?.message?.includes('expired')) {
                toast.error('Sesión expirada. Por favor, inicia sesión nuevamente.');
                setTimeout(() => navigate('/login'), 2000);
            } else {
                toast.error('Código MFA inválido');
                setMfaCode('');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
            <div className="max-w-md w-full space-y-8 bg-white p-10 rounded-2xl shadow-2xl">
                <div className="text-center">
                    <div className="flex justify-center mb-4">
                        <div className="rounded-full bg-blue-100 p-4">
                            <svg className="w-12 h-12 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                            </svg>
                        </div>
                    </div>
                    <h2 className="text-3xl font-extrabold text-gray-900 mb-2">
                        Verificación de Dos Factores
                    </h2>
                    <p className="text-sm text-gray-600 mb-2">
                        Iniciando sesión como: <span className="font-semibold">{email}</span>
                    </p>
                    <p className="text-sm text-gray-600">
                        Ingresa el código de 6 dígitos de tu aplicación autenticadora
                    </p>
                </div>

                <form onSubmit={handleSubmit} className="mt-8 space-y-6">
                    <div>
                        <label htmlFor="mfaCode" className="block text-sm font-medium text-gray-700 mb-2">
                            Código MFA
                        </label>
                        <input
                            id="mfaCode"
                            type="text"
                            placeholder="000000"
                            value={mfaCode}
                            onChange={(e) => setMfaCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                            className="appearance-none relative block w-full px-4 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-150 text-center text-2xl tracking-widest font-mono"
                            maxLength={6}
                            autoFocus
                            required
                            disabled={loading}
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading || mfaCode.length !== 6}
                        className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150 shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {loading ? (
                            <div className="flex items-center">
                                <div className="animate-spin rounded-full h-5 w-5 border-2 border-white border-t-transparent mr-2"></div>
                                Verificando...
                            </div>
                        ) : (
                            'Verificar Código'
                        )}
                    </button>

                    <div className="text-center">
                        <button
                            type="button"
                            onClick={() => navigate('/login')}
                            className="text-sm text-blue-600 hover:text-blue-500 transition"
                        >
                            ← Volver al inicio de sesión
                        </button>
                    </div>
                </form>

                <div className="mt-6 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                    <p className="text-xs text-yellow-800">
                        <strong>Nota:</strong> Esta sesión expirará en 5 minutos por seguridad.
                    </p>
                </div>
            </div>
        </div>
    );
};
