import { api, MOCKS_ENABLED } from './api.js';
import { demoUsers } from '../data/mockData.js';

function normalizeAuthResponse(data, identifier) {
  const roles = data.roles || data.user?.roles || [data.role || 'CITIZEN'];
  return {
    accessToken: data.accessToken || data.token || 'mock-access-token',
    refreshToken: data.refreshToken || '',
    user: {
      id: data.user?.id || data.userId || 1,
      name: data.user?.fullName || data.fullName || data.name || identifier,
      email: data.user?.email || data.email || identifier,
      role: Array.isArray(roles) ? roles[0] : roles,
    },
  };
}

function mockLogin(credentials) {
  const user = demoUsers.find(
    (item) => item.email === credentials.identifier && item.password === credentials.password,
  );
  if (!user) {
    throw new Error('Invalid demo credentials. Try citizen@aaharrakshak.dev / password.');
  }
  return {
    accessToken: `mock-${user.role.toLowerCase()}-token`,
    refreshToken: 'mock-refresh-token',
    user: {
      id: user.email,
      name: user.name,
      email: user.email,
      role: user.role,
    },
  };
}

export const authService = {
  async login(credentials) {
    if (MOCKS_ENABLED) return mockLogin(credentials);

    const { data } = await api.post('/auth/login', credentials);
    return normalizeAuthResponse(data, credentials.identifier);
  },

  async registerCitizen(form) {
    if (MOCKS_ENABLED) {
      return {
        message: 'Mock registration successful. Use OTP 123456 for the academic demo.',
        verificationToken: 'mock-otp-token',
      };
    }

    const payload = {
      fullName: form.fullName,
      email: form.email,
      mobileNumber: form.mobileNumber,
      password: form.password,
    };
    const { data } = await api.post('/auth/register/citizen', payload);
    return data;
  },

  async requestOtp(identifier, channel = 'EMAIL') {
    if (MOCKS_ENABLED) {
      return {
        destination: identifier,
        mockCode: '123456',
        message: 'Mock OTP generated for development.',
      };
    }

    const { data } = await api.post('/auth/otp/request', { identifier, channel });
    return data;
  },

  async verifyOtp(identifier, code, channel = 'EMAIL') {
    if (MOCKS_ENABLED) {
      if (code !== '123456') {
        throw new Error('Invalid mock OTP. Use 123456.');
      }
      return { status: 'VERIFIED' };
    }

    const { data } = await api.post('/auth/otp/verify', { identifier, channel, code });
    return data;
  },
};
