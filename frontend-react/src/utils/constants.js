export const STORAGE_KEYS = {
  accessToken: 'aaharrakshak.accessToken',
  refreshToken: 'aaharrakshak.refreshToken',
  user: 'aaharrakshak.user',
  theme: 'aaharrakshak.theme',
};

export const ROLES = {
  citizen: 'CITIZEN',
  company: 'COMPANY',
  inspector: 'FOOD_INSPECTOR',
  lab: 'LABORATORY_OFFICER',
  district: 'DISTRICT_ESCALATION_OFFICER',
  admin: 'CENTRAL_ADMINISTRATOR',
};

export const complaintStatuses = [
  'DRAFT',
  'SUBMITTED',
  'ASSIGNED',
  'INSPECTION_SCHEDULED',
  'SAMPLE_COLLECTED',
  'LAB_TESTING',
  'REPORT_PUBLISHED',
  'ACTION_TAKEN',
];
