import { api } from './api.js';

export const companyService = {
  async profile() {
    const { data } = await api.get('/company/profile');
    return data;
  },
  async updateProfile(payload) {
    const { data } = await api.put('/company/profile', payload);
    return data;
  },
  async licences() {
    const { data } = await api.get('/company/licences');
    return data;
  },
  async submitLicence(payload) {
    const { data } = await api.post('/company/licences', payload);
    return data;
  },
};
