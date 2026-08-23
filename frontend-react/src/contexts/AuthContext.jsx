import { useCallback, useMemo, useState } from 'react';
import { AuthContext } from './auth.context.js';
import { authService } from '../services/authService.js';
import { STORAGE_KEYS } from '../utils/constants.js';

function readStoredAuth() {
  try {
    return {
      token: localStorage.getItem(STORAGE_KEYS.accessToken),
      user: JSON.parse(localStorage.getItem(STORAGE_KEYS.user) || 'null'),
    };
  } catch {
    return { token: null, user: null };
  }
}

export function AuthProvider({ children }) {
  const stored = readStoredAuth();
  const [token, setToken] = useState(stored.token);
  const [user, setUser] = useState(stored.user);
  const [loading, setLoading] = useState(false);

  const persistSession = useCallback((payload) => {
    setToken(payload.accessToken);
    setUser(payload.user);
    localStorage.setItem(STORAGE_KEYS.accessToken, payload.accessToken);
    localStorage.setItem(STORAGE_KEYS.refreshToken, payload.refreshToken || '');
    localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(payload.user));
  }, []);

  const login = useCallback(
    async (credentials) => {
      setLoading(true);
      try {
        const payload = await authService.login(credentials);
        persistSession(payload);
        return payload;
      } finally {
        setLoading(false);
      }
    },
    [persistSession],
  );

  const register = useCallback(async (form) => authService.registerCitizen(form), []);
  const requestOtp = useCallback((identifier, channel) => authService.requestOtp(identifier, channel), []);
  const verifyOtp = useCallback((identifier, code, channel) => authService.verifyOtp(identifier, code, channel), []);

  const logout = useCallback(() => {
    setToken(null);
    setUser(null);
    localStorage.removeItem(STORAGE_KEYS.accessToken);
    localStorage.removeItem(STORAGE_KEYS.refreshToken);
    localStorage.removeItem(STORAGE_KEYS.user);
  }, []);

  const value = useMemo(
    () => ({
      user,
      token,
      loading,
      login,
      logout,
      register,
      requestOtp,
      verifyOtp,
      isAuthenticated: Boolean(token),
    }),
    [user, token, loading, login, logout, register, requestOtp, verifyOtp],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
