import { useAuth } from '../hooks/useAuth';
import { authService } from '../api/authService';
import { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { QRCodeCanvas } from 'qrcode.react';

export const Profile = () => {
    const { user, refreshUser } = useAuth();
    const [mfaSecret, setMfaSecret] = useState(null);
    const [mfaCode, setMfaCode] = useState('');
    const [showQR, setShowQR] = useState(false);
    const [isEnabling, setIsEnabling] = useState(false);

    const handleToggleMFA = async (enabled) => {
        if (enabled) {
            // Enable MFA - show setup
            try {
                const data = await authService.setupMFA();
                setMfaSecret(data);
                setShowQR(true);
                setIsEnabling(true);
            } catch (error) {
                toast.error('Error al iniciar setup MFA');
            }
        } else {
            // Disable MFA
            if (window.confirm('¿Estás seguro de que deseas desactivar la autenticación de dos factores?')) {
                try {
                    await authService.disableMFA();
                    toast.success('MFA desactivado correctamente');
                    if (refreshUser) await refreshUser();
                } catch (error) {
                    toast.error('Error al desactivar MFA');
                }
            }
        }
    };

    const handleEnableMFA = async () => {
        try {
            await authService.enableMFA(mfaCode);
            toast.success('MFA activado correctamente');
            setShowQR(false);
            setIsEnabling(false);
            setMfaCode('');
            if (refreshUser) await refreshUser();
        } catch (error) {
            toast.error('Código inválido');
        }
    };

    const handleCancelSetup = () => {
        setShowQR(false);
        setIsEnabling(false);
        setMfaCode('');
        setMfaSecret(null);
    };

    return (
        <div className="container mx-auto p-4 max-w-4xl">
            <h1 className="text-3xl font-bold mb-6 text-gray-800">Mi Perfil</h1>

            {/* User Info Card */}
            <div className="bg-white shadow-md rounded-lg p-6 mb-6">
                <h2 className="text-xl font-semibold mb-4 text-gray-700">Información Personal</h2>
                <div className="space-y-3">
                    <div className="flex items-center">
                        <span className="font-medium text-gray-600 w-32">Usuario:</span>
                        <span className="text-gray-800">{user.username}</span>
                    </div>
                    <div className="flex items-center">
                        <span className="font-medium text-gray-600 w-32">Email:</span>
                        <span className="text-gray-800">{user.email}</span>
                    </div>
                    <div className="flex items-center">
                        <span className="font-medium text-gray-600 w-32">Rol:</span>
                        <span className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
                            {user.role}
                        </span>
                    </div>
                </div>
            </div>

            {/* Security Card */}
            <div className="bg-white shadow-md rounded-lg p-6">
                <h2 className="text-xl font-semibold mb-4 text-gray-700">Seguridad</h2>

                {/* MFA Toggle */}
                <div className="flex items-center justify-between mb-4 p-4 bg-gray-50 rounded-lg">
                    <div>
                        <h3 className="font-medium text-gray-800">Autenticación de Dos Factores (MFA)</h3>
                        <p className="text-sm text-gray-600 mt-1">
                            Agrega una capa adicional de seguridad a tu cuenta
                        </p>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                        <input
                            type="checkbox"
                            checked={user.mfaEnabled}
                            onChange={(e) => handleToggleMFA(e.target.checked)}
                            disabled={isEnabling}
                            className="sr-only peer"
                        />
                        <div className="w-14 h-7 bg-gray-300 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:left-[4px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-6 after:w-6 after:transition-all peer-checked:bg-blue-600"></div>
                    </label>
                </div>

                {/* QR Code Setup */}
                {showQR && mfaSecret && (
                    <div className="mt-6 p-6 bg-blue-50 border border-blue-200 rounded-lg">
                        <h3 className="font-semibold text-lg mb-4 text-blue-900">Configurar Google Authenticator</h3>

                        <div className="mb-4">
                            <p className="text-sm text-gray-700 mb-3">
                                1. Descarga Google Authenticator en tu móvil
                            </p>
                            <p className="text-sm text-gray-700 mb-3">
                                2. Escanea este código QR con la aplicación:
                            </p>
                        </div>

                        <div className="flex justify-center mb-4">
                            <div className="bg-white p-4 rounded-lg shadow-sm">
                                <QRCodeCanvas value={mfaSecret.qrCodeUrl} size={200} />
                            </div>
                        </div>

                        <div className="bg-white p-4 rounded-lg mb-4">
                            <p className="text-xs text-gray-600 mb-2">O ingresa este código manualmente:</p>
                            <p className="font-mono bg-gray-100 p-3 text-sm rounded border border-gray-300 break-all">
                                {mfaSecret.secret}
                            </p>
                        </div>

                        <div className="mt-4">
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                3. Ingresa el código de 6 dígitos de la app:
                            </label>
                            <div className="flex gap-2">
                                <input
                                    type="text"
                                    placeholder="000000"
                                    value={mfaCode}
                                    onChange={(e) => setMfaCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                                    className="flex-1 border border-gray-300 p-3 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    maxLength={6}
                                />
                                <button
                                    onClick={handleEnableMFA}
                                    disabled={mfaCode.length !== 6}
                                    className="bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition"
                                >
                                    Verificar
                                </button>
                                <button
                                    onClick={handleCancelSetup}
                                    className="bg-gray-500 text-white px-6 py-3 rounded-lg hover:bg-gray-600 transition"
                                >
                                    Cancelar
                                </button>
                            </div>
                        </div>
                    </div>
                )}

                {/* MFA Status */}
                {user.mfaEnabled && !showQR && (
                    <div className="mt-4 p-4 bg-green-50 border border-green-200 rounded-lg flex items-center gap-3">
                        <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        <div>
                            <p className="font-semibold text-green-800">MFA Activado</p>
                            <p className="text-sm text-green-700">Tu cuenta está protegida con autenticación de dos factores</p>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};
