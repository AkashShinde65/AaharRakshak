import { api, MOCKS_ENABLED } from './api.js';
import { complaints, trackingTimeline } from '../data/mockData.js';

async function computeChecksum(file) {
  const buffer = await file.arrayBuffer();
  const hashBuffer = await crypto.subtle.digest('SHA-256', buffer);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  return hashArray.map((b) => b.toString(16).padStart(2, '0')).join('');
}

async function toComplaintPayload(values) {
  const evidence = await Promise.all(
    (values.evidence || []).map(async (file) => ({
      type: file.type?.startsWith('image') ? 'PRODUCT_LABEL_PHOTO' : 'RECEIPT_PHOTO',
      objectKey: `frontend/mock/${file.name}`,
      originalFileName: file.name,
      contentType: file.type || 'application/octet-stream',
      sizeBytes: file.size,
      checksumSha256: await computeChecksum(file),
      capturedAt: new Date().toISOString(),
    }))
  );

  return {
    complaintType: values.complaintType,
    category: values.category,
    scannedBarcode: values.barcode || null,
    confirmedProductName: values.productName || null,
    confirmedCompanyName: values.companyName || null,
    confirmedFssaiLicenceNumber: values.fssaiLicenceNumber || null,
    confirmedBatchNumber: values.batchNumber || null,
    vendorName: values.vendorName || null,
    vendorAddress: values.address,
    description: values.description,
    location: {
      consentAccepted: values.gpsConsent,
      latitude: values.latitude ? Number(values.latitude) : null,
      longitude: values.longitude ? Number(values.longitude) : null,
      address: values.address,
    },
    evidence,
  };
}

export const complaintService = {
  async submitComplaint(values) {
    if (MOCKS_ENABLED) {
      return {
        ticketNumber: `ARK-DEMO-${Math.floor(Math.random() * 900000 + 100000)}`,
        status: 'SUBMITTED',
      };
    }

    const payload = await toComplaintPayload(values);
    const { data: draft } = await api.post('/citizen/complaints/drafts', payload);
    const draftId = draft.id || draft.complaintId;
    const { data } = await api.post(`/citizen/complaints/${draftId}/submit`);
    return data;
  },

  async trackComplaint(ticketNumber) {
    if (MOCKS_ENABLED) {
      return {
        ticketNumber,
        status: 'REPORT_PUBLISHED',
        timeline: trackingTimeline,
      };
    }

    const { data } = await api.get(`/public/transparency/complaints/${ticketNumber}/status`);
    return data;
  },

  async listMyComplaints() {
    if (MOCKS_ENABLED) return complaints;
    const { data } = await api.get('/citizen/complaints');
    return data;
  },
};
