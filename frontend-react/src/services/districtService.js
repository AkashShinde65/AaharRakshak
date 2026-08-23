import { api } from './api.js';

export const districtService = {
  async dashboard() {
    const { data } = await api.get('/official/investigations/dashboard');
    return data;
  },
  async complaintDetail(ticketNumber) {
    const { data } = await api.get(`/official/investigations/complaints/${ticketNumber}`);
    return data;
  },
  async verifyComplaint(ticketNumber) {
    const { data } = await api.post(`/official/investigations/complaints/${ticketNumber}/verify`);
    return data;
  },
  async assignInspector(ticketNumber, payload) {
    const { data } = await api.post(`/official/investigations/complaints/${ticketNumber}/assign-inspector`, payload);
    return data;
  },
  async assignLab(sampleId, payload) {
    const { data } = await api.post(`/official/investigations/samples/${sampleId}/assign-lab`, payload);
    return data;
  },
  async reviewReport(reportId, payload) {
    const { data } = await api.post(`/official/investigations/lab-reports/${reportId}/review`, payload);
    return data;
  },
  async publishReport(reportId, payload) {
    const { data } = await api.post(`/official/investigations/lab-reports/${reportId}/publish`, payload);
    return data;
  },
};
