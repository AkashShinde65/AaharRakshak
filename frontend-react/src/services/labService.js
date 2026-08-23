import { api } from './api.js';

export const labService = {
  async assignedSamples() {
    const { data } = await api.get('/lab/investigations/samples/assigned');
    return data;
  },
  async receiveSample(sampleId, payload) {
    const { data } = await api.post(`/lab/investigations/samples/${sampleId}/received`, payload);
    return data;
  },
  async createReportDraft(sampleId, payload) {
    const { data } = await api.post(`/lab/investigations/samples/${sampleId}/reports/drafts`, payload);
    return data;
  },
  async submitReport(reportId) {
    const { data } = await api.post(`/lab/investigations/reports/${reportId}/submit`);
    return data;
  },
};
