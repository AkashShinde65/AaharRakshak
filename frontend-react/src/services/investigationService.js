import { api } from './api.js';

export const investigationService = {
  async assignedComplaints() {
    const { data } = await api.get('/official/investigations/assigned');
    return data;
  },

  async dashboard() {
    const { data } = await api.get('/official/investigations/dashboard');
    return data;
  },

  async complaintDetail(ticketNumber) {
    const { data } = await api.get(`/official/investigations/complaints/${ticketNumber}`);
    return data;
  },

  async scheduleInspection(ticketNumber, payload) {
    const { data } = await api.post(`/official/investigations/complaints/${ticketNumber}/inspections/schedule`, payload);
    return data;
  },

  async checkIn(inspectionId, payload) {
    const { data } = await api.post(`/official/investigations/inspections/${inspectionId}/check-in`, payload);
    return data;
  },

  async recordVisit(inspectionId, payload) {
    const { data } = await api.post(`/official/investigations/inspections/${inspectionId}/visit-record`, payload);
    return data;
  },

  async collectSample(inspectionId, payload) {
    const { data } = await api.post(`/official/investigations/inspections/${inspectionId}/samples`, payload);
    return data;
  },
};
